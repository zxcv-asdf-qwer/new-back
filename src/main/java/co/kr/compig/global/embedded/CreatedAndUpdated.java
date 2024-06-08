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
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Data
@Embeddable
public class CreatedAndUpdated {

	/**********************************************
	 * Default columns
	 **********************************************/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", updatable = false)
	private Member createdBy; // 등록자 객체

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by")
	private Member updatedBy; // 수정자 객체

	@Column(updatable = false)
	@ColumnDefault("CURRENT_TIMESTAMP")
	private LocalDateTime createdOn; // 등록일시

	@ColumnDefault("CURRENT_TIMESTAMP")
	private LocalDateTime updatedOn; // 수정일시

	@PrePersist
	public void prePersist() {
		if (ObjectUtils.allNull(createdBy)) {
			createdBy = SecurityUtil.getCurrentMember();
		}
		if (createdOn == null) {
			createdOn = LocalDateTime.now();
		}

		this.preUpdate();
	}

	@PreUpdate
	public void preUpdate() {
		updatedBy = SecurityUtil.getCurrentMember();
		updatedOn = LocalDateTime.now();
	}

	public CreatedAndUpdated() {
	}

	public CreatedAndUpdated(Member createdBy, LocalDateTime createdOn, Member updatedBy,
		LocalDateTime updatedOn) {
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
	}

	public String getCreatedOnString() {
		return this.createdOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public String getUpdatedOnString() {
		return this.updatedOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
