package parentPackage.implementation.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import parentPackage.domain.OrderResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class OrderRowMapper implements RowMapper<OrderResponse> {
    @Override
    public OrderResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrderResponse(
                rs.getLong("id"),
                new ArrayList<>(),
                rs.getBoolean("paid")
        );
    }
}
