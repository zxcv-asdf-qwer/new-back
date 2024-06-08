package co.kr.compig.global.utils;

import static org.springframework.context.i18n.LocaleContextHolder.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageUtil {
	private MessageSource messageSource;

	public String getMessage(String code, Object... args) {
		List<Object> objectList = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(args)) {
			objectList.addAll(Arrays.asList(args));
		}
		return messageSource.getMessage(code, objectList.toArray(), code, getLocale());
	}

}
