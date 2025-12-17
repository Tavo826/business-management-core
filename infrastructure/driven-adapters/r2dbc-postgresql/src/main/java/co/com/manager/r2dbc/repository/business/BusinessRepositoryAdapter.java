package co.com.manager.r2dbc.repository.business;

import co.com.manager.model.business.Business;
import co.com.manager.r2dbc.data.BusinessData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BusinessRepositoryAdapter extends ReactiveAdapterOperations<
        Business,
        BusinessData,
        String,
        BusinessRepository
> {
    public BusinessRepositoryAdapter(BusinessRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Business.class));
    }

}
