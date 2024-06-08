package co.kr.compig.global.dto.pagination.nouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageInfo {

	private int nowPage, startPage, endPage, total, cntPerPage, lastPage;
	private int cntPage = 10;

	//PageInfo(result.get(0).getTotalCount(), result.get(0).getPageStart(), result.get(0).getPageLength());
	public PageInfo(int total, int nowPage, int cntPerPage) {
		setNowPage(nowPage);
		setCntPerPage(cntPerPage);
		setTotal(total);
		calcLastPage(getTotal(), getCntPerPage());
		calcStartEndPage(getNowPage(), cntPage);
	}

	// 제일 마지막 페이지 계산
	private void calcLastPage(int total, int cntPerPage) {
		setLastPage((int)Math.ceil((double)total / (double)cntPerPage));
	}

	// 시작, 끝 페이지 계산
	private void calcStartEndPage(int nowPage, int cntPage) {
		setEndPage(((int)Math.ceil(((double)nowPage < (double)cntPage / 2 ? cntPage
			: (double)nowPage + ((double)cntPage / 2)))));
		if (getLastPage() < getEndPage()) {
			setEndPage(getLastPage());
		}
		setStartPage(((int)Math.ceil((double)nowPage - ((double)cntPage / 2))));
		if (getStartPage() < 1) {
			setStartPage(1);
		}
	}
}
