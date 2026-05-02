package co.com.manager.api.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private String businessId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemRequest> items;
}
