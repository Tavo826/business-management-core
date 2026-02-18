package co.com.manager.api.handler;

import co.com.manager.model.message.ClientMessage;
import co.com.manager.model.message.Message;
import co.com.manager.usecase.message.UserMessageHandler;
import co.com.manager.usecase.validation.ValidateWebhookTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final ValidateWebhookTokenUseCase validateWebhookTokenUseCase;
    private final UserMessageHandler userMessageHandler;

    public Mono<ServerResponse> apiStatus(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("OK");
    }

    public Mono<ServerResponse> verifyConnection(ServerRequest serverRequest) {
        Optional<String> challenge = serverRequest.queryParam("hub.challenge");
        Optional<String> verifyToken = serverRequest.queryParam("hub.verify_token");

        String challengeValue = challenge.orElse("");
        String verifyTokenValue = verifyToken.orElse("");

        return ServerResponse.ok()
                .bodyValue(validateWebhookTokenUseCase.verifyToken(challengeValue, verifyTokenValue));
    }

    public Mono<ServerResponse> handleUserMessage(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ClientMessage.class)
                .flatMap(userMessageHandler::handleMessage)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
}
