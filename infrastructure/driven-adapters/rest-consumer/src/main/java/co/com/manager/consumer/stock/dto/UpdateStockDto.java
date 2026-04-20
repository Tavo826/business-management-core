package co.com.manager.consumer.stock.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateStockDto {

    private List<StockItems> items;
}
