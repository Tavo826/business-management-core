package co.com.manager.r2dbc.repository.order;

import co.com.manager.model.order.Order;
import co.com.manager.model.order.OrderRepository;
import co.com.manager.model.order.OrderItem;
import co.com.manager.model.user.User;
import co.com.manager.r2dbc.data.order.OrderData;
import co.com.manager.r2dbc.data.order.OrderItemData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class OrderRepositoryAdapter extends ReactiveAdapterOperations<
        Order,
        OrderData,
        String,
        OrderDataRepository
        > implements OrderRepository {

    private final OrderItemDataRepository orderItemRepository;

    public OrderRepositoryAdapter(OrderDataRepository repository, ObjectMapper mapper, OrderItemDataRepository orderItemRepository) {
        super(repository, mapper, d -> mapper.map(d, Order.class));

        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Mono<Order> saveOrder(Order order) {

        log.info("Saving order");

        String orderId = UUID.randomUUID().toString();
        order.setId(orderId);

        OrderData orderData = toData(order);

        log.info("Order a guardar: {}", order);

        return repository.save(orderData)
                .then(saveItems(orderId, order.getItems()))
                .then(Mono.just(order))
                .doOnSuccess(o -> log.info("Order saved with id: {}", orderId));
    }

    @Override
    public Mono<Order> findById(String id) {

        log.info("Finding order by id {}", id);

        return repository.findById(id)
                .flatMap(this::toOrderWithItems);
    }

    @Override
    public Flux<Order> findAllByBusinessId(String businessId) {

        log.info("Finding orders by business id {}", businessId);

        return repository.findAllByBusinessId(businessId)
                .flatMap(this::toOrderWithItems);
    }


    @Override
    public Flux<Order> findByStatus(String businessId, String status) {

        log.info("Finding orders by status {}", status);

        return repository.findAllByBusinessIdAndStatus(businessId, status)
                .flatMap(this::toOrderWithItems);
    }

    @Override
    public Mono<Order> updateStatus(Order order) {

        log.info("Updating order by id {} with status {}", order.getId(), order.getStatus());

        return Mono.just(toData(order))
                .doOnNext(orderData -> orderData.setNew(false))
                .flatMap(this::saveData)
                .flatMap(this::toOrderWithItems);
    }

    private Mono<Void> saveItems(String orderId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return Mono.empty();
        }
        List<OrderItemData> itemDataList = items.stream()
                .map(item -> OrderItemData.builder()
                        .id(UUID.randomUUID().toString())
                        .orderId(orderId)
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .toList();

        log.info("Productos a guardar: {}", itemDataList);

        return orderItemRepository.saveAll(itemDataList).then();
    }

    private Mono<Order> toOrderWithItems(OrderData data) {
        return orderItemRepository.findByOrderId(data.getId())
                .map(itemData -> OrderItem.builder()
                        .productName(itemData.getProductName())
                        .quantity(itemData.getQuantity())
                        .unitPrice(itemData.getUnitPrice())
                        .build())
                .collectList()
                .map(items -> Order.builder()
                        .id(data.getId())
                        .businessId(data.getBusinessId())
                        .customerName(data.getCustomerName())
                        .customerPhone(data.getCustomerPhone())
                        .customerAddress(data.getCustomerAddress())
                        .status(data.getStatus())
                        .totalAmount(data.getTotalAmount())
                        .items(items)
                        .createdAt(data.getCreatedAt())
                        .build());
    }

    @Override
    protected OrderData toData(Order order) {
        return OrderData.builder()
                .id(order.getId())
                .businessId(order.getBusinessId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerAddress(order.getCustomerAddress())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
