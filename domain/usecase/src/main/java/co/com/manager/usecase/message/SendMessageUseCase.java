package co.com.manager.usecase.message;

import co.com.manager.model.message.user.MessageGateway;
import co.com.manager.model.message.user.UserMessageRequest;
import co.com.manager.model.message.user.UserMessageResponse;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class SendMessageUseCase {

    private final MessageGateway messageGateway;

    public Mono<UserMessageResponse> sendMessage(String phoneNumberId, UserMessageRequest request) {

        return messageGateway.sendMessage(phoneNumberId, request);
    }
}
