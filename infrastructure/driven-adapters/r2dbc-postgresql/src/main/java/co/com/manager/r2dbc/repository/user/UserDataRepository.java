package co.com.manager.r2dbc.repository.user;

import co.com.manager.r2dbc.data.UserData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserDataRepository extends ReactiveCrudRepository<UserData, String>, ReactiveQueryByExampleExecutor<UserData> {

    Mono<UserData> findByEmail(String email);
}
