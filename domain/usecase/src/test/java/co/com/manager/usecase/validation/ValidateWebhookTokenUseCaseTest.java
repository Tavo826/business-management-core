package co.com.manager.usecase.validation;

import co.com.manager.model.token.TokenValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateWebhookTokenUseCaseTest {

    @Mock
    private TokenValidator tokenValidator;

    @InjectMocks
    private ValidateWebhookTokenUseCase useCase;

    @Test
    void shouldReturnChallengeWhenTokenIsValid() {
        String token = "valid-token";
        String challenge = "challenge-123";

        when(tokenValidator.validate(token)).thenReturn(true);

        StepVerifier.create(useCase.verifyToken(Optional.of(challenge), Optional.of(token)))
                .assertNext(result -> assertEquals(challenge, result))
                .verifyComplete();

        verify(tokenValidator).validate(token);
    }

    @Test
    void shouldReturnEmptyStringWhenTokenIsValidButNoChallengeProvided() {
        String token = "valid-token";

        when(tokenValidator.validate(token)).thenReturn(true);

        StepVerifier.create(useCase.verifyToken(Optional.empty(), Optional.of(token)))
                .assertNext(result -> assertEquals("", result))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyMonoWhenTokenIsInvalid() {
        String token = "invalid-token";

        when(tokenValidator.validate(token)).thenReturn(false);

        StepVerifier.create(useCase.verifyToken(Optional.of("challenge"), Optional.of(token)))
                .verifyComplete();
    }
}