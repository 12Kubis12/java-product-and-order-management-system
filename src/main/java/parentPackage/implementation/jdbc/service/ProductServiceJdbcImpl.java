package parentPackage.implementation.jdbc.service;

import org.springframework.stereotype.Service;
import parentPackage.api.ProductService;
import parentPackage.api.request.AddProductRequest;
import parentPackage.api.request.ProductAmountRequest;
import parentPackage.api.request.UpdateProductRequest;
import parentPackage.domain.ProductAmountResponse;
import parentPackage.domain.ProductResponse;
import parentPackage.implementation.jdbc.repository.ProductJdbcRepository;

import java.util.List;

@Service
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
        return this.productJdbcRepository.add(request);
    }

    @Override
    public ProductResponse edit(long id, UpdateProductRequest request) {
        if (this.get(id) != null) {
            return this.productJdbcRepository.update(id, request);
        }
        return null;
    }

    @Override
    public ProductAmountResponse getAmount(long id) {
        if (this.get(id) != null) {
            return this.productJdbcRepository.getAmount(id);
        }
        return null;
    }

    @Override
    public ProductAmountResponse addAmount(long id, ProductAmountRequest request) {
        if (this.get(id) != null) {
            return this.productJdbcRepository.addAmount(id, request);
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
