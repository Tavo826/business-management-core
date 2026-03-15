package co.com.manager.r2dbc.data.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserData implements Persistable<String> {

    @Id
    @Column("document_id")
    private String documentId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String birthdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public String getId() {
        return documentId;
    }
}
