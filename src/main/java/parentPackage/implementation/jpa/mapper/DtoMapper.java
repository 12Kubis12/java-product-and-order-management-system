package parentPackage.implementation.jpa.mapper;

import parentPackage.dto.response.OrderResponse;
import parentPackage.dto.response.ProductResponse;
import parentPackage.dto.response.ShoppingListItem;
import parentPackage.implementation.jpa.entity.OrderEntity;
import parentPackage.implementation.jpa.entity.OrderProductEntity;
import parentPackage.implementation.jpa.entity.ProductEntity;

public class DtoMapper {

    public static ProductResponse mapProductEntityToProductResponse(ProductEntity productEntity) {
        return new ProductResponse(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getAmount(),
                productEntity.getPrice()
        );
    }

    public static OrderResponse mapOrderEntityToOrderResponse(OrderEntity orderEntity) {
        return new OrderResponse(
                orderEntity.getId(),
                orderEntity.getOrderProductEntities().stream()
                        .map(DtoMapper::mapOrderProductEntityToShoppingList)
                        .toList(),
                orderEntity.isPaid(),
                orderEntity.getCreatedAt(),
                orderEntity.getUpdatedAt()
        );
    }

    public static ShoppingListItem mapOrderProductEntityToShoppingList(OrderProductEntity orderProductEntity) {
        return new ShoppingListItem(
                orderProductEntity.getProductEntity().getId(),
                orderProductEntity.getAmount()
        );
    }


}
