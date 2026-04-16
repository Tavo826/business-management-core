package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@AllArgsConstructor
public class CreateBusinessUseCase {

    private final BusinessRepository repository;

    public Mono<Business> create(Business business) {

        return Mono.fromCallable(() -> {
            business.setPhone(verifyPhoneNumber(business.getPhone()));
            business.setPhoneNumberId("0");
            business.setCreatedAt(LocalDateTime.now());
            business.setUpdatedAt(LocalDateTime.now());
            return business;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(repository::create);
    }

    private String verifyPhoneNumber(String phone) {

        if (!phone.startsWith("57"))
            return "57" + phone;

        return phone;
    }
}
