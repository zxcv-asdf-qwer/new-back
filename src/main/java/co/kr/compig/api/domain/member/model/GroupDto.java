package co.kr.compig.api.domain.member.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class GroupDto implements Serializable {

	private static final long serialVersionUID = 5807623857923618922L;

	private String groupNm;
	private String groupKey;
	private String groupPath;

}
