package co.kr.compig.api.application.menu;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.compig.api.domain.menu.Menu;
import co.kr.compig.api.domain.menu.MenuMapper;
import co.kr.compig.api.domain.menu.MenuRepository;
import co.kr.compig.api.presentation.menu.model.MenuTree;
import co.kr.compig.api.presentation.menu.request.MenuCreateRequest;
import co.kr.compig.api.presentation.menu.request.MenuUpdateRequest;
import co.kr.compig.api.presentation.menu.response.MenuDetailResponse;
import co.kr.compig.global.error.exception.NotExistDataException;
import co.kr.compig.global.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

	private final MenuRepository menuRepository;
	private final MenuMapper menuMapper;

	public Long createMenu(MenuCreateRequest menuCreateRequest) {
		Menu menu = menuCreateRequest.converterEntity();
		return menuRepository.save(menu).getId();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("rawtypes")
	public List<MenuTree> menuList() {
		List<MenuTree> menuTrees = menuMapper.selectMenuTree(SecurityUtil.getMemberId());
		return menuTrees.stream()
			.map(
				menuTree -> {
					String[] split = menuTree.getPath().replaceAll("[{|\\\"|}]", "").split(",");
					menuTree.setPath(split[0]);
					return menuTree;
				})
			.sorted(Comparator.comparing(menuTree -> Integer.parseInt(menuTree.getPath())))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public MenuDetailResponse getMenu(Long menuId) {
		Menu menu = menuRepository.findById(menuId).orElseThrow(NotExistDataException::new);
		return menu.toMenuDetailResponse();
	}

	public Long updateMenu(Long menuId, MenuUpdateRequest menuUpdateRequest) {
		Menu menu = menuRepository.findById(menuId).orElseThrow(NotExistDataException::new);
		Menu parent = null;
		if (menuUpdateRequest.getParentMenuId() != null) {
			parent = menuRepository.findById(menuUpdateRequest.getParentMenuId())
				.orElseThrow(NotExistDataException::new);
		}
		menu.update(menuUpdateRequest, parent);
		return menu.getId();
	}

	public void deleteMenu(Long menuId) {
		Menu menu = menuRepository.findById(menuId).orElseThrow(NotExistDataException::new);
		menuRepository.delete(menu);
	}

	@Transactional(readOnly = true)
	public Menu getMenuById(Long menuId) {
		return menuRepository.findById(menuId).orElseThrow(NotExistDataException::new);
	}
}
