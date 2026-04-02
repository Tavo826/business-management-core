package co.com.manager.consumer.stock;

import co.com.manager.consumer.stock.dto.ProductDto;
import co.com.manager.consumer.stock.dto.StockDto;
import co.com.manager.model.stock.Product;
import co.com.manager.model.stock.Stock;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StockMapper {

    public Stock toDomain(StockDto dto) {

        return Stock.builder()
                .query(dto.getQuery())
                .results(toDomain(dto.getResults()))
                .totalFound(dto.getTotalFound())
                .build();
    }

    private List<Product> toDomain(List<ProductDto> dtoList) {

        List<Product> products = new ArrayList<>();
        for (ProductDto dto : dtoList) {
            products.add(toDomain(dto));
        }

        return products;
    }

    private Product toDomain(ProductDto dto) {

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .relevanceScore(dto.getRelevanceScore())
                .build();
    }
}
