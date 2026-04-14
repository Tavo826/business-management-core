package co.com.manager.usecase.message;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.message.user.UserMessageResponse;
import co.com.manager.model.message.webhook.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMessageHandlerUseCaseTest {

    @Mock
    private ModelPort modelPort;

    @Mock
    private SendMessageUseCase sendMessageUseCase;

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private UserMessageHandlerUseCase handler;

    private static final String PHONE_NUMBER_ID = "123456";
    private static final String CUSTOMER_PHONE = "573001234567";
    private static final String BUSINESS_NIT = "900123456";

    @Test
    void shouldResolveBusinessAndSendToModel() {
        String messageBody = "Hello, I need help";
        String aiResponse = "Sure, how can I help you?";
        String clientId = BUSINESS_NIT + ":" + CUSTOMER_PHONE;

        Business business = Business.builder()
                .nit(BUSINESS_NIT)
                .name("Test Business")
                .phoneNumberId(PHONE_NUMBER_ID)
                .phone("573009999999")
                .description("A test business")
                .build();

        ClientMessage clientMessage = buildClientMessage(messageBody);

        UserMessageResponse expectedResponse = UserMessageResponse.builder().build();

        when(businessRepository.findByPhoneNumberId(PHONE_NUMBER_ID)).thenReturn(Mono.just(business));
        when(modelPort.chat(messageBody, clientId, business)).thenReturn(Mono.just(aiResponse));
        when(sendMessageUseCase.sendMessage(eq(PHONE_NUMBER_ID), any())).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(businessRepository).findByPhoneNumberId(PHONE_NUMBER_ID);
        verify(modelPort).chat(messageBody, clientId, business);
    }

    @Test
    void shouldSendFallbackWhenBusinessNotFound() {
        String messageBody = "Hello";

        ClientMessage clientMessage = buildClientMessage(messageBody);

        UserMessageResponse expectedResponse = UserMessageResponse.builder().build();

        when(businessRepository.findByPhoneNumberId(PHONE_NUMBER_ID)).thenReturn(Mono.empty());
        when(sendMessageUseCase.sendMessage(eq(PHONE_NUMBER_ID), any())).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(modelPort, never()).chat(any(), any(), any());
    }

    @Test
    void shouldSendFallbackWhenModelFails() {
        String messageBody = "Hello";
        String clientId = BUSINESS_NIT + ":" + CUSTOMER_PHONE;

        Business business = Business.builder()
                .nit(BUSINESS_NIT)
                .name("Test Business")
                .phoneNumberId(PHONE_NUMBER_ID)
                .build();

        ClientMessage clientMessage = buildClientMessage(messageBody);

        UserMessageResponse expectedResponse = UserMessageResponse.builder().build();

        when(businessRepository.findByPhoneNumberId(PHONE_NUMBER_ID)).thenReturn(Mono.just(business));
        when(modelPort.chat(messageBody, clientId, business)).thenReturn(Mono.error(new RuntimeException("AI service unavailable")));
        when(sendMessageUseCase.sendMessage(eq(PHONE_NUMBER_ID), any())).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    private ClientMessage buildClientMessage(String body) {
        Text text = Text.builder().body(body).build();
        Message message = Message.builder().text(text).from(CUSTOMER_PHONE).build();
        Metadata metadata = Metadata.builder().phoneNumberId(PHONE_NUMBER_ID).build();
        Value value = Value.builder()
                .messages(List.of(message))
                .metadata(metadata)
                .build();
        Change change = Change.builder().value(value).build();
        Entry entry = Entry.builder().changes(List.of(change)).build();
        return ClientMessage.builder().entry(List.of(entry)).build();
    }
}
