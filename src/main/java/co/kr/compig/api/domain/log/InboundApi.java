package co.kr.compig.api.domain.log;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import co.kr.compig.global.code.AgentCode;
import co.kr.compig.global.code.LogType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@SequenceGenerator(
	name = "inbound_api_seq_gen", //시퀀스 제너레이터 이름
	sequenceName = "inbound_api_seq", //시퀀스 이름
	initialValue = 1, //시작값
	allocationSize = 1 //메모리를 통해 할당 할 범위 사이즈
)
public class InboundApi {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inbound_api_seq_gen")
	@Column(name = "inbound_api_id")
	private Long id;

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	private LogType logType;

	@Column(length = 100, nullable = false)
	private String traceId;

	@Column(length = 100, nullable = false)
	private String spanId;

	@Column(nullable = false)
	private Long processTime;

	@Column(length = 50, nullable = false)
	private String service;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String path;

	@Column(length = 6, nullable = false)
	private String method;

	@Column(columnDefinition = "TEXT")
	private String requestParam;

	@Column(columnDefinition = "TEXT")
	private String requestData;

	@Column(columnDefinition = "TEXT")
	private String responseData;

	@Column(columnDefinition = "TEXT")
	private String error;

	@Column(nullable = false)
	@ColumnDefault("'anonymous'")
	@Builder.Default
	private String userId = "anonymous";

	@Column(length = 20, nullable = false)
	private String remoteAddress;

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	private AgentCode agent;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdOn;

    /* =================================================================
     * Relation method
     ================================================================= */


    /* =================================================================
     * Business login
     ================================================================= */

}
