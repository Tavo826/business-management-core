package co.com.manager.usecase.message;

import co.com.manager.model.message.webhook.ModelPort;
import co.com.manager.model.message.webhook.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMessageHandlerTest {

    @Mock
    private ModelPort modelPort;

    @InjectMocks
    private UserMessageHandler handler;

    @Test
    void shouldExtractMessageBodyAndSendToModel() {
        String messageBody = "Hello, I need help";
        String aiResponse = "Sure, how can I help you?";

        ClientMessage clientMessage = buildClientMessage(messageBody);

        when(modelPort.chat(messageBody)).thenReturn(Mono.just(aiResponse));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .assertNext(response -> assertEquals(aiResponse, response))
                .verifyComplete();

        verify(modelPort).chat(messageBody);
    }

    @Test
    void shouldPropagateErrorWhenModelFails() {
        String messageBody = "Hello";

        ClientMessage clientMessage = buildClientMessage(messageBody);

        when(modelPort.chat(messageBody)).thenReturn(Mono.error(new RuntimeException("AI service unavailable")));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectErrorMessage("AI service unavailable")
                .verify();
    }

    private ClientMessage buildClientMessage(String body) {
        Text text = Text.builder().body(body).build();
        Message message = Message.builder().text(text).build();
        Value value = Value.builder().messages(List.of(message)).build();
        Change change = Change.builder().value(value).build();
        Entry entry = Entry.builder().changes(List.of(change)).build();
        return ClientMessage.builder().entry(List.of(entry)).build();
    }
}