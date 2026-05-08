package co.com.manager.usecase.message;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.consent.ClientConsent;
import co.com.manager.model.consent.ConsentRepository;
import co.com.manager.model.message.user.UserMessageResponse;
import co.com.manager.model.message.webhook.*;
import co.com.manager.usecase.consent.PrivacyPolicyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Mock
    private ConsentRepository consentRepository;

    private UserMessageHandlerUseCase handler;

    private static final String PHONE_NUMBER_ID = "123456";
    private static final String CUSTOMER_PHONE = "573001234567";
    private static final String BUSINESS_NIT = "900123456";
    private static final String POLICY_URL = "https://businessmanagement.store/privacy-policy";
    private static final String POLICY_VERSION = "v1";
    private static final String POLICY_TEMPLATE = "Aviso. Al continuar aceptas la política. Léela en {url}";

    @BeforeEach
    void setUp() {
        handler = new UserMessageHandlerUseCase(
                modelPort,
                sendMessageUseCase,
                businessRepository,
                consentRepository,
                new PrivacyPolicyConfig(POLICY_URL, POLICY_VERSION, POLICY_TEMPLATE));
    }

    @Test
    void shouldSendPrivacyNoticeAndRecordConsentOnFirstInteraction() {
        Business business = baseBusiness();
        ClientMessage clientMessage = buildClientMessage("Hola");
        UserMessageResponse expectedResponse = UserMessageResponse.builder().build();

        when(businessRepository.findByPhoneNumberId(PHONE_NUMBER_ID)).thenReturn(Mono.just(business));
        when(consentRepository.hasConsent(BUSINESS_NIT, CUSTOMER_PHONE)).thenReturn(Mono.just(false));
        when(sendMessageUseCase.sendMessage(eq(PHONE_NUMBER_ID), any())).thenReturn(Mono.just(expectedResponse));
        when(consentRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectNext(expectedResponse)
                .verifyComplete();

        ArgumentCaptor<ClientConsent> consentCaptor = ArgumentCaptor.forClass(ClientConsent.class);
        verify(consentRepository).save(consentCaptor.capture());
        ClientConsent saved = consentCaptor.getValue();
        assertThat(saved.getBusinessNit()).isEqualTo(BUSINESS_NIT);
        assertThat(saved.getCustomerPhone()).isEqualTo(CUSTOMER_PHONE);
        assertThat(saved.getPolicyVersion()).isEqualTo(POLICY_VERSION);
        assertThat(saved.getPolicyUrl()).isEqualTo(POLICY_URL);
        assertThat(saved.getNoticeSentAt()).isNotNull();

        verify(modelPort, never()).chat(any(), any(), any(), any());
    }

    @Test
    void shouldProcessWithModelWhenConsentAlreadyExists() {
        String messageBody = "Necesito ayuda";
        String aiResponse = "Claro, ¿con qué necesitas ayuda?";
        String clientId = BUSINESS_NIT + ":" + CUSTOMER_PHONE;

        Business business = baseBusiness();
        ClientMessage clientMessage = buildClientMessage(messageBody);
        UserMessageResponse expectedResponse = UserMessageResponse.builder().build();

        when(businessRepository.findByPhoneNumberId(PHONE_NUMBER_ID)).thenReturn(Mono.just(business));
        when(consentRepository.hasConsent(BUSINESS_NIT, CUSTOMER_PHONE)).thenReturn(Mono.just(true));
        when(modelPort.chat(messageBody, clientId, business, CUSTOMER_PHONE)).thenReturn(Mono.just(aiResponse));
        when(sendMessageUseCase.sendMessage(eq(PHONE_NUMBER_ID), any())).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(consentRepository, never()).save(any());
        verify(modelPort).chat(messageBody, clientId, business, CUSTOMER_PHONE);
    }

    @Test
    void shouldSendFallbackWhenBusinessNotFound() {
        ClientMessage clientMessage = buildClientMessage("Hola");
        UserMessageResponse expectedResponse = UserMessageResponse.builder().build();

        when(businessRepository.findByPhoneNumberId(PHONE_NUMBER_ID)).thenReturn(Mono.empty());
        when(sendMessageUseCase.sendMessage(eq(PHONE_NUMBER_ID), any())).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(modelPort, never()).chat(any(), any(), any(), any());
        verify(consentRepository, never()).save(any());
    }

    @Test
    void shouldSendFallbackWhenModelFails() {
        String messageBody = "Hola";
        String clientId = BUSINESS_NIT + ":" + CUSTOMER_PHONE;

        Business business = baseBusiness();
        ClientMessage clientMessage = buildClientMessage(messageBody);
        UserMessageResponse expectedResponse = UserMessageResponse.builder().build();

        when(businessRepository.findByPhoneNumberId(PHONE_NUMBER_ID)).thenReturn(Mono.just(business));
        when(consentRepository.hasConsent(BUSINESS_NIT, CUSTOMER_PHONE)).thenReturn(Mono.just(true));
        when(modelPort.chat(messageBody, clientId, business, CUSTOMER_PHONE))
                .thenReturn(Mono.error(new RuntimeException("AI service unavailable")));
        when(sendMessageUseCase.sendMessage(eq(PHONE_NUMBER_ID), any())).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(handler.handleMessage(clientMessage))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    private Business baseBusiness() {
        return Business.builder()
                .nit(BUSINESS_NIT)
                .name("Test Business")
                .phoneNumberId(PHONE_NUMBER_ID)
                .phone("573009999999")
                .description("A test business")
                .build();
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
