package co.com.manager.tokenvalitador;

import co.com.manager.model.token.TokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenValidatorImpl implements TokenValidator {

    private final String webhookToken;

    public TokenValidatorImpl(@Value("${security.webhook-token}") String webhookToken) {
        this.webhookToken = webhookToken;
    }

    public boolean validate(String token) {
        return webhookToken.equals(token);
    }
}
