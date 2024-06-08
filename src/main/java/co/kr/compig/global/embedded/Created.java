package co.kr.compig.global.embedded;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.ColumnDefault;

import co.kr.compig.api.domain.member.Member;
import co.kr.compig.global.utils.SecurityUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
@Embeddable
public class Created {
	/**********************************************
	 * Default columns
	 **********************************************/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", updatable = false)
	private Member createdBy; // 등록자 객체

	@Column(updatable = false)
	@ColumnDefault("CURRENT_TIMESTAMP")
	private LocalDateTime createdOn; // 등록일시

	@PrePersist
	public void prePersist() {
		if (ObjectUtils.allNull(createdBy)) {
			createdBy = SecurityUtil.getCurrentMember();
		}
		if (createdOn == null) {
			createdOn = LocalDateTime.now();
		}
	}

	public Created() {
	}

	public Created(Member createdBy, LocalDateTime createdOn) {
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}

	public String getCreatedOnString() {
		return this.createdOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
