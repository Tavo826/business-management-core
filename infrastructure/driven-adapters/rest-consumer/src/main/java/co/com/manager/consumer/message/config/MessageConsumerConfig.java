package co.com.manager.consumer.message.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class MessageConsumerConfig {

    private final String url;
    private final int timeout;
    private final String accessToken;

    public MessageConsumerConfig(@Value("${adapters.restmessageconsumer.url}") String url,
                                 @Value("${adapters.restmessageconsumer.timeout}") int timeout,
                                 @Value("${adapters.restmessageconsumer.token}") String accessToken) {
        this.url = url;
        this.timeout = timeout;
        this.accessToken = accessToken;
    }

    @Bean(name = "messageWebClient")
    public WebClient getMessageWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .clientConnector(getMessageClientHttpConnector())
                .build();
    }

    private ClientHttpConnector getMessageClientHttpConnector() {

        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
    }

}
