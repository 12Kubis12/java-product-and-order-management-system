package parentPackage.implementation.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity(name = "order_product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderProductEntity {
    @Id
    @SequenceGenerator(name = "order_product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_product_id_seq")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;
    @Column(nullable = false)
    @Setter
    private Long amount;

    public OrderProductEntity(OrderEntity orderEntity, ProductEntity productEntity, Long amount) {
        this.orderEntity = orderEntity;
        this.productEntity = productEntity;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderProductEntity that = (OrderProductEntity) o;
        return Objects.equals(orderEntity, that.orderEntity) && Objects.equals(productEntity, that.productEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderEntity, productEntity);
    }
}
