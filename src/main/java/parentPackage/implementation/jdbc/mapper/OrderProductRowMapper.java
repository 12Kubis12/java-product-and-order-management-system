package parentPackage.implementation.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import parentPackage.dto.response.ShoppingListItem;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OrderProductRowMapper implements RowMapper<ShoppingListItem> {
    @Override
    public ShoppingListItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ShoppingListItem(
                rs.getLong("product_id"),
                rs.getLong("amount")
        );
    }
}
