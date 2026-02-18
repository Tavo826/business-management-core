package co.com.manager.api.security;

import co.com.manager.model.exceptions.InvalidTokenException;
import co.com.manager.model.token.AuthenticationPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final AuthenticationPort authenticationPort;
    private final ObjectMapper objectMapper;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/status",
            "/webhook",
            "/api/users",
            "/api/users/auth"
    );

    public JwtAuthenticationFilter(AuthenticationPort authenticationPort, ObjectMapper objectMapper) {
        this.authenticationPort = authenticationPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        if (isPublicPath(exchange.getRequest().getPath().value())) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);

        if (token == null) {
            return handleMissingToken(exchange);
        }

        return processToken(token)
                .flatMap(auth -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .onErrorResume(InvalidTokenException.class, ex -> handleInvalidToken(exchange, ex));
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::equals);
    }

    private String extractToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private Mono<UsernamePasswordAuthenticationToken> processToken(String token) {

        return authenticationPort.validateToken(token)
                .filter(aBoolean -> aBoolean)
                .switchIfEmpty(Mono.error(new Throwable("Validación del tiempo de expiración fallida")))
                .flatMap(aBoolean -> authenticationPort.extractMail(token))
                .filter(StringUtils::hasText)
                .switchIfEmpty(Mono.error(new Throwable("La extracción de correo electrónico falló o está vacía")))
                .map(mail -> new UsernamePasswordAuthenticationToken(mail, null, Collections.emptyList()))
                .onErrorResume(throwable -> Mono.error(new InvalidTokenException(throwable.getMessage())));
    }

    private Mono<Void> handleMissingToken(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "No se proporcionó un token de autenticación. Incluya un Bearer token válido en el encabezado de autorización"
        );
        problem.setTitle("Missing Authentication Token");

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(problem);
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Mono<Void> handleInvalidToken(ServerWebExchange exchange, InvalidTokenException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
        problem.setTitle("Invalid Token");

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(problem);
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
