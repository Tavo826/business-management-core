package co.com.manager.r2dbc.repository.user;

import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import co.com.manager.r2dbc.data.user.UserData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserData,
        String,
        UserDataRepository
> implements UserRepository {

    public UserRepositoryAdapter(UserDataRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> findUserById(String id) {

        return findById(id);
    }

    @Override
    public Mono<User> findUserByEmail(String email) {

        return repository.findByEmail(email)
                .map(this::toEntity);
    }

    @Override
    public Mono<User> create(User user) {

        return save(user);
    }

    @Override
    public Mono<User> update(User user) {

        return Mono.just(toData(user))
                .doOnNext(userData -> userData.setNew(false))
                .flatMap(this::saveData)
                .map(updatedUser -> mapper.mapBuilder(updatedUser, User.UserBuilder.class).build());
    }

    @Override
    public Mono<Void> delete(String id) {

        return repository.deleteById(id);
    }


}
