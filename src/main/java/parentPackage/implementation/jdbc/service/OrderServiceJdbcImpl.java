package parentPackage.implementation.jdbc.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import parentPackage.api.OrderService;
import parentPackage.api.exception.BadRequestException;
import parentPackage.dto.request.AddToOrderRequest;
import parentPackage.dto.request.ProductAmountRequest;
import parentPackage.dto.response.OrderResponse;
import parentPackage.dto.response.ShoppingListItem;
import parentPackage.implementation.jdbc.repository.OrderJdbcRepository;
import parentPackage.implementation.jdbc.repository.OrderProductJdbcRepository;
import parentPackage.implementation.jdbc.repository.ProductJdbcRepository;

import java.util.List;

@Service
@Profile("jdbc")
public class OrderServiceJdbcImpl implements OrderService {
    private final OrderJdbcRepository orderJdbcRepository;
    private final ProductJdbcRepository productJdbcRepository;
    private final OrderProductJdbcRepository orderProductJdbcRepository;

    public OrderServiceJdbcImpl(OrderJdbcRepository orderJdbcRepository, ProductJdbcRepository productJdbcRepository, OrderProductJdbcRepository orderProductJdbcRepository) {
        this.orderJdbcRepository = orderJdbcRepository;
        this.productJdbcRepository = productJdbcRepository;
        this.orderProductJdbcRepository = orderProductJdbcRepository;
    }

    @Override
    public OrderResponse get(long id) {
        OrderResponse orderResponse = this.orderJdbcRepository.getById(id);
        orderResponse.updateShoppingList(this.orderProductJdbcRepository.getShoppingList(orderResponse.getId()));
        return orderResponse;
    }

    @Override
    public OrderResponse add() {
        return this.get(this.orderJdbcRepository.add());
    }

    @Override
    public String pay(long id) {
        if (this.get(id) != null) {
            if (this.get(id).isPaid()) {
                throw new BadRequestException("Order " + id + " is already paid!");
            } else {
                this.orderJdbcRepository.editOrder(id, true);
                return this.calculateOrderPrice(this.get(id));
            }
        }
        return null;
    }

    @Override
    public OrderResponse addProduct(long id, AddToOrderRequest request) {
        long availableAmount = this.productJdbcRepository.getById(request.getProductId()).getAmount();
        long amount = request.getAmount();
        OrderResponse orderResponse = this.get(id);

        if (amount == 0) {
            throw new BadRequestException("Amount cannot be 0!");
        } else if (availableAmount >= amount) {

            if (orderResponse.isPaid()) {
                throw new BadRequestException("Order " + id + " is already paid. You cannot change products!");
            }

            List<ShoppingListItem> oderShoppingListItem = this.orderProductJdbcRepository.getShoppingList(id);

            for (ShoppingListItem shoppingListItem : oderShoppingListItem) {
                if (shoppingListItem.getProductId() == request.getProductId()) {
                    request.setAmount(amount + shoppingListItem.getAmount());
                    if (request.getAmount() < 0) {
                        throw new BadRequestException("There is not enough amount of product " + request.getProductId() + "!");
                    }

                    this.productJdbcRepository.updateAmount(request.getProductId(), new ProductAmountRequest(-amount));

                    if (request.getAmount() == 0) {
                        this.orderProductJdbcRepository.deleteOrderProductItem(id, request.getProductId());
                    } else {
                        this.orderProductJdbcRepository.updateAmount(id, request);
                    }

                    this.orderJdbcRepository.editOrder(id, false);
                    return this.get(id);
                }
            }

            if (amount > 0) {
                this.productJdbcRepository.updateAmount(request.getProductId(), new ProductAmountRequest(-amount));
                this.orderProductJdbcRepository.addProduct(id, request);
                this.orderJdbcRepository.editOrder(id, false);
                return this.get(id);
            }
        }
        throw new BadRequestException("There is not enough amount of product " + request.getProductId() + "!");
    }

    @Override
    public void delete(long id) {
        OrderResponse orderResponse = this.get(id);
        if (!orderResponse.isPaid()) {
            List<ShoppingListItem> orderShoppingListItems = this.orderProductJdbcRepository.getShoppingList(id);
            for (ShoppingListItem shoppingListItem : orderShoppingListItems) {
                this.productJdbcRepository.updateAmount(shoppingListItem.getProductId(),
                        new ProductAmountRequest(shoppingListItem.getAmount()));
            }
        }
        this.orderProductJdbcRepository.deleteOrderProduct(id);
        this.orderJdbcRepository.deleteOrder(id);

    }

    private String calculateOrderPrice(OrderResponse orderResponse) {
        double totalPrice = 0;
        for (ShoppingListItem shoppingListItem : orderResponse.getShoppingList()) {
            totalPrice += shoppingListItem.getAmount() * this.productJdbcRepository.getById(shoppingListItem.getProductId()).getPrice();
        }
        return String.format("%.1f", totalPrice);
    }
}
