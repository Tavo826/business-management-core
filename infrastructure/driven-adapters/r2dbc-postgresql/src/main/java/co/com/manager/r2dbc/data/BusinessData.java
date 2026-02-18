package co.com.manager.r2dbc.data;

import co.com.manager.model.bank.BankAccount;
import co.com.manager.model.social.SocialMedia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<SocialMediaData> socialMediaList;
    private List<BankAccountData> bankAccountList;
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
