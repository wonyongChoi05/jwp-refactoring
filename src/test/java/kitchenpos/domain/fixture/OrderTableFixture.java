package kitchenpos.domain.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTable 주문_테이블_생성() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(false);
        return orderTable;
    }

}