package parentPackage.implementation.jdbc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import parentPackage.api.exception.InternalErrorException;
import parentPackage.dto.request.AddToOrderRequest;
import parentPackage.dto.response.ShoppingListItem;
import parentPackage.implementation.jdbc.mapper.OrderProductRowMapper;

import java.util.List;

@Repository
public class OrderProductJdbcRepository {
    private final OrderProductRowMapper orderProductRowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final static Logger logger;
    private static final String GET_SHOPPING_LIST;
    private static final String INSERT_PRODUCT;
    private static final String UPDATE_PRODUCT_AMOUNT;
    private static final String DELETE_ORDER_SHOPPING_LIST;
    private static final String DELETE_ORDER_SHOPPING_LIST_ITEM;

    static {
        logger = LoggerFactory.getLogger(OrderProductJdbcRepository.class);
        GET_SHOPPING_LIST = "SELECT product_id, amount FROM order_product WHERE order_id = ?";
        INSERT_PRODUCT = "INSERT INTO order_product(id, order_id, product_id, amount) VALUES (next value for order_product_id_seq, ?, ?, ?)";
        UPDATE_PRODUCT_AMOUNT = "UPDATE order_product SET amount = ? WHERE order_id = ? AND product_id = ?";
        DELETE_ORDER_SHOPPING_LIST = "DELETE FROM order_product WHERE order_id = ?";
        DELETE_ORDER_SHOPPING_LIST_ITEM = "DELETE FROM order_product WHERE order_id = ? AND product_id = ?";
    }

    public OrderProductJdbcRepository(OrderProductRowMapper orderProductRowMapper, JdbcTemplate jdbcTemplate) {
        this.orderProductRowMapper = orderProductRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ShoppingListItem> getShoppingList(long id) {
        return this.jdbcTemplate.query(GET_SHOPPING_LIST, this.orderProductRowMapper, id);
    }

    public void addProduct(long id, AddToOrderRequest request) {
        try {
            this.jdbcTemplate.update(INSERT_PRODUCT, id, request.getProductId(), request.getAmount());
        } catch (DataAccessException e) {
            logger.error("Error while adding product {} to order {}!", request.getProductId(), id, e);
            throw new InternalErrorException("Error while adding product " + request.getProductId() + " to order " + id + "!");
        }
    }

    public void updateAmount(long id, AddToOrderRequest request) {
        try {
            this.jdbcTemplate.update(UPDATE_PRODUCT_AMOUNT, request.getAmount(), id, request.getProductId());
        } catch (DataAccessException e) {
            logger.error("Error while adding amount ({}) of product {} to order {}!", request.getAmount(), request.getProductId(), id, e);
            throw new InternalErrorException("Error while adding amount (" + request.getAmount() + ") of product "
                    + request.getProductId() + " to order " + id + "!");
        }
    }

    public void deleteOrderProduct(long id) {
        this.jdbcTemplate.update(DELETE_ORDER_SHOPPING_LIST, id);
    }

    public void deleteOrderProductItem(long order_id, long product_id) {
        this.jdbcTemplate.update(DELETE_ORDER_SHOPPING_LIST_ITEM, order_id, product_id);
    }
}
