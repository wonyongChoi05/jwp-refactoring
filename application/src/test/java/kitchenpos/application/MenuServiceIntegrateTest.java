package kitchenpos.application;

import kitchenpos.execute.ServiceIntegrateTest;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.menu.application.MenuService;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.repository.MenuRepository;
import kitchenpos.menu.dto.request.MenuCreateRequest;
import kitchenpos.menu.dto.response.MenuResponse;
import kitchenpos.menu_group.domain.MenuGroup;
import kitchenpos.menu_group.domain.repository.MenuGroupRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuServiceIntegrateTest extends ServiceIntegrateTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Nested
    class 메뉴를_생성한다 {

        @Test
        void 메뉴를_생성한다() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.인기_메뉴_생성());
            MenuCreateRequest request = new MenuCreateRequest("치킨", BigDecimal.valueOf(20000), menuGroup.getId());

            // when, then
            Assertions.assertDoesNotThrow(() -> menuService.create(request));
        }

        @Test
        void 메뉴의_가격이_null이면_예외가_발생한다() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.인기_메뉴_생성());
            MenuCreateRequest request = new MenuCreateRequest("치킨", null, menuGroup.getId());

            // when, then
            Assertions.assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @Test
        void 메뉴의_가격이_0보다_작으면_예외가_발생한다() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.인기_메뉴_생성());
            MenuCreateRequest request = new MenuCreateRequest("치킨", BigDecimal.valueOf(-100), menuGroup.getId());

            // when, then
            Assertions.assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

    }

    @Nested
    class 메뉴_목록을_조회한다 {

        @BeforeEach
        void setUp() {
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.인기_메뉴_생성());
            Menu menu = new Menu("치킨", BigDecimal.valueOf(20000), menuGroup.getId());
            menuRepository.save(menu);
        }

        @Test
        void 메뉴_목록을_조회한다() {
            // when
            List<MenuResponse> menus = menuService.list();

            // then
            Assertions.assertAll(
                    () -> assertThat(menus).hasSize(1),
                    () -> assertThat(menus).extracting(MenuResponse::getName)
                            .contains("치킨")
            );
        }

    }

}
