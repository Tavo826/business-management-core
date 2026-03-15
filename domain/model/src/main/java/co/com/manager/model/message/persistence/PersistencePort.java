package co.com.manager.model.message.persistence;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersistencePort {

    Mono<MessageInfo> saveMessage(MessageInfo messageInfo);
    Flux<MessageInfo> getMessagesById(String messageId);
}
