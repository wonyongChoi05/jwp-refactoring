package kitchenpos.order_table.application;

import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order_table.domain.OrderTable;
import kitchenpos.order_table.domain.repository.OrderTableRepository;
import kitchenpos.order_table.dto.response.OrderTableResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderTableService {

    private final OrderTableRepository orderTableRepository;

    public OrderTableService(OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public Long create(final int numberOfGuests, final boolean empty) {
        OrderTable orderTable = new OrderTable(numberOfGuests, empty);
        return orderTableRepository.save(orderTable).getId();
    }

    @Transactional(readOnly = true)
    public List<OrderTableResponse> findAll() {
        return orderTableRepository.findAll().stream()
                .map(OrderTableResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeIsEmpty(final Long orderTableId, final boolean isEmpty) {
        final OrderTable savedOrderTable = orderTableRepository.getById(orderTableId);
        validateOrderStatusIsCookingAndMeal(orderTableId);
        savedOrderTable.updateEmpty(isEmpty);
    }

    private void validateOrderStatusIsCookingAndMeal(Long orderTableId) {
        if (orderTableRepository.existsByOrderTableIdAndOrderStatusIn(
                orderTableId, Arrays.asList(OrderStatus.COOKING, OrderStatus.MEAL))) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public OrderTable changeNumberOfGuests(final Long orderTableId, final int numberOfGuests) {
        OrderTable orderTable = orderTableRepository.getById(orderTableId);
        orderTable.validateNumberOfGuests();
        orderTable.validateIsEmpty();
        orderTable.updateNumberOfGuests(numberOfGuests);
        return orderTableRepository.save(orderTable);
    }

}
