package co.kr.compig.global.dto.pagination;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import lombok.Getter;

@Getter
public class SliceResponse<T> implements Serializable {

	private final List<T> data;

	private final boolean hasNext;

	private final String lastCursorId;

	//기본 생성자 호출시 empty list
	public SliceResponse() {
		this.data = Collections.emptyList();
		this.hasNext = false;
		this.lastCursorId = null;
	}

	public SliceResponse(List<T> content, Pageable pageable, boolean hasNext) {
		final SliceImpl<T> slice = new SliceImpl<>(content, pageable, hasNext);

		this.data = slice.getContent();
		this.hasNext = slice.hasNext();
		this.lastCursorId = null;
	}

	public SliceResponse(List<T> content, Pageable pageable, boolean hasNext, String lastCursorId) {
		final SliceImpl<T> slice = new SliceImpl<>(content, pageable, hasNext);

		this.data = slice.getContent();
		this.hasNext = slice.hasNext();
		this.lastCursorId = lastCursorId;
	}

	public SliceResponse(SliceImpl<T> slice) {
		this.data = slice.getContent();
		this.hasNext = slice.hasNext();
		this.lastCursorId = null;
	}

}

