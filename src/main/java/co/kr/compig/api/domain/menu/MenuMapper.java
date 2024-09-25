package co.kr.compig.api.domain.menu;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import co.kr.compig.api.presentation.menu.model.MenuTree;

@Mapper
public interface MenuMapper {
	List<MenuTree> selectMenuTree(String memberId);

}
