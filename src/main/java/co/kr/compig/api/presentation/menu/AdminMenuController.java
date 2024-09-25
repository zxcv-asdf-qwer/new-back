package co.kr.compig.api.presentation.menu;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.compig.api.application.menu.MenuService;
import co.kr.compig.api.presentation.menu.model.MenuTree;
import co.kr.compig.api.presentation.menu.request.MenuCreateRequest;
import co.kr.compig.api.presentation.menu.request.MenuUpdateRequest;
import co.kr.compig.api.presentation.menu.response.MenuDetailResponse;
import co.kr.compig.global.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "관리자 메뉴", description = "메뉴 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/menu", produces = "application/json")
public class AdminMenuController {

	private final MenuService menuService;

	@Operation(summary = "생성하기", hidden = true)
	@PostMapping
	public ResponseEntity<Response<?>> createMenu(
		@RequestBody @Valid MenuCreateRequest menuCreateRequest
	) {
		return ResponseEntity.ok().body(Response.<Map<String, Long>>builder()
			.data(Map.of("menuId", menuService.createMenu(menuCreateRequest)))
			.build());
	}

	@Operation(summary = "조회")
	@GetMapping
	public ResponseEntity<Response<List<MenuTree>>> menuList() {
		return ResponseEntity.ok(Response.<List<MenuTree>>builder()
			.data(menuService.menuList())
			.build());
	}

	@Operation(summary = "상세 조회", hidden = true)
	@GetMapping(path = "/{menuId}")
	public ResponseEntity<Response<MenuDetailResponse>> getMenu(
		@PathVariable(name = "menuId") Long menuId
	) {
		return ResponseEntity.ok(Response.<MenuDetailResponse>builder()
			.data(menuService.getMenu(menuId))
			.build());
	}

	@Operation(summary = "정보 수정하기", hidden = true)
	@PutMapping(path = "/{menuId}")
	public ResponseEntity<Response<?>> updateMenu(
		@PathVariable(name = "menuId") Long menuId,
		@RequestBody @Valid MenuUpdateRequest menuUpdateRequest) {
		return ResponseEntity.ok().body(Response.<Map<String, Long>>builder()
			.data(Map.of("menuId", menuService.updateMenu(menuId, menuUpdateRequest)))
			.build());
	}

	@Operation(summary = "삭제", hidden = true)
	@DeleteMapping(path = "/{menuId}")
	public ResponseEntity<Response<?>> deleteMenu(
		@PathVariable(name = "menuId") Long menuId) {
		menuService.deleteMenu(menuId);
		return ResponseEntity.ok().build();
	}
}
