package parentPackage.implementation.jdbc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import parentPackage.api.exception.InternalErrorException;
import parentPackage.api.exception.ResourceNotFoundException;
import parentPackage.dto.response.OrderResponse;
import parentPackage.implementation.jdbc.mapper.OrderRowMapper;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Repository
public class OrderJdbcRepository {
    private final OrderRowMapper orderRowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final static Logger logger;
    private static final String GET_BY_ID;
    private static final String INSERT_ORDER;
    private static final String UPDATE_ORDER;
    private static final String DELETE_ORDER;

    static {
        logger = LoggerFactory.getLogger(OrderJdbcRepository.class);
        GET_BY_ID = "SELECT * FROM orders WHERE id = ?";
        INSERT_ORDER = "INSERT INTO orders(id, paid, created_at, updated_at) VALUES (next value for order_id_seq, ?, ?, ?)";
        UPDATE_ORDER = "UPDATE orders SET paid = ?, updated_at = ? WHERE id = ?";
        DELETE_ORDER = "DELETE FROM orders WHERE id = ?";
    }

    public OrderJdbcRepository(OrderRowMapper orderRowMapper, JdbcTemplate jdbcTemplate) {
        this.orderRowMapper = orderRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public OrderResponse getById(long id) {
        try {
            return this.jdbcTemplate.queryForObject(GET_BY_ID, this.orderRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Order id " + id + " was not found!");
        } catch (DataAccessException e) {
            logger.error("Error while getting order {}!", id, e);
            throw new InternalErrorException("Error while getting order " + id + "!");
        }
    }

    public long add() {
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

            return keyHolder.getKey().longValue();
        } catch (DataAccessException e) {
            logger.error("Error while creating order!", e);
            throw new InternalErrorException("Error while creating order!");
        }
    }

    public void editOrder(long id, boolean pay) {
        try {
            this.jdbcTemplate.update(UPDATE_ORDER, pay, Timestamp.from(OffsetDateTime.now().toInstant()), id);
        } catch (DataAccessException e) {
            logger.error("Error while paying/editing order {}!", id, e);
            throw new InternalErrorException("Error while paying/editing order " + id + "!");
        }
    }

    public void deleteOrder(long id) {
        try {
            this.jdbcTemplate.update(DELETE_ORDER, id);
        } catch (DataAccessException e) {
            logger.error("Error while deleting order {}!", id, e);
            throw new InternalErrorException("Error while deleting order " + id + "!");
        }
    }
}
