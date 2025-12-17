package co.com.manager.api.handler;

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

    public Mono<ServerResponse> apiStatus(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("OK");
    }

    public Mono<ServerResponse> verifyConnection(ServerRequest serverRequest) {
        Optional<String> challenge = serverRequest.queryParam("hub.challenge");
        Optional<String> verifyToken = serverRequest.queryParam("hub.verify_token");

        return ServerResponse.ok()
                .bodyValue(validateWebhookTokenUseCase.verifyToken(challenge, verifyToken));
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }
}
