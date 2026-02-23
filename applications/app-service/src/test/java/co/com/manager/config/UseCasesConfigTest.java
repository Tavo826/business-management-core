package co.com.manager.config;

import co.com.manager.model.message.user.MessageGateway;
import co.com.manager.model.message.webhook.ModelPort;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.encoder.EncoderPort;
import co.com.manager.model.token.AuthenticationPort;
import co.com.manager.model.token.TokenValidator;
import co.com.manager.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase") || beanName.endsWith("Handler")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'UseCase' or 'Handler' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public BusinessRepository businessRepository() {
            return Mockito.mock(BusinessRepository.class);
        }

        @Bean
        public EncoderPort encoderPort() {
            return Mockito.mock(EncoderPort.class);
        }

        @Bean
        public AuthenticationPort authenticationPort() {
            return Mockito.mock(AuthenticationPort.class);
        }

        @Bean
        public ModelPort modelPort() {
            return Mockito.mock(ModelPort.class);
        }

        @Bean
        public MessageGateway messageGateway() {
            return Mockito.mock(MessageGateway.class);
        }

        @Bean
        public TokenValidator tokenValidator() {
            return Mockito.mock(TokenValidator.class);
        }
    }
}