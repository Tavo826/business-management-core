package co.com.manager.usecase.message;

import co.com.manager.model.ai.ModelPort;
import co.com.manager.model.message.ClientMessage;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UserMessageHandler {

    private final ModelPort modelPort;

    public Mono<String> handleMessage(ClientMessage clientMessage) {
        return modelPort.chat(clientMessage.getEntry().getFirst()
                .getChanges().getFirst()
                .getValue()
                .getMessages().getFirst()
                .getText()
                .getBody()
        );
    }
}
