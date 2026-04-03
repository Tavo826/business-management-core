package co.com.manager.r2dbc.repository.message;

import co.com.manager.model.message.persistence.MessageInfo;
import co.com.manager.model.message.persistence.PersistencePort;
import co.com.manager.r2dbc.data.message.MessageInfoData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class MessageRepositoryAdapter extends ReactiveAdapterOperations<
        MessageInfo,
        MessageInfoData,
        String,
        MessageDataRepository
> implements PersistencePort {


    public MessageRepositoryAdapter(MessageDataRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, MessageInfo.class));
    }

    @Override
    public Mono<MessageInfo> saveMessage(MessageInfo message) {

        log.info("Saving message {}", message.messageId);
        return save(message);
    }

    @Override
    public Flux<MessageInfo> getMessagesById(String messageId) {

        log.info("Retrieving messages by id {}", messageId);

        return repository.getAllByMessageId(messageId)
                .map(messageInfoData -> mapper.map(messageInfoData, MessageInfo.class));
    }
}
