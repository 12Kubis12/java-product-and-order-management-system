package parentPackage.implementation.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import parentPackage.domain.ProductResponse;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProductRowMapper implements RowMapper<ProductResponse> {
    @Override
    public ProductResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProductResponse(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getLong("amount"),
                rs.getDouble("price")
        );
    }
}