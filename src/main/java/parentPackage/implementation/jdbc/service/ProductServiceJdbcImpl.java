package parentPackage.implementation.jdbc.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import parentPackage.api.ProductService;
import parentPackage.dto.request.AddProductRequest;
import parentPackage.dto.request.ProductAmountRequest;
import parentPackage.dto.request.UpdateProductRequest;
import parentPackage.dto.response.ProductAmountResponse;
import parentPackage.dto.response.ProductResponse;
import parentPackage.implementation.jdbc.repository.ProductJdbcRepository;

import java.util.List;

@Service
@Profile("jdbc")
public class ProductServiceJdbcImpl implements ProductService {
    private final ProductJdbcRepository productJdbcRepository;

    public ProductServiceJdbcImpl(ProductJdbcRepository productJdbcRepository) {
        this.productJdbcRepository = productJdbcRepository;
    }

    @Override
    public List<ProductResponse> getAll() {
        return this.productJdbcRepository.getAll();
    }

    @Override
    public ProductResponse get(long id) {
        return this.productJdbcRepository.getById(id);
    }


    @Override
    public ProductResponse add(AddProductRequest request) {
        return this.get(this.productJdbcRepository.add(request));
    }

    @Override
    public ProductResponse edit(long id, UpdateProductRequest request) {
        if (this.get(id) != null) {
            this.productJdbcRepository.update(id, request);
            return this.get(id);
        }
        return null;
    }

    @Override
    public ProductAmountResponse getAmount(long id) {
        if (this.get(id) != null) {
            return new ProductAmountResponse(this.productJdbcRepository.getById(id).getAmount());
        }
        return null;
    }

    @Override
    public ProductAmountResponse updateAmount(long id, ProductAmountRequest request) {
        if (this.get(id) != null) {
            this.productJdbcRepository.updateAmount(id, request);
            return new ProductAmountResponse(request.getAmount());
        }
        return null;
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            this.productJdbcRepository.delete(id);
        }
    }
}
