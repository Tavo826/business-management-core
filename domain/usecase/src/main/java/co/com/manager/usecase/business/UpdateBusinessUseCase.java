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
                    var actualSocialMediaList = actualBusiness.getSocialMediaList();
                    var actualBankAccountList = actualBusiness.getBankAccountList();

                    actualSocialMediaList.addAll(business.getSocialMediaList());
                    actualBankAccountList.addAll(business.getBankAccountList());

                    actualBusiness.setName(business.getName());
                    actualBusiness.setDescription(business.getDescription());
                    actualBusiness.setPhone(business.getPhone());
                    actualBusiness.setEmail(business.getEmail());
                    actualBusiness.setAddress(business.getAddress());
                    actualBusiness.setSocialMediaList(actualSocialMediaList);
                    actualBusiness.setBankAccountList(actualBankAccountList);
                    actualBusiness.setUpdatedAt(LocalDateTime.now());

                    return actualBusiness;
                })
                .flatMap(repository::update);
    }
}
