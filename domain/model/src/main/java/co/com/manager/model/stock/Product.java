package co.com.manager.model.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private int id;
    private String name;
    private BigDecimal price;
    private int stock;
    private String category;
    private String description;
    private int relevanceScore;
}
