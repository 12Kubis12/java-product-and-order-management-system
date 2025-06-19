package parentPackage.implementation.jdbc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import parentPackage.api.exception.BadRequestException;
import parentPackage.api.exception.InternalErrorException;
import parentPackage.api.exception.ResourceNotFoundException;
import parentPackage.dto.request.AddProductRequest;
import parentPackage.dto.request.ProductAmountRequest;
import parentPackage.dto.request.UpdateProductRequest;
import parentPackage.dto.response.ProductResponse;
import parentPackage.implementation.jdbc.mapper.ProductRowMapper;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

@Repository
public class ProductJdbcRepository {
    private final ProductRowMapper productRowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final static Logger logger;
    private static final String GET_ALL;
    private static final String GET_BY_ID;
    private static final String INSERT;
    private static final String UPDATE;
    private static final String UPDATE_AMOUNT;
    private static final String DELETE;

    static {
        logger = LoggerFactory.getLogger(OrderJdbcRepository.class);
        GET_ALL = "SELECT * FROM product";
        GET_BY_ID = "SELECT * FROM product WHERE id = ?";
        INSERT = "INSERT INTO product(id, name, description, amount, price) VALUES (next value for product_id_seq, ?, ?, ?, ?)";
        UPDATE = "UPDATE product SET name = ?, description = ?, price = ? WHERE id = ?";
        UPDATE_AMOUNT = "UPDATE product SET amount = ? WHERE id = ?";
        DELETE = "DELETE FROM product WHERE id = ?";
    }

    public ProductJdbcRepository(ProductRowMapper productRowMapper, JdbcTemplate jdbcTemplate) {
        this.productRowMapper = productRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductResponse> getAll() {
        try {
            return this.jdbcTemplate.query(GET_ALL, this.productRowMapper);
        } catch (DataAccessException e) {
            logger.error("Error while getting all products!", e);
            throw new InternalErrorException("Error while getting all products!");
        }
    }

    public ProductResponse getById(long id) {
        try {
            return this.jdbcTemplate.queryForObject(GET_BY_ID, this.productRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Product with id " + id + " was not found!");
        } catch (DataAccessException e) {
            logger.error("Error while getting product {}!", id, e);
            throw new InternalErrorException("Error while getting product " + id + "!");
        }
    }

    public long add(AddProductRequest request) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(connection -> {
                final PreparedStatement ps = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, request.getName());
                if (request.getDescription() != null) {
                    ps.setString(2, request.getDescription());
                } else {
                    ps.setNull(2, Types.VARCHAR);
                }
                ps.setLong(3, request.getAmount());
                ps.setDouble(4, request.getPrice());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                logger.error("Error while adding product! KeyHolder.getKey() is null!");
                throw new InternalErrorException("Error while adding product!");
            }

            return keyHolder.getKey().longValue();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Product with name " + request.getName() + " already exists!");
        } catch (DataAccessException e) {
            logger.error("Error while adding product!", e);
            throw new InternalErrorException("Error while adding product!");
        }
    }

    public void update(long id, UpdateProductRequest request) {
        try {
            this.jdbcTemplate.update(UPDATE, request.getName(), request.getDescription(), request.getPrice(), id);
        } catch (DataAccessException e) {
            logger.error("Error while updating product {}!", id, e);
            throw new InternalErrorException("Error while updating product " + id + "!");
        }
    }

    public void updateAmount(long id, ProductAmountRequest request) {
        try {
            request.setAmount(request.getAmount() + this.getById(id).getAmount());
            if (request.getAmount() < 0) {
                throw new BadRequestException("Amount of product " + id + " cannot be less than 0!");
            }
            this.jdbcTemplate.update(UPDATE_AMOUNT, request.getAmount(), id);
        } catch (DataAccessException e) {
            logger.error("Error while updating amount ({}) of product {}!", request.getAmount(), id, e);
            throw new InternalErrorException("Error while updating amount (" + request.getAmount() + ") of product " + id + "!");
        }
    }

    public void delete(Long id) {
        try {
            this.jdbcTemplate.update(DELETE, id);
        } catch (DataIntegrityViolationException e) {
            logger.error("Product {} is part of some order. You cannot delete it!", id, e);
            throw new BadRequestException("Product " + id + " is part of some order. You cannot delete it!");
        } catch (DataAccessException e) {
            logger.error("Error while deleting product {}!", id, e);
            throw new InternalErrorException("Error while deleting product " + id + "!");
        }
    }
}
