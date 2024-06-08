package co.kr.compig.global.dto.pagination;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PageableRequest extends PagingRequest {

	private String cursorId; // 커서 id
	private String keywordType; // 검색조건
	private String keyword;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDateTime fromCreatedOn;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDateTime toCreatedOn;
}
