package co.com.manager.r2dbc.repository.business;

import co.com.manager.r2dbc.data.BusinessData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BusinessRepository extends ReactiveCrudRepository<BusinessData, String>, ReactiveQueryByExampleExecutor<BusinessData> {
}
