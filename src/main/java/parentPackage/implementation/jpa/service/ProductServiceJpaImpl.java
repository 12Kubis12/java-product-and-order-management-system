package parentPackage.implementation.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import parentPackage.api.ProductService;
import parentPackage.api.exception.BadRequestException;
import parentPackage.api.exception.InternalErrorException;
import parentPackage.api.exception.ResourceNotFoundException;
import parentPackage.dto.request.AddProductRequest;
import parentPackage.dto.request.ProductAmountRequest;
import parentPackage.dto.request.UpdateProductRequest;
import parentPackage.dto.response.ProductAmountResponse;
import parentPackage.dto.response.ProductResponse;
import parentPackage.implementation.jpa.entity.ProductEntity;
import parentPackage.implementation.jpa.mapper.DtoMapper;
import parentPackage.implementation.jpa.repository.ProductJpaRepository;

import java.util.List;

@Service
@Profile("jpa")
public class ProductServiceJpaImpl implements ProductService {
    private final ProductJpaRepository productJpaRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceJpaImpl.class);

    public ProductServiceJpaImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public ProductResponse get(long id) {
        return DtoMapper.mapProductEntityToProductResponse(this.findProductEntity(id));
    }

    @Override
    public List<ProductResponse> getAll() {
        return this.productJpaRepository.findAll().stream()
                .map(DtoMapper::mapProductEntityToProductResponse)
                .toList();
    }

    @Override
    public ProductResponse add(AddProductRequest request) {
        try {
            return this.get(this.productJpaRepository.save(new ProductEntity(
                    request.getName(),
                    request.getDescription(),
                    request.getAmount(),
                    request.getPrice()
            )).getId());
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Product with name " + request.getName() + " already exists!");
        } catch (DataAccessException e) {
            logger.error("Error while adding product!", e);
            throw new InternalErrorException("Error while adding product!");
        }
    }

    @Override
    public ProductResponse edit(long id, UpdateProductRequest request) {
        ProductEntity productEntity = this.findProductEntity(id);

        try {
            productEntity.setName(request.getName());
            productEntity.setDescription(request.getDescription());
            productEntity.setPrice(request.getPrice());
            this.productJpaRepository.save(productEntity);
        } catch (DataAccessException e) {
            logger.error("Error while updating product {}!", id, e);
            throw new InternalErrorException("Error while updating product " + id + "!");
        }

        return this.get(id);
    }

    @Override
    public ProductAmountResponse getAmount(long id) {
        final ProductEntity productEntity = this.findProductEntity(id);
        return new ProductAmountResponse(productEntity.getAmount());
    }

    @Override
    public ProductAmountResponse updateAmount(long id, ProductAmountRequest request) {
        final ProductEntity productEntity = this.findProductEntity(id);
        request.setAmount(request.getAmount() + productEntity.getAmount());

        if (request.getAmount() < 0) {
            throw new BadRequestException("Amount of product " + id + " cannot be less than 0!");
        }

        try {
            productEntity.setAmount(request.getAmount());
            this.productJpaRepository.save(productEntity);
            return new ProductAmountResponse(productEntity.getAmount());
        } catch (DataAccessException e) {
            logger.error("Error while updating amount ({}) of product {}!", request.getAmount(), id, e);
            throw new InternalErrorException("Error while updating amount (" + request.getAmount() + ") of product " + id + "!");
        }
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            try {
                this.productJpaRepository.deleteById(id);
            } catch (DataIntegrityViolationException e) {
                logger.error("Product {} is part of some order. You cannot delete it!", id, e);
                throw new BadRequestException("Product " + id + " is part of some order. You cannot delete it!");
            } catch (DataAccessException e) {
                logger.error("Error while deleting product {}!", id, e);
                throw new InternalErrorException("Error while deleting product " + id + "!");
            }
        }
    }

    public ProductEntity findProductEntity(long id) {
        return this.productJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }
}
