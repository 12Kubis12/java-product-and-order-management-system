package parentPackage.domain;

import lombok.Value;

import java.util.List;

@Value
public class OrderResponse {
    long id;
    List<ShoppingListItem> shoppingList;
    boolean paid;

    public void updateShoppingList(List<ShoppingListItem> shoppingListItem) {
        this.shoppingList.addAll(shoppingListItem);
    }
}
