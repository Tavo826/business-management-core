package co.com.manager.api.handler;

import co.com.manager.usecase.order.FindOrderUseCase;
import co.com.manager.usecase.order.UpdateOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final FindOrderUseCase findOrderUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;

    public Mono<ServerResponse> findAllOrdersByBusiness(ServerRequest request) {
        String status = request.queryParam("status").orElse(null);
        String businessId = request.pathVariable("businessId");

        if (status != null) {
            return findOrderUseCase.findByStatus(businessId, status)
                    .collectList()
                    .flatMap(orders -> ServerResponse.ok().bodyValue(orders));
        }

        return findOrderUseCase.findAllByBusinessId(businessId)
                .collectList()
                .flatMap(orders -> ServerResponse.ok().bodyValue(orders));
    }

    public Mono<ServerResponse> findOrderById(ServerRequest request) {
        String id = request.pathVariable("id");

        return findOrderUseCase.findById(id)
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateOrderStatus(ServerRequest request) {
        String id = request.pathVariable("id");

        return request.bodyToMono(Map.class)
                .map(body -> (String) body.get("status"))
                .flatMap(status -> updateOrderUseCase.updateStatus(id, status))
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
