package parentPackage.api;

import parentPackage.api.request.AddProductRequest;
import parentPackage.api.request.ProductAmountRequest;
import parentPackage.api.request.UpdateProductRequest;
import parentPackage.domain.ProductAmountResponse;
import parentPackage.domain.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse add(AddProductRequest request);

    ProductResponse edit(long id, UpdateProductRequest request);

    ProductAmountResponse getAmount(long id);

    ProductAmountResponse addAmount(long id, ProductAmountRequest request);

    void delete(long id);

    ProductResponse get(long id);

    List<ProductResponse> getAll();
}
