package parentPackage.implementation.jdbc.service;

import org.springframework.stereotype.Service;
import parentPackage.api.OrderService;
import parentPackage.api.exception.BadRequestException;
import parentPackage.api.request.AddToOrderRequest;
import parentPackage.api.request.ProductAmountRequest;
import parentPackage.domain.OrderResponse;
import parentPackage.domain.ShoppingListItem;
import parentPackage.implementation.jdbc.repository.OrderJdbcRepository;
import parentPackage.implementation.jdbc.repository.ProductJdbcRepository;

import java.util.List;

@Service
public class OrderServiceJdbcImpl implements OrderService {
    private final OrderJdbcRepository orderJdbcRepository;
    private final ProductJdbcRepository productJdbcRepository;

    public OrderServiceJdbcImpl(OrderJdbcRepository orderJdbcRepository, ProductJdbcRepository productJdbcRepository) {
        this.orderJdbcRepository = orderJdbcRepository;
        this.productJdbcRepository = productJdbcRepository;
    }

    @Override
    public OrderResponse get(long id) {
        return this.orderJdbcRepository.getById(id);
    }

    @Override
    public OrderResponse create() {
        return this.orderJdbcRepository.create();
    }

    @Override
    public String pay(long id) {
        if (this.get(id) != null) {
            return this.calculateOrderPrice(this.orderJdbcRepository.payOder(id));
        }
        return null;
    }

    @Override
    public OrderResponse addProduct(long id, AddToOrderRequest request) {
        long availableAmount = this.productJdbcRepository.getAmount(request.getProductId()).getAmount();
        long amount = request.getAmount();

        if (amount == 0) {
            throw new BadRequestException("Amount cannot be 0!");
        } else if (this.get(id) != null && this.productJdbcRepository.getById(request.getProductId()) != null &&
                availableAmount >= amount) {

            if (this.get(id).isPaid()) {
                throw new BadRequestException("Order " + id + " is already paid. You cannot change products!");
            }

            this.productJdbcRepository.addAmount(request.getProductId(), new ProductAmountRequest(-amount));
            List<ShoppingListItem> oderShoppingListItem = this.orderJdbcRepository.getShoppingList(id);

            for (ShoppingListItem shoppingListItem : oderShoppingListItem) {
                if (shoppingListItem.getProductId() == request.getProductId()) {
                    request.setAmount(amount + shoppingListItem.getAmount());
                    return this.orderJdbcRepository.addAmount(id, request);
                }
            }

            if (amount > 0) {
                return this.orderJdbcRepository.addProduct(id, request);
            }
        }

        throw new BadRequestException("Not enough amount of product " + request.getProductId() + "!");
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            if (!this.get(id).isPaid()) {
                List<ShoppingListItem> orderShoppingListItems = this.orderJdbcRepository.getShoppingList(id);
                for (ShoppingListItem shoppingListItem : orderShoppingListItems) {
                    this.productJdbcRepository.addAmount(shoppingListItem.getProductId(),
                            new ProductAmountRequest(shoppingListItem.getAmount()));
                }
            }
            this.orderJdbcRepository.deleteOrder(id);
        }
    }

    private String calculateOrderPrice(OrderResponse orderResponse) {
        double totalPrice = 0;
        for (ShoppingListItem shoppingListItem : orderResponse.getShoppingList()) {
            totalPrice += shoppingListItem.getAmount() * this.productJdbcRepository.getById(shoppingListItem.getProductId()).getPrice();
        }
        return String.format("%.1f", totalPrice);
    }
}
