package co.com.manager.usecase.user;

import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class DeleteUserByIdUseCase {

    private UserRepository userRepository;

    public Mono<Void> delete(String id) {

        return userRepository.delete(id);
    }
}
