package co.com.manager.r2dbc.repository.user;

import co.com.manager.model.exceptions.UserNotFoundException;
import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import co.com.manager.r2dbc.data.UserData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserData,
        String,
        UserDataRepository
> implements UserRepository {

    private final PasswordEncoder passwordEncoder;

    public UserRepositoryAdapter(UserDataRepository repository, ObjectMapper mapper, PasswordEncoder passwordEncoder) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<User> findUserById(String id) {

        return findById(id);
    }

    @Override
    public Mono<User> create(User user) {

        return Mono.fromCallable(() -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return user;
        })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::save);
    }

    @Override
    public Mono<User> update(User user) {

        return Mono.just(toData(user))
                .doOnNext(userData -> userData.setNew(false))
                .flatMap(this::saveData)
                .map(updatedUser -> mapper.mapBuilder(updatedUser, User.UserBuilder.class).build());
    }

    @Override
    public Mono<User> updatePassword(User user) {

        return Mono.fromCallable(() -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return user;
        })
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::toData)
                .doOnNext(userData -> userData.setNew(false))
                .flatMap(this::saveData)
                .map(updatedUser -> mapper.mapBuilder(updatedUser, User.UserBuilder.class).build());
    }

    @Override
    public Mono<Void> delete(String id) {

        return repository.deleteById(id);
    }


}
