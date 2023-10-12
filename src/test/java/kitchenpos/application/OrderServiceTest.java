package kitchenpos.application;

import kitchenpos.application.test.ServiceUnitTest;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static kitchenpos.domain.OrderStatus.COMPLETION;
import static kitchenpos.domain.OrderStatus.COOKING;
import static kitchenpos.domain.fixture.OrderFixture.주문_생성;
import static kitchenpos.domain.fixture.OrderLineItemFixture.주문_항목_생성;
import static kitchenpos.domain.fixture.OrderTableFixture.주문_테이블_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class OrderServiceTest extends ServiceUnitTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    private Order order;
    private OrderLineItem oli;

    @BeforeEach
    void setUp() {
        oli = 주문_항목_생성(1L, 1L);
        this.order = 주문_생성(1L, List.of(oli));
    }
    @Nested
    class 주문을_생성한다 {

        @Test
        void 주문을_생성한다() {
            // given
            when(menuDao.countByIdIn(List.of(1L))).thenReturn(1L);
            when(orderTableDao.findById(1L)).thenReturn(Optional.of(주문_테이블_생성()));
            when(orderDao.save(order)).thenReturn(order);
            when(orderLineItemDao.save(oli)).thenReturn(oli);

            // when, then
            assertDoesNotThrow(() -> orderService.create(order));
        }

        @Test
        void 주문에_주문_항목이_존재하지_않을경우_예외가_발생한다() {
            // given
            order.setOrderLineItems(Collections.EMPTY_LIST);

            // when, then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @Test
        void 주문_항목의_메뉴_개수와_주문한_메뉴의_개수가_다를_경우_예외가_발생한다() {
            // given
            order.setOrderLineItems(List.of(주문_항목_생성(99L, 1L)));

            // when, then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @Test
        void 주문_테이블이_존재하지_않는다면_예외가_발생한다() {
            // given
            when(menuDao.countByIdIn(List.of(1L))).thenReturn(1L);
            when(orderTableDao.findById(1L)).thenReturn(Optional.ofNullable(null));

            // when, then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @Test
        void 주문_테이블이_비어있다면_예외가_발생한다() {
            // given
            OrderTable 주문_테이블 = 주문_테이블_생성();
            주문_테이블.setEmpty(true);
            when(menuDao.countByIdIn(List.of(1L))).thenReturn(1L);
            when(orderTableDao.findById(1L)).thenReturn(Optional.ofNullable(주문_테이블));

            // when, then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @Test
        void OrderTableId를_설정한다() {
            // given
            OrderTable 주문_테이블 = 주문_테이블_생성();
            주문_테이블.setId(1L);
            when(menuDao.countByIdIn(List.of(1L))).thenReturn(1L);
            when(orderTableDao.findById(1L)).thenReturn(Optional.of(주문_테이블));
            when(orderDao.save(order)).thenReturn(order);
            when(orderLineItemDao.save(oli)).thenReturn(oli);

            // when
            Order order = orderService.create(OrderServiceTest.this.order);

            // then
            assertThat(order.getOrderTableId()).isEqualTo(1L);
        }

        @Test
        void OrderStatus를_COOKING으로_설정한다() {
            // given
            OrderTable 주문_테이블 = 주문_테이블_생성();
            주문_테이블.setId(1L);
            when(menuDao.countByIdIn(List.of(1L))).thenReturn(1L);
            when(orderTableDao.findById(1L)).thenReturn(Optional.of(주문_테이블));
            when(orderDao.save(order)).thenReturn(order);
            when(orderLineItemDao.save(oli)).thenReturn(oli);

            // when
            Order order = orderService.create(OrderServiceTest.this.order);

            // then
            assertThat(order.getOrderStatus()).isEqualTo(COOKING.name());
        }

        @Test
        void OrderedTime을_설정한다() {
            // given
            OrderTable 주문_테이블 = 주문_테이블_생성();
            주문_테이블.setId(1L);
            when(menuDao.countByIdIn(List.of(1L))).thenReturn(1L);
            when(orderTableDao.findById(1L)).thenReturn(Optional.of(주문_테이블));
            when(orderDao.save(order)).thenReturn(order);
            when(orderLineItemDao.save(oli)).thenReturn(oli);

            // when
            Order order = orderService.create(OrderServiceTest.this.order);

            // then
            assertThat(order.getOrderedTime()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        void 저장한_주문_항목들을_반환한다() {
            // given
            OrderTable 주문_테이블 = 주문_테이블_생성();
            주문_테이블.setId(1L);
            when(menuDao.countByIdIn(List.of(1L))).thenReturn(1L);
            when(orderTableDao.findById(1L)).thenReturn(Optional.of(주문_테이블));
            when(orderDao.save(order)).thenReturn(order);
            when(orderLineItemDao.save(oli)).thenReturn(oli);

            // when
            Order order = orderService.create(OrderServiceTest.this.order);

            // then
            assertThat(order).isNotNull();
        }

    }

    @Nested
    class 주문_목록을_반환한다 {

        @Test
        void 주문_목록을_반환한다() {
            // given
            when(orderDao.findAll()).thenReturn(List.of(order));

            // when
            List<Order> orders = orderService.list();

            // then
            assertAll(
                    () -> assertThat(orders).hasSize(1),
                    () -> assertThat(orders.get(0)).isEqualTo(order)
            );
        }

    }

    @Nested
    class 주문_상태를_변경한다 {

        @Test
        void 주문_상태를_변경한다() {
            // given
            order.setId(1L);
            order.setOrderStatus(COOKING.name());
            when(orderDao.findById(order.getId())).thenReturn(Optional.ofNullable(order));
            when(orderLineItemDao.findAllByOrderId(order.getId())).thenReturn(List.of(주문_항목_생성(1L, 1L)));

            // when
            Order savedOrder = orderService.changeOrderStatus(order.getId(), OrderServiceTest.this.order);

            // then
            assertThat(savedOrder).isEqualTo(order);
        }

        @Test
        void 입력받은_orderId가_존재하지_않는다면_예외가_발생한다() {
            // given
            order.setId(1L);
            order.setOrderStatus(COOKING.name());

            // when, then
            assertThrows(IllegalArgumentException.class,
                    () -> orderService.changeOrderStatus(10L, OrderServiceTest.this.order));
        }

        @Test
        void 저장된_주문이_이미_완료되었다면_예외가_발생한다() {
            // given
            order.setId(1L);
            order.setOrderStatus(COMPLETION.name());
            when(orderDao.findById(order.getId())).thenReturn(Optional.ofNullable(order));

            // when
            assertThrows(IllegalArgumentException.class,
                    () -> orderService.changeOrderStatus(order.getId(), OrderServiceTest.this.order));
        }

    }

}