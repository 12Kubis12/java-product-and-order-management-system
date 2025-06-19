package parentPackage.implementation.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductEntity {
    @Id
    @SequenceGenerator(name = "product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_seq")
    private Long id;
    @Column(nullable = false)
    @Setter
    private String name;
    @Column
    @Setter
    private String description;
    @Column(nullable = false)
    @Setter
    private long amount;
    @Column(nullable = false)
    @Setter
    private double price;

    public ProductEntity(String name, String description, long amount, double price) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
