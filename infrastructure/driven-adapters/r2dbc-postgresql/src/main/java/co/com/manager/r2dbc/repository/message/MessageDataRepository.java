package co.com.manager.r2dbc.repository.message;

import co.com.manager.r2dbc.data.message.MessageInfoData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageDataRepository extends ReactiveCrudRepository<MessageInfoData, String>, ReactiveQueryByExampleExecutor<MessageInfoData> {

    Flux<MessageInfoData> getAllByMessageId(String messageId);
}
