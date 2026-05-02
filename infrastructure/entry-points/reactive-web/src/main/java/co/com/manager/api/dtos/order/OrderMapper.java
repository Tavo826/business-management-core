package co.com.manager.api.dtos.order;

import co.com.manager.model.order.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public static Order toEntity(String id, OrderRequest orderRequest) {

        return Order.builder()
                .id(id)
                .customerName(orderRequest.getCustomerName())
                .customerPhone(orderRequest.getCustomerPhone())
                .customerAddress(orderRequest.getCustomerAddress())
                .status(orderRequest.getStatus())
                .build();
    }
}
