package co.kr.compig.global.dto.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageResponse {

	private List<?> data;
	private int totalCount;

	public PageResponse(List<?> data, int total) {
		this.data = data;
		this.totalCount = total;
	}

	public static ResponseEntity<PageResponse> ok(List<? extends PagingResult> result) {
		int total = 0;

		if (CollectionUtils.isEmpty(result)) {
			result = new ArrayList<>();
		} else {
			total = result.get(0).getTotalCount();
			if (total == 0) {
				total = result.size();
			}
		}
		return ResponseEntity.ok(new PageResponse(result, total));
	}

	public static ResponseEntity<PageResponse> ok(List<? extends PagingResult> dataList, long offset, long total) {
		return ResponseEntity.ok(new PageResponse(convertDataList(dataList, offset), (int)total));
	}

	public static ResponseEntity<PageResponse> noResult() {
		return ResponseEntity.ok(new PageResponse(
			Collections.singletonList(CollectionUtils.emptyCollection()), 0));
	}

	private static List<?> convertDataList(List<?> dataList, long offset) {
		AtomicInteger atomicInteger = new AtomicInteger((int)(offset + 1));

		return dataList.stream().map(data -> {
			if (data instanceof PagingResult) {
				((PagingResult)data).setRowNum(atomicInteger.getAndIncrement());
			}

			return data;
		}).collect(Collectors.toList());
	}
}
