package co.com.manager.usecase.business;

import co.com.manager.model.business.BusinessRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class DeleteBusinessByIdUseCase {

    private BusinessRepository repository;

    public Mono<Void> delete(String id) {

        return repository.delete(id);
    }
}
