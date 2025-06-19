package parentPackage.api;

import parentPackage.dto.request.AddProductRequest;
import parentPackage.dto.request.ProductAmountRequest;
import parentPackage.dto.request.UpdateProductRequest;
import parentPackage.dto.response.ProductAmountResponse;
import parentPackage.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse get(long id);

    List<ProductResponse> getAll();

    ProductResponse add(AddProductRequest request);

    ProductResponse edit(long id, UpdateProductRequest request);

    ProductAmountResponse getAmount(long id);

    ProductAmountResponse updateAmount(long id, ProductAmountRequest request);

    void delete(long id);
}
