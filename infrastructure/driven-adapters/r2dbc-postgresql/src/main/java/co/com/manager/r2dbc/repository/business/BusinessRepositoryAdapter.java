package co.com.manager.r2dbc.repository.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.r2dbc.data.BusinessData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class BusinessRepositoryAdapter extends ReactiveAdapterOperations<
        Business,
        BusinessData,
        String,
        BusinessDataRepository
> implements BusinessRepository {
    public BusinessRepositoryAdapter(BusinessDataRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Business.class));
    }

    @Override
    public Mono<Business> findBusinessById(String id) {

        return findById(id);
    }

    @Override
    public Flux<Business> findAllBusinessByUserId(String userId) {

        return repository.findAllByOwnerDocumentId(userId);
    }

    @Override
    public Mono<Business> create(Business business) {

        return save(business);
    }

    @Override
    public Mono<Business> update(Business business) {

        return Mono.just(toData(business))
                .doOnNext(businessData -> businessData.setNew(false))
                .flatMap(this::saveData)
                .map(updatedBusiness -> mapper.mapBuilder(updatedBusiness, Business.BusinessBuilder.class).build());
    }

    @Override
    public Mono<Void> delete(String id) {

        return repository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAllBusinessByUserId(String userId) {

        return repository.deleteAllByOwnerDocumentId(userId);
    }
}
