package co.com.manager.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String documentId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String birthdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
