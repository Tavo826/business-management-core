package co.com.manager.r2dbc.data.consent;

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
@Table("client_consent")
public class ConsentData implements Persistable<String> {

    @Id
    private String id;
    private String businessNit;
    private String customerPhone;
    private String policyVersion;
    private String policyUrl;
    private LocalDateTime noticeSentAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public String getId() {
        return id;
    }
}
