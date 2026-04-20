package co.com.manager.consumer.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StockItems {

    private String name;
    @JsonProperty("purchased_quantity")
    private int purchasedQuantity;
}
