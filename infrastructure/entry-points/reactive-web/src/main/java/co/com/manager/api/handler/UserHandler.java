package co.com.manager.api.handler;

import co.com.manager.api.dtos.user.AuthCredentialsRequest;
import co.com.manager.api.dtos.user.CreateUserRequest;
import co.com.manager.api.dtos.user.UpdateUserRequest;
import co.com.manager.api.dtos.user.UserMapper;
import co.com.manager.usecase.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final Validator validator;
    private final AuthUserUseCase authUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserByIdUseCase deleteUserByIdUseCase;

    public Mono<ServerResponse> authenticate(ServerRequest request) {

            return request.bodyToMono(AuthCredentialsRequest.class)
                    .flatMap(authCredentials -> {
                        Errors errors = new BeanPropertyBindingResult(authCredentials, AuthCredentialsRequest.class.getName());
                        validator.validate(authCredentials, errors);

                        if (errors.hasErrors()) {
                            return Mono.error(new ServerWebInputException(
                                    formatErrors(errors)
                            ));
                        }

                        return  authUserUseCase.authenticate(UserMapper.toEntity(authCredentials));
                    })
                    .flatMap(tokenResponse -> ServerResponse.ok()
                            .bodyValue(tokenResponse));
    }

    public Mono<ServerResponse> findUserById(ServerRequest request) {

        String id = request.pathVariable("id");

        return findUserByIdUseCase.findById(id)
                .map(UserMapper::toResponse)
                .flatMap(user -> ServerResponse.ok()
                        .bodyValue(user)
                );
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {

        return request.bodyToMono(CreateUserRequest.class)
                .flatMap(createUserRequest -> {
                    Errors errors = new BeanPropertyBindingResult(createUserRequest, CreateUserRequest.class.getName());
                    validator.validate(createUserRequest, errors);

                    if (errors.hasErrors()) {
                        return Mono.error(new ServerWebInputException(
                                formatErrors(errors)
                        ));
                    }

                    return createUserUseCase.create(UserMapper.toEntity(createUserRequest));
                })
                .flatMap(tokenResponse -> ServerResponse.status(HttpStatus.CREATED)
                            .bodyValue(tokenResponse)
                );
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {

        String id = request.pathVariable("id");

        return request.bodyToMono(UpdateUserRequest.class)
                .flatMap(updateUserRequest -> {
                    Errors errors = new BeanPropertyBindingResult(updateUserRequest, UpdateUserRequest.class.getName());
                    validator.validate(updateUserRequest, errors);

                    if (errors.hasErrors()) {
                        return Mono.error(new ServerWebInputException(
                                formatErrors(errors)
                        ));
                    }

                    return updateUserUseCase.update(id, UserMapper.toEntity(updateUserRequest));
                })
                .flatMap(tokenResponse -> ServerResponse.ok()
                        .bodyValue(tokenResponse)
                );
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {

        String id = request.pathVariable("id");

        return deleteUserByIdUseCase.delete(id)
                .then(ServerResponse.noContent().build());
    }

    private String formatErrors(Errors errors) {
        return errors.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
