package co.com.manager.api.dtos.user;

import co.com.manager.model.token.AuthCredentials;
import co.com.manager.model.user.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class UserMapper {

    public static User toEntity(CreateUserRequest request) {

        return User.builder()
                .documentId(request.getDocumentId())
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(decodeBase64(request.getPassword()))
                .birthdate(request.getBirthdate())
                .build();
    }

    public static User toEntity(UpdateUserRequest request) {

        return User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .birthdate(request.getBirthdate())
                .build();
    }

    public static AuthCredentials toEntity(AuthCredentialsRequest request) {

        return AuthCredentials.builder()
                .email(request.getEmail())
                .password(decodeBase64(request.getPassword()))
                .build();
    }

    private static String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    public static UserResponse toResponse(User user) {

        return UserResponse.builder()
                .documentId(user.getDocumentId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .birthdate(user.getBirthdate())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
