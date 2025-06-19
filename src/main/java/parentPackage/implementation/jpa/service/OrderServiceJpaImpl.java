package parentPackage.implementation.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import parentPackage.api.OrderService;
import parentPackage.api.exception.BadRequestException;
import parentPackage.api.exception.InternalErrorException;
import parentPackage.api.exception.ResourceNotFoundException;
import parentPackage.dto.request.AddToOrderRequest;
import parentPackage.dto.request.ProductAmountRequest;
import parentPackage.dto.response.OrderResponse;
import parentPackage.implementation.jpa.entity.OrderEntity;
import parentPackage.implementation.jpa.entity.OrderProductEntity;
import parentPackage.implementation.jpa.mapper.DtoMapper;
import parentPackage.implementation.jpa.repository.OrderJpaRepository;
import parentPackage.implementation.jpa.repository.OrderProductJpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Profile("jpa")
public class OrderServiceJpaImpl implements OrderService {
    private final OrderJpaRepository orderJpaRepository;
    private final ProductServiceJpaImpl productServiceJpa;
    private final OrderProductJpaRepository orderProductJpaRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceJpaImpl.class);

    public OrderServiceJpaImpl(OrderJpaRepository orderJpaRepository, ProductServiceJpaImpl productServiceJpa, OrderProductJpaRepository orderProductJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.productServiceJpa = productServiceJpa;
        this.orderProductJpaRepository = orderProductJpaRepository;
    }

    @Override
    public OrderResponse get(long id) {
        System.out.println(DtoMapper.mapOrderEntityToOrderResponse(this.findOrderEntity(id)));
        return DtoMapper.mapOrderEntityToOrderResponse(this.findOrderEntity(id));
    }

    @Override
    public OrderResponse add() {
        try {
            return this.get(this.orderJpaRepository.save(new OrderEntity(
                    false,
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            )).getId());
        } catch (DataAccessException e) {
            logger.error("Error while creating order!", e);
            throw new InternalErrorException("Error while creating order!");
        }
    }

    @Override
    public String pay(long id) {
        final OrderEntity orderEntity = this.findOrderEntity(id);
        if (orderEntity.isPaid()) {
            throw new BadRequestException("Order " + id + " is already paid!");
        } else {
            try {
                orderEntity.setPaid(true);
                orderEntity.setUpdatedAt(OffsetDateTime.now());
                this.orderJpaRepository.save(orderEntity);
                return this.calculateOrderPrice(orderEntity);
            } catch (DataAccessException e) {
                logger.error("Error while paying/editing order {}!", id, e);
                throw new InternalErrorException("Error while paying/editing order " + id + "!");
            }
        }
    }

    @Override
    public OrderResponse addProduct(long id, AddToOrderRequest request) {
        long availableAmount = this.productServiceJpa.findProductEntity(request.getProductId()).getAmount();
        long amount = request.getAmount();
        OrderEntity orderEntity = this.findOrderEntity(id);

        if (amount == 0) {
            throw new BadRequestException("Amount cannot be 0!");
        } else if (availableAmount >= amount) {

            if (orderEntity.isPaid()) {
                throw new BadRequestException("Order " + id + " is already paid. You cannot change products!");
            }

            List<OrderProductEntity> orderProductEntities = orderEntity.getOrderProductEntities();

            for (OrderProductEntity orderProductEntity : orderProductEntities) {
                if (orderProductEntity.getProductEntity().getId() == request.getProductId()) {
                    request.setAmount(amount + orderProductEntity.getAmount());
                    if (request.getAmount() < 0) {
                        throw new BadRequestException("There is not enough amount of product " + request.getProductId() + "!");
                    }

                    this.productServiceJpa.updateAmount(request.getProductId(), new ProductAmountRequest(-amount));

                    try {
                        if (request.getAmount() == 0) {
                            this.orderProductJpaRepository.deleteByOrderIdAndProductId(id, request.getProductId());
                            orderEntity.removeProduct(this.productServiceJpa.findProductEntity(request.getProductId()));
                        } else {
                            orderProductEntity.setAmount(request.getAmount());
                            this.orderProductJpaRepository.save(orderProductEntity);
                        }
                        this.updateOrder(id);

                    } catch (DataAccessException e) {
                        logger.error("Error while adding amount ({}) of product {} to order {}!", request.getAmount(), request.getProductId(), id, e);
                        throw new InternalErrorException("Error while adding amount (" + request.getAmount() + ") of product "
                                + request.getProductId() + " to order " + id + "!");
                    }

                    return this.get(id);
                }
            }

            if (amount > 0) {
                this.productServiceJpa.updateAmount(request.getProductId(), new ProductAmountRequest(-amount));
                this.addProductToOrder(id, request);
                this.updateOrder(id);
                return this.get(id);
            }
        }

        throw new BadRequestException("There is not enough amount of product " + request.getProductId() + "!");
    }

    @Override
    public void delete(long id) {
        OrderEntity orderEntity = this.findOrderEntity(id);
        if (!orderEntity.isPaid()) {
            List<OrderProductEntity> orderProductEntities = this.findOrderEntity(id).getOrderProductEntities();
            for (OrderProductEntity orderProductEntity : orderProductEntities) {
                this.productServiceJpa.updateAmount(orderProductEntity.getProductEntity().getId(),
                        new ProductAmountRequest(orderProductEntity.getAmount()));
            }
        }
        try {
            this.orderJpaRepository.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Error while deleting order {}!", id, e);
            throw new InternalErrorException("Error while deleting order " + id + "!");
        }

    }

    private OrderEntity findOrderEntity(long id) {
        return this.orderJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " was not found!"));
    }

    private void updateOrder(long id) {
        OrderEntity orderEntity = this.findOrderEntity(id);
        orderEntity.setUpdatedAt(OffsetDateTime.now());
        this.orderJpaRepository.save(orderEntity);
    }

    private void addProductToOrder(long id, AddToOrderRequest request) {
        try {
            OrderEntity orderEntity = this.findOrderEntity(id);
            orderEntity.addProduct(this.productServiceJpa.findProductEntity(request.getProductId()), request.getAmount());
            this.orderJpaRepository.save(orderEntity);
        } catch (DataAccessException e) {
            logger.error("Error while adding product {} to order {}!", request.getProductId(), id, e);
            throw new InternalErrorException("Error while adding product " + request.getProductId() + " to order " + id + "!");
        }
    }

    private String calculateOrderPrice(OrderEntity orderEntity) {
        double totalPrice = 0;
        for (OrderProductEntity orderProductEntity : orderEntity.getOrderProductEntities()) {
            long productId = orderProductEntity.getProductEntity().getId();
            totalPrice += orderProductEntity.getAmount() * this.productServiceJpa.findProductEntity(productId).getPrice();
        }
        return String.format("%.1f", totalPrice);
    }
}
