package parentPackage.api;

import parentPackage.api.request.AddToOrderRequest;
import parentPackage.domain.OrderResponse;

public interface OrderService {
    OrderResponse create();

    String pay(long id);

    OrderResponse addProduct(long id, AddToOrderRequest request);

    void delete(long id);

    OrderResponse get(long id);
}
