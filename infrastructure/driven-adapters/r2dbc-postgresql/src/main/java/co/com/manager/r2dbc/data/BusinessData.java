package co.com.manager.r2dbc.data;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("business")
public class BusinessData implements Persistable<String> {

    @Id
    private String nit;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String address;
    private String ownerDocumentId;
    private Json socialMediaList;
    private Json bankAccountList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public String getId() {
        return nit;
    }
}
