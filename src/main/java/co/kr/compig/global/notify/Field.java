package co.kr.compig.global.notify;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Field {
	private String title;
	private String value;
}
