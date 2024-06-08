package co.kr.compig.global.notify;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attachment {
	List<Field> fields;
}
