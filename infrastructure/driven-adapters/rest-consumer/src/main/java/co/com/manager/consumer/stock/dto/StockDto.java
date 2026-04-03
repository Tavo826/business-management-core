package co.com.manager.consumer.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StockDto {

    private String query;
    private List<ProductDto> results;
    @JsonProperty("total_found")
    private int totalFound;
}
