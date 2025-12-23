package co.com.manager.model.user;

import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> findUserById(String id);
    Mono<User> create(User user);
    Mono<User> update(User user);
    Mono<User> updatePassword(User user);
    Mono<Void> delete(String id);
}
