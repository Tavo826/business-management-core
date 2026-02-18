package co.com.manager.jwtadapter;

import co.com.manager.model.token.AuthenticationPort;
import co.com.manager.model.token.AuthResponse;
import co.com.manager.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtAdapter implements AuthenticationPort {

    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtAdapter(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.expiration:86400000}") long expirationTime) {
                this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
                this.expirationTime = expirationTime;
    }

    @Override
    public Mono<AuthResponse> authenticate(User user) {

        String token = generateToken(user);

        return Mono.just(AuthResponse.builder()
                .token(token)
                .build());
    }

    @Override
    public Mono<Boolean> validateToken(String token) {

        try {
            extractClaims(token);
            return Mono.just(!isTokenExpired(token));
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    @Override
    public Mono<String> extractMail(String token) {

        return Mono.just(extractClaims(token).getSubject());
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("documentId", user.getDocumentId())
                .claim("name", user.getName())
                .claim("surname", user.getSurname())
                .claim("birthdate", user.getBirthdate())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }
}
