package kitchenpos.application;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.domain.repository.OrderRepository;
import kitchenpos.domain.repository.OrderTableRepository;
import kitchenpos.domain.repository.TableGroupRepository;
import kitchenpos.dto.response.OrderTableResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderStatus.COOKING;

@Service
public class OrderTableService {

    private final TableGroupRepository tableGroupRepository;
    private final OrderTableRepository orderTableRepository;
    private final OrderRepository orderRepository;

    public OrderTableService(TableGroupRepository tableGroupRepository, OrderTableRepository orderTableRepository, OrderRepository orderRepository) {
        this.tableGroupRepository = tableGroupRepository;
        this.orderTableRepository = orderTableRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Long create(final Long tableGroupId, final int numberOfGuests, final List<Long> orderIds) {
        TableGroup tableGroup = tableGroupRepository.getById(tableGroupId);
        List<Order> orders = orderRepository.findAllByIdIn(orderIds);
        OrderTable orderTable = new OrderTable(tableGroup, orders, numberOfGuests);
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
        orderTableRepository.save(savedOrderTable);
    }

    private void validateOrderStatusIsCookingAndMeal(Long orderTableId) {
        if (orderTableRepository.existsByOrderTableIdAndOrderStatusIn(
                orderTableId, Arrays.asList(COOKING, MEAL))) {
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
