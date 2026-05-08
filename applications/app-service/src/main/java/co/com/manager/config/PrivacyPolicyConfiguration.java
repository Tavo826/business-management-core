package co.com.manager.config;

import co.com.manager.usecase.consent.PrivacyPolicyConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrivacyPolicyConfiguration {

    @Bean
    public PrivacyPolicyConfig privacyPolicyConfig(
            @Value("${privacy.policy.url}") String url,
            @Value("${privacy.policy.version}") String version,
            @Value("${privacy.policy.message}") String messageTemplate) {
        return new PrivacyPolicyConfig(url, version, messageTemplate);
    }
}
