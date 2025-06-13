package parentPackage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import parentPackage.api.OrderService;
import parentPackage.api.request.AddToOrderRequest;
import parentPackage.domain.OrderResponse;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.orderService.get(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create() {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.create());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        this.orderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<String> pay(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.orderService.pay(id));
    }

    @PostMapping("/{id}/add")
    public ResponseEntity<OrderResponse> addProduct(@PathVariable("id") long id, @RequestBody AddToOrderRequest request) {
        return ResponseEntity.ok().body(this.orderService.addProduct(id, request));
    }
}
