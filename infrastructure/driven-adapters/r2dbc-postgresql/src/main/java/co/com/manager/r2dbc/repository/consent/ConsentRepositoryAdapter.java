package co.com.manager.r2dbc.repository.consent;

import co.com.manager.model.consent.ClientConsent;
import co.com.manager.model.consent.ConsentRepository;
import co.com.manager.r2dbc.data.consent.ConsentData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class ConsentRepositoryAdapter extends ReactiveAdapterOperations<
        ClientConsent,
        ConsentData,
        String,
        ConsentDataRepository
> implements ConsentRepository {

    public ConsentRepositoryAdapter(ConsentDataRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, ClientConsent.class));
    }

    @Override
    public Mono<Boolean> hasConsent(String businessNit, String customerPhone) {
        return repository.existsByBusinessNitAndCustomerPhone(businessNit, customerPhone);
    }

    @Override
    public Mono<ClientConsent> save(ClientConsent consent) {
        log.info("Recording consent for {} / {}", consent.getBusinessNit(), consent.getCustomerPhone());
        return super.save(consent);
    }
}
