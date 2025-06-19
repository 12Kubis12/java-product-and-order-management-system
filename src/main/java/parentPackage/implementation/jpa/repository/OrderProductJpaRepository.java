package parentPackage.implementation.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import parentPackage.implementation.jpa.entity.OrderProductEntity;

@Repository
public interface OrderProductJpaRepository extends JpaRepository<OrderProductEntity, Long> {
    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM order_product WHERE order_id = :orderId AND product_id = :productId",
            nativeQuery = true
    )
    void deleteByOrderIdAndProductId(long orderId, long productId);
}
