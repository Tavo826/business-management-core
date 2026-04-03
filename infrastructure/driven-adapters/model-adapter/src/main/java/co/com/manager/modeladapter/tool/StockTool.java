package co.com.manager.modeladapter.tool;

import co.com.manager.model.stock.Product;
import co.com.manager.model.stock.Stock;
import co.com.manager.model.stock.StockGateway;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class StockTool {

    private final StockGateway stockGateway;

    @Tool("Busca productos en el inventario del negocio. Usa esta herramienta cuando el cliente pregunte por productos disponibles, precios, stock o catálogo.")
    public String searchProducts(
            @P("Consulta de búsqueda del producto, por ejemplo: 'camisetas', 'zapatos deportivos', 'talla M'") String query) {
        log.info("Tool invocado - buscando productos con query: {}", query);

        Stock stock = stockGateway.getStock(query).block();

        if (stock == null || stock.getResults() == null || stock.getResults().isEmpty()) {
            return "No se encontraron productos para la búsqueda: " + query;
        }

        String productList = stock.getResults().stream()
                .map(this::formatProduct)
                .collect(Collectors.joining("\n"));

        return String.format("Se encontraron %d productos:\n%s", stock.getTotalFound(), productList);
    }

    private String formatProduct(Product product) {
        return String.format("- %s | Precio: $%s | Disponibles: %d | Categoría: %s | %s",
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getDescription());
    }
}