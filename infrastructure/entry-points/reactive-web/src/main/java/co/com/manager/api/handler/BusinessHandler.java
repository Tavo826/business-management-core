package co.com.manager.api.handler;

import co.com.manager.model.business.Business;
import co.com.manager.usecase.business.CreateBusinessUseCase;
import co.com.manager.usecase.business.DeleteBusinessByIdUseCase;
import co.com.manager.usecase.business.FindBusinessUseCase;
import co.com.manager.usecase.business.UpdateBusinessUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BusinessHandler {

    private final CreateBusinessUseCase createBusinessUseCase;
    private final FindBusinessUseCase findBusinessUseCase;
    private final UpdateBusinessUseCase updateBusinessUseCase;
    private final DeleteBusinessByIdUseCase deleteBusinessByIdUseCase;

    public Mono<ServerResponse> findBusinessById(ServerRequest request) {

        String id = request.pathVariable("id");

        return findBusinessUseCase.findById(id)
                .flatMap(business -> ServerResponse.ok()
                        .bodyValue(business)
                );
    }

    public Mono<ServerResponse> findBusinessByUserId(ServerRequest request) {

        String id = request.pathVariable("id");

        return findBusinessUseCase.findAllByUserId(id)
                .collectList()
                .flatMap(businessList -> ServerResponse.ok()
                        .bodyValue(businessList)
                );
    }

    public Mono<ServerResponse> createBusiness(ServerRequest request) {

        return request.bodyToMono(Business.class)
                .flatMap(createBusinessUseCase::create)
                .flatMap(createdBusiness -> ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(createdBusiness)
                );
    }

    public Mono<ServerResponse> updateBusiness(ServerRequest request) {

        String id = request.pathVariable("id");

        return request.bodyToMono(Business.class)
                .flatMap(business -> updateBusinessUseCase.update(id, business))
                .flatMap(updatedBusiness -> ServerResponse.ok()
                        .bodyValue(updatedBusiness)
                );
    }

    public Mono<ServerResponse> deleteBusiness(ServerRequest request) {

        String id = request.pathVariable("id");

        return deleteBusinessByIdUseCase.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
