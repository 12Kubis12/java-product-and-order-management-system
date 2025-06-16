package parentPackage.domain;

import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

@Value
public class OrderResponse {
    long id;
    List<ShoppingListItem> shoppingList;
    boolean paid;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    public void updateShoppingList(List<ShoppingListItem> shoppingListItem) {
        this.shoppingList.addAll(shoppingListItem);
    }
}
