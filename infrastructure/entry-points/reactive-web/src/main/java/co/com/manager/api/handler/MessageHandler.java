package co.com.manager.api.handler;

import co.com.manager.api.dtos.message.ClientMessageDto;
import co.com.manager.api.dtos.message.ClientMessageMapper;
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
    private final ClientMessageMapper clientMessageMapper;

    public Mono<ServerResponse> apiStatus(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("OK");
    }

    public Mono<ServerResponse> verifyConnection(ServerRequest serverRequest) {
        Optional<String> challenge = serverRequest.queryParam("hub.challenge");
        Optional<String> verifyToken = serverRequest.queryParam("hub.verify_token");

        String challengeValue = challenge.orElse("");
        String verifyTokenValue = verifyToken.orElse("");

        return validateWebhookTokenUseCase.verifyToken(challengeValue, verifyTokenValue)
                .flatMap(value -> ServerResponse.ok().bodyValue(value))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> handleUserMessage(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ClientMessageDto.class)
                .map(clientMessageMapper::toDomain)
                .flatMap(userMessageHandler::handleMessage)
                .flatMap(userMessage -> ServerResponse.ok().bodyValue(userMessage));
    }
}
