package co.com.manager.model.message.user;

import reactor.core.publisher.Mono;

public interface MessageGateway {

    Mono<UserMessageResponse> sendMessage(String phoneNumberId, UserMessageRequest request);
}
