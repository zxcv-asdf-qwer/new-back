package co.kr.compig.api.application.log;

import static co.kr.compig.global.aspect.LogDataConverter.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.ContentCachingRequestWrapper;

import co.kr.compig.api.domain.log.InboundApi;
import co.kr.compig.api.domain.log.InboundApiRepository;
import co.kr.compig.global.code.LogType;
import co.kr.compig.global.utils.WebUtil;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * API Log 처리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiLogService {

	private final InboundApiRepository inboundApiRepository;

	@Transactional
	public void inboundSave(Tracer tracer, ApplicationContext context, Instant start, Instant end,
		LocalDateTime createdOn, ContentCachingRequestWrapper request, Object responseData, ProceedingJoinPoint pjp,
		String error) {
		try {

			inboundApiRepository.save(InboundApi.builder()
				.logType(LogType.INBOUND)
				.traceId(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
				.spanId(Objects.requireNonNull(tracer.currentSpan()).context().spanId())
				.service(context.getId())
				.processTime(Duration.between(start, end).toMillis())
				.createdOn(createdOn)
				.path(request.getRequestURI())
				.method(request.getMethod())
				.requestParam(getUrlParameter(request))
				.requestData(dataConverter(requestData(pjp, request)))
				.responseData(dataConverter(responseData))
				.error(error)
				.userId(request.getRemoteUser())
				.remoteAddress(WebUtil.getClientIp(request))
				.agent(WebUtil.getAgentCode(request))
				.build());
		} catch (Exception e) {
			//log는 db 에러나도 exception 처리 X
			log.error("CommonLogService insertCommonLogging Exception - {} \n {}", e, request.getRequestURI());
		}

	}
}
