package co.com.manager.usecase.user;

import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class DeleteUserByIdUseCase {

    private final UserRepository repository;
    private final BusinessRepository businessRepository;

    public Mono<Void> delete(String id) {

        return businessRepository.deleteAllBusinessByUserId(id)
                .then(repository.delete(id));
    }
}
