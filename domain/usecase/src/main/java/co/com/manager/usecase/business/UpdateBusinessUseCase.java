package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.exceptions.BusinessNotFoundException;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UpdateBusinessUseCase {

    private final BusinessRepository repository;

    public Mono<Business> update(String id, Business business) {

        return repository.findBusinessById(id)
                .switchIfEmpty(Mono.error(new BusinessNotFoundException(id)))
                .map(actualBusiness -> {

                    actualBusiness.setName(business.getName());
                    actualBusiness.setDescription(business.getDescription());
                    actualBusiness.setPhone(verifyPhoneNumber(business.getPhone()));
                    actualBusiness.setEmail(business.getEmail());
                    actualBusiness.setAddress(business.getAddress());
                    actualBusiness.setPhoneNumberId(business.getPhoneNumberId());
                    actualBusiness.setSocialMediaList(business.getSocialMediaList());
                    actualBusiness.setBankAccountList(business.getBankAccountList());
                    actualBusiness.setUpdatedAt(LocalDateTime.now());

                    return actualBusiness;
                })
                .flatMap(repository::update);
    }

    private String verifyPhoneNumber(String phone) {

        if (!phone.startsWith("57"))
            return "57" + phone;

        return phone;

    }
}
