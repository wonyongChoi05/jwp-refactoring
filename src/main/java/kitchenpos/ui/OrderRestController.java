package kitchenpos.ui;

import kitchenpos.application.OrderService;
import kitchenpos.domain.OrderStatus;
import kitchenpos.dto.response.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/orders/{orderTableId}")
    public ResponseEntity<Void> create(@PathVariable Long orderTableId) {
        final Long orderId = orderService.create(orderTableId);
        final URI uri = URI.create("/api/orders/" + orderId);
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/api/orders")
    public ResponseEntity<List<OrderResponse>> list() {
        return ResponseEntity.ok()
                .body(orderService.list());
    }

    @PutMapping("/api/orders/{orderId}/order-status")
    public ResponseEntity<OrderResponse> changeOrderStatus(
            @PathVariable final Long orderId,
            @RequestParam final OrderStatus orderStatus
    ) {
        return ResponseEntity.ok(orderService.changeOrderStatus(orderId, orderStatus));
    }
}
