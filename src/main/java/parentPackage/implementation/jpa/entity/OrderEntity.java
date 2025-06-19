package parentPackage.implementation.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderEntity {
    @Id
    @SequenceGenerator(name = "order_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    private Long id;
    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderProductEntity> orderProductEntities = new ArrayList<>();
    @Column(nullable = false)
    @Setter
    private boolean paid;
    @Column(nullable = false)
    private OffsetDateTime createdAt;
    @Column(nullable = false)
    @Setter
    private OffsetDateTime updatedAt;

    public OrderEntity(boolean paid, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.paid = paid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addProduct(ProductEntity productEntity, long amount) {
        OrderProductEntity orderProductEntity = new OrderProductEntity(this, productEntity, amount);
        this.getOrderProductEntities().add(orderProductEntity);
    }

    public void removeProduct(ProductEntity productEntity) {
        this.orderProductEntities.removeIf(orderProductEntity -> orderProductEntity.getOrderEntity().equals(this) &&
                orderProductEntity.getProductEntity().equals(productEntity));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
