package co.com.manager.api.handler;

import co.com.manager.model.user.User;
import co.com.manager.usecase.user.CreateUserUseCase;
import co.com.manager.usecase.user.DeleteUserByIdUseCase;
import co.com.manager.usecase.user.FindUserByIdUseCase;
import co.com.manager.usecase.user.UpdateUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final FindUserByIdUseCase findUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserByIdUseCase deleteUserByIdUseCase;

    public Mono<ServerResponse> findUserById(ServerRequest request) {

        String id = request.pathVariable("id");

        return findUserByIdUseCase.findById(id)
                .flatMap(user -> ServerResponse.ok()
                        .bodyValue(user)
                );
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {

        return request.bodyToMono(User.class)
                .flatMap(createUserUseCase::create)
                .flatMap(createdUser -> ServerResponse.status(HttpStatus.CREATED)
                            .bodyValue(createdUser)
                );
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {

        String id = request.pathVariable("id");

        return request.bodyToMono(User.class)
                .flatMap(user -> updateUserUseCase.update(id, user))
                .flatMap(updatedUser -> ServerResponse.ok()
                        .bodyValue(updatedUser)
                );
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {

        String id = request.pathVariable("id");

        return deleteUserByIdUseCase.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
