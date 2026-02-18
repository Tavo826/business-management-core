package co.com.manager.usecase.user;

import co.com.manager.model.exceptions.UserNotFoundException;
import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class FindUserByIdUseCase {

    private final UserRepository repository;

    public Mono<User> findById(String id) {

        return repository.findUserById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("[id: " + id + "]")));
    }
}
