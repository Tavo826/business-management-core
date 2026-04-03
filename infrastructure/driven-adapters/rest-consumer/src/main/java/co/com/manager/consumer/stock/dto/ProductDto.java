package co.com.manager.consumer.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductDto {

    private int id;
    private String name;
    private BigDecimal price;
    private int stock;
    private String category;
    private String description;
    @JsonProperty("relevance_score")
    private int relevanceScore;
}
