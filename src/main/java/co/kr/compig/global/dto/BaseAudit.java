package co.kr.compig.global.dto;

import java.time.LocalDateTime;

import co.kr.compig.global.dto.pagination.PagingResult;
import co.kr.compig.global.embedded.Created;
import co.kr.compig.global.embedded.CreatedAndUpdated;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BaseAudit extends PagingResult {

	private String createdByName; // 등록자 아이디
	private String createdByUserId; // 등록자 아이디
	private LocalDateTime createdOn; // 등록일시
	private String updatedByName; // 수정자 아이디
	private String updatedByUserId; // 수정자 아이디
	private LocalDateTime updatedOn; // 수정일시

	public void setCreatedAndUpdated(CreatedAndUpdated createdAndUpdated) {
		if (createdAndUpdated != null) {
			if (createdAndUpdated.getCreatedBy() != null) {
				try {
					this.createdByName = createdAndUpdated.getCreatedBy().getUserNm();
					this.createdByUserId = createdAndUpdated.getCreatedBy().getUserId();
				} catch (EntityNotFoundException e) {
					this.createdByName = null;
					this.createdByUserId = null;
				}
			}
			this.createdOn = createdAndUpdated.getCreatedOn();
			if (createdAndUpdated.getUpdatedBy() != null) {
				try {
					this.updatedByName = createdAndUpdated.getUpdatedBy().getUserNm();
					this.updatedByUserId = createdAndUpdated.getUpdatedBy().getUserId();
				} catch (EntityNotFoundException e) {
					this.updatedByName = null;
					this.updatedByUserId = null;
				}
			}
			this.updatedOn = createdAndUpdated.getUpdatedOn();
		}
	}

	public void setCreated(Created created) {
		if (created != null) {
			if (created.getCreatedBy() != null) {
				try {
					this.createdByName = created.getCreatedBy().getUserNm();
					this.createdByUserId = created.getCreatedBy().getUserId();
				} catch (EntityNotFoundException e) {
					this.createdByName = null;
					this.createdByUserId = null;
				}
			}
			this.createdOn = created.getCreatedOn();
		}
	}

}

