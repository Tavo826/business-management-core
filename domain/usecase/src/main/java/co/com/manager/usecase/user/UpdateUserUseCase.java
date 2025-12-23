package co.com.manager.usecase.user;

import co.com.manager.model.exceptions.UserNotFoundException;
import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UpdateUserUseCase {

    private final UserRepository repository;

    public Mono<User> update(String id, User user) {

        return repository.findUserById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .map(actualUser -> {
                    actualUser.setName(user.getName());
                    actualUser.setSurname(user.getSurname());
                    actualUser.setEmail(user.getEmail());
                    actualUser.setBirthdate(user.getBirthdate());

                    return actualUser;
                })
                .flatMap(repository::update);
    }

    public Mono<User> updatePassword(String id, String password) {
        return repository.findUserById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .map(actualUser -> {
                    actualUser.setPassword(password);

                    return actualUser;
                })
                .flatMap(repository::updatePassword);
    }
}
