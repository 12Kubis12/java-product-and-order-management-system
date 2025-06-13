package parentPackage.domain;

import lombok.Value;

@Value
public class ProductResponse {
    long id;
    String name;
    String description;
    long amount;
    double price;
}
