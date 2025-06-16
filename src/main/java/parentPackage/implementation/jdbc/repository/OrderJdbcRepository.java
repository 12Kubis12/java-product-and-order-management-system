package parentPackage.implementation.jdbc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import parentPackage.api.exception.BadRequestException;
import parentPackage.api.exception.InternalErrorException;
import parentPackage.api.exception.ResourceNotFoundException;
import parentPackage.api.request.AddToOrderRequest;
import parentPackage.domain.OrderResponse;
import parentPackage.domain.ShoppingListItem;
import parentPackage.implementation.jdbc.mapper.OrderProductRowMapper;
import parentPackage.implementation.jdbc.mapper.OrderRowMapper;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class OrderJdbcRepository {
    private final OrderRowMapper orderRowMapper;
    private final OrderProductRowMapper orderProductRowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final static Logger logger;
    private static final String GET_BY_ID;
    private static final String GET_SHOPPING_LIST;
    private static final String INSERT_ORDER;
    private static final String INSERT_PRODUCT;
    private static final String UPDATE_ORDER;
    private static final String UPDATE_PRODUCT_AMOUNT;
    private static final String DELETE_ORDER;
    private static final String DELETE_ORDER_SHOPPING_LIST;
    private static final String DELETE_ORDER_SHOPPING_LIST_ITEM;

    static {
        logger = LoggerFactory.getLogger(OrderJdbcRepository.class);
        GET_BY_ID = "SELECT * FROM order WHERE id = ?";
        GET_SHOPPING_LIST = "SELECT product_id, amount FROM order_product WHERE order_id = ?";
        INSERT_ORDER = "INSERT INTO order(id, paid, created_at, updated_at) VALUES (next value for order_id_seq, ?, ?, ?)";
        INSERT_PRODUCT = "INSERT INTO order_product(order_id, product_id, amount) VALUES (?, ?, ?)";
        UPDATE_ORDER = "UPDATE order SET paid = ?, updated_at = ? WHERE id = ?";
        UPDATE_PRODUCT_AMOUNT = "UPDATE order_product SET amount = ? WHERE order_id = ? AND product_id = ?";
        DELETE_ORDER = "DELETE FROM order WHERE id = ?";
        DELETE_ORDER_SHOPPING_LIST = "DELETE FROM order_product WHERE order_id = ?";
        DELETE_ORDER_SHOPPING_LIST_ITEM = "DELETE FROM order_product WHERE order_id = ? AND product_id = ?";
    }

    public OrderJdbcRepository(OrderRowMapper orderRowMapper, OrderProductRowMapper orderProductRowMapper, JdbcTemplate jdbcTemplate) {
        this.orderRowMapper = orderRowMapper;
        this.orderProductRowMapper = orderProductRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public OrderResponse getById(long id) {
        try {
            List<ShoppingListItem> orderShoppingListItems = this.getShoppingList(id);
            OrderResponse orderResponse = this.jdbcTemplate.queryForObject(GET_BY_ID, this.orderRowMapper, id);
            if (orderResponse != null) {
                orderResponse.updateShoppingList(orderShoppingListItems);
            }
            return orderResponse;
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Order id " + id + " was not found!");
        } catch (DataAccessException e) {
            logger.error("Error while getting order {}!", id, e);
            throw new InternalErrorException("Error while getting order " + id + "!");
        }
    }

    public OrderResponse create() {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(connection -> {
                final PreparedStatement ps = connection.prepareStatement(INSERT_ORDER, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setBoolean(1, false);
                ps.setTimestamp(2, Timestamp.from(OffsetDateTime.now().toInstant()));
                ps.setTimestamp(3, Timestamp.from(OffsetDateTime.now().toInstant()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                logger.error("Error while creating order! KeyHolder.getKey() is null!");
                throw new InternalErrorException("Error while creating order!");
            }

            long orderId = keyHolder.getKey().longValue();
            return this.getById(orderId);
        } catch (DataAccessException e) {
            logger.error("Error while creating order!", e);
            throw new InternalErrorException("Error while creating order!");
        }
    }

    public void deleteOrder(long id) {
        try {
            this.jdbcTemplate.update(DELETE_ORDER_SHOPPING_LIST, id);
            this.jdbcTemplate.update(DELETE_ORDER, id);
        } catch (DataAccessException e) {
            logger.error("Error while deleting order {}!", id, e);
            throw new InternalErrorException("Error while deleting order " + id + "!");
        }
    }

    public OrderResponse addProduct(long id, AddToOrderRequest request) {
        try {
            this.jdbcTemplate.update(INSERT_PRODUCT, id, request.getProductId(), request.getAmount());
            this.editOrder(id, false);
            return this.getById(id);
        } catch (DataAccessException e) {
            logger.error("Error while adding product {} to order {}!", request.getProductId(), id, e);
            throw new InternalErrorException("Error while adding product " + request.getProductId() + " to order " + id + "!");
        }
    }

    public OrderResponse addAmount(long id, AddToOrderRequest request) {
        try {
            this.jdbcTemplate.update(UPDATE_PRODUCT_AMOUNT, request.getAmount(), id, request.getProductId());
            this.editOrder(id, false);
            OrderResponse orderResponse = this.getById(id);
            for (ShoppingListItem shoppingListItem : orderResponse.getShoppingList()) {
                if (shoppingListItem.getAmount() == 0) {
                    this.jdbcTemplate.update(DELETE_ORDER_SHOPPING_LIST_ITEM, id, request.getProductId());
                }
            }
            return orderResponse;
        } catch (DataAccessException e) {
            logger.error("Error while adding amount ({}) of product {} to order {}!", request.getAmount(), request.getProductId(), id, e);
            throw new InternalErrorException("Error while adding amount (" + request.getAmount() + ") of product "
                    + request.getProductId() + " to order " + id + "!");
        }
    }

    public OrderResponse editOrder(long id, boolean pay) {
        try {
            if (!this.getById(id).isPaid()) {
                this.jdbcTemplate.update(UPDATE_ORDER, pay, Timestamp.from(OffsetDateTime.now().toInstant()), id);
            } else {
                throw new BadRequestException("Order " + id + " is already paid!");
            }
            return this.getById(id);
        } catch (DataAccessException e) {
            logger.error("Error while paying order {}!", id, e);
            throw new InternalErrorException("Error while paying order " + id + "!");
        }
    }

    public List<ShoppingListItem> getShoppingList(long id) {
        return this.jdbcTemplate.query(GET_SHOPPING_LIST, this.orderProductRowMapper, id);
    }
}
