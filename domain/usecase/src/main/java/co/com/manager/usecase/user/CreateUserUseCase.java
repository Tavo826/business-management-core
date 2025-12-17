package co.com.manager.usecase.user;

import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class CreateUserUseCase {

    private final UserRepository repository;

    public Mono<User> create(User user) {

        return repository.create(user);
    }
}
