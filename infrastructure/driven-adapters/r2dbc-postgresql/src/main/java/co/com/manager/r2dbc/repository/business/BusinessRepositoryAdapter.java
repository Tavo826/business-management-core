package co.com.manager.r2dbc.repository.business;

import co.com.manager.model.bank.BankAccount;
import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.social.SocialMedia;
import co.com.manager.r2dbc.data.business.BusinessData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class BusinessRepositoryAdapter extends ReactiveAdapterOperations<
        Business,
        BusinessData,
        String,
        BusinessDataRepository
> implements BusinessRepository {

    private static final TypeReference<List<SocialMedia>> SOCIAL_MEDIA_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<BankAccount>> BANK_ACCOUNT_TYPE = new TypeReference<>() {};

    private final ObjectMapper jacksonMapper;

    public BusinessRepositoryAdapter(BusinessDataRepository repository,
                                     org.reactivecommons.utils.ObjectMapper mapper,
                                     ObjectMapper jacksonMapper) {
        // jacksonMapper is injected by Spring; org.reactivecommons.utils.ObjectMapper is the reactive-commons mapper
        super(repository, mapper, d -> null);
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    protected Business toEntity(BusinessData data) {
        if (data == null) return null;
        return Business.builder()
                .nit(data.getNit())
                .name(data.getName())
                .description(data.getDescription())
                .phone(data.getPhone())
                .email(data.getEmail())
                .address(data.getAddress())
                .ownerDocumentId(data.getOwnerDocumentId())
                .phoneNumberId(data.getPhoneNumberId())
                .socialMediaList(deserializeSocialMedia(data.getSocialMediaList()))
                .bankAccountList(deserializeBankAccount(data.getBankAccountList()))
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .build();
    }

    @Override
    protected BusinessData toData(Business entity) {
        return BusinessData.builder()
                .nit(entity.getNit())
                .name(entity.getName())
                .description(entity.getDescription())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .ownerDocumentId(entity.getOwnerDocumentId())
                .phoneNumberId(entity.getPhoneNumberId())
                .socialMediaList(serializeToJson(entity.getSocialMediaList()))
                .bankAccountList(serializeToJson(entity.getBankAccountList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Mono<Business> findBusinessById(String id) {

        log.info("Finding business by id {}", id);

        return findById(id);
    }

    @Override
    public Mono<Business> findByPhoneNumberId(String phoneNumberId) {

        log.info("Finding business by phoneNumberId {}", phoneNumberId);

        return repository.findByPhoneNumberId(phoneNumberId)
                .map(this::toEntity);
    }

    @Override
    public Flux<Business> findAllBusinessByUserId(String userId) {

        log.info("Finding business by user id {}", userId);

        return repository.findAllByOwnerDocumentId(userId)
                .map(this::toEntity);
    }

    @Override
    public Mono<Business> create(Business business) {

        log.info("Creating business");

        return save(business);
    }

    @Override
    public Mono<Business> update(Business business) {

        log.info("Updating business");

        return Mono.just(toData(business))
                .doOnNext(businessData -> businessData.setNew(false))
                .flatMap(this::saveData)
                .map(this::toEntity);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.info("Deleting business with id {}", id);
        return repository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAllBusinessByUserId(String userId) {
        log.info("Deleting business by user id {}", userId);
        return repository.deleteAllByOwnerDocumentId(userId);
    }

    private Json serializeToJson(Object value) {
        if (value == null) return Json.of("[]");
        try {
            return Json.of(jacksonMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException e) {
            return Json.of("[]");
        }
    }

    private List<SocialMedia> deserializeSocialMedia(Json json) {
        if (json == null) return Collections.emptyList();
        try {
            return jacksonMapper.readValue(json.asArray(), SOCIAL_MEDIA_TYPE);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<BankAccount> deserializeBankAccount(Json json) {
        if (json == null) return Collections.emptyList();
        try {
            return jacksonMapper.readValue(json.asArray(), BANK_ACCOUNT_TYPE);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}