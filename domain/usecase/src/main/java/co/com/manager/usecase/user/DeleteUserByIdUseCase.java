package co.com.manager.usecase.user;

import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class DeleteUserByIdUseCase {

    private UserRepository repository;
    private BusinessRepository businessRepository;

    public Mono<Void> delete(String id) {

        return repository.delete(id)
                .then(businessRepository.deleteAllBusinessByUserId(id));
    }
}
