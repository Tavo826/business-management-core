package co.com.manager.api.dtos.order;

import co.com.manager.model.order.Order;
import co.com.manager.model.order.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
