package kitchenpos.domain.fixture;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

    public static MenuGroup 인기_메뉴_생성() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(null);
        menuGroup.setName("인기 메뉴");
        return menuGroup;
    }

}
