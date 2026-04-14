package co.com.manager.usecase.message;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.message.user.Text;
import co.com.manager.model.message.user.UserMessageRequest;
import co.com.manager.model.message.user.UserMessageResponse;
import co.com.manager.model.message.webhook.ClientMessage;
import co.com.manager.model.message.webhook.ModelPort;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public class UserMessageHandlerUseCase {

    private static final String FALLBACK_MESSAGE =
            "Lo sentimos, el asistente no está disponible en este momento. Alguien se comunicará contigo en breve.";

    private static final long CACHE_TTL_MILLIS = 24 * 60 * 60 * 1000L;

    private final ModelPort modelPort;
    private final SendMessageUseCase sendMessageUseCase;
    private final BusinessRepository businessRepository;

    private final ConcurrentHashMap<String, CachedBusiness> businessCache = new ConcurrentHashMap<>();

    public UserMessageHandlerUseCase(ModelPort modelPort,
                                     SendMessageUseCase sendMessageUseCase,
                                     BusinessRepository businessRepository) {
        this.modelPort = modelPort;
        this.sendMessageUseCase = sendMessageUseCase;
        this.businessRepository = businessRepository;
    }

    private record CachedBusiness(Business business, long expiresAt) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

    public Mono<UserMessageResponse> handleMessage(ClientMessage clientMessage) {

        String messageBody = clientMessage.getEntry().getFirst()
                .getChanges().getFirst()
                .getValue()
                .getMessages().getFirst()
                .getText()
                .getBody();

        String phoneNumberId = clientMessage.getEntry().getFirst()
                .getChanges().getFirst()
                .getValue()
                .getMetadata()
                .getPhoneNumberId();

        String phoneNumber = clientMessage.getEntry().getFirst()
                .getChanges().getFirst()
                .getValue()
                .getMessages().getFirst()
                .getFrom();

        return findBusinessCached(phoneNumberId)
                .flatMap(business -> {
                    String clientId = business.getNit() + ":" + phoneNumber;
                    return modelPort.chat(messageBody, clientId, business);
                })
                .switchIfEmpty(Mono.just(FALLBACK_MESSAGE))
                .flatMap(modelResponse -> sendMessageResponse(phoneNumberId, phoneNumber, modelResponse))
                .onErrorResume(e -> sendMessageResponse(phoneNumberId, phoneNumber, FALLBACK_MESSAGE));
    }

    private Mono<Business> findBusinessCached(String phoneNumberId) {
        CachedBusiness cached = businessCache.get(phoneNumberId);
        if (cached != null && !cached.isExpired()) {
            return Mono.just(cached.business());
        }
        if (cached != null) {
            businessCache.remove(phoneNumberId);
        }
        return businessRepository.findByPhoneNumberId(phoneNumberId)
                .doOnNext(business -> businessCache.put(phoneNumberId,
                        new CachedBusiness(business, System.currentTimeMillis() + CACHE_TTL_MILLIS)));
    }

    private Mono<UserMessageResponse> sendMessageResponse(String phoneNumberId, String phoneNumber, String body) {
        UserMessageRequest userMessageRequest = UserMessageRequest.builder()
                .messagingProduct("whatsapp")
                .recipientType("individual")
                .to(phoneNumber)
                .type("text")
                .text(Text.builder()
                        .previewUrl(false)
                        .body(body)
                        .build())
                .build();

        return sendMessageUseCase.sendMessage(phoneNumberId, userMessageRequest);
    }
}
