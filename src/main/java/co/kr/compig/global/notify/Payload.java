package co.kr.compig.global.notify;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
	private String channel;
	@Builder.Default
	private String username = "Compig";
	private List<Attachment> attachments;
}
