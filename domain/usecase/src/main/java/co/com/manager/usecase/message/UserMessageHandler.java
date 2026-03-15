package co.com.manager.usecase.message;

import co.com.manager.model.message.persistence.MessageInfo;
import co.com.manager.model.message.persistence.PersistencePort;
import co.com.manager.model.message.user.MessageGateway;
import co.com.manager.model.message.user.Text;
import co.com.manager.model.message.user.UserMessageRequest;
import co.com.manager.model.message.user.UserMessageResponse;
import co.com.manager.model.message.webhook.ModelPort;
import co.com.manager.model.message.webhook.ClientMessage;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UserMessageHandler {

    private static final String FALLBACK_MESSAGE =
            "Lo sentimos, el asistente no está disponible en este momento. Alguien se comunicará contigo en breve.";

    private final ModelPort modelPort;
    private final MessageGateway messageGateway;

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

        return modelPort.chat(messageBody, phoneNumber)
                .flatMap(modelResponse -> sendMessage(phoneNumberId, phoneNumber, modelResponse))
                .onErrorResume(e -> sendMessage(phoneNumberId, phoneNumber, FALLBACK_MESSAGE));
    }

    private Mono<UserMessageResponse> sendMessage(String phoneNumberId, String phoneNumber, String body) {
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

        return messageGateway.sendMessage(phoneNumberId, userMessageRequest);
    }
}
