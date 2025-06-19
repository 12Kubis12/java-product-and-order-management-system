package parentPackage.api;

import parentPackage.dto.request.AddToOrderRequest;
import parentPackage.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse get(long id);

    OrderResponse add();

    String pay(long id);

    OrderResponse addProduct(long id, AddToOrderRequest request);

    void delete(long id);
}
