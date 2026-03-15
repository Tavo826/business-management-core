package co.com.manager.r2dbc.repository.message;

import co.com.manager.model.message.persistence.MessageInfo;
import co.com.manager.model.message.persistence.PersistencePort;
import co.com.manager.r2dbc.data.message.MessageInfoData;
import co.com.manager.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        return save(message);
    }

    @Override
    public Flux<MessageInfo> getMessagesById(String messageId) {

        return repository.getAllByMessageId(messageId)
                .map(messageInfoData -> mapper.map(messageInfoData, MessageInfo.class));
    }
}
