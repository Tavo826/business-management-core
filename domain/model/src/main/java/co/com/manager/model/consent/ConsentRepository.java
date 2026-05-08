package co.com.manager.model.consent;

import reactor.core.publisher.Mono;

public interface ConsentRepository {

    Mono<Boolean> hasConsent(String businessNit, String customerPhone);
    Mono<ClientConsent> save(ClientConsent consent);
}
