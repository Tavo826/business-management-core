package co.com.manager.model.business;

import co.com.manager.model.bank.BankAccount;
import co.com.manager.model.social.SocialMedia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    private String nit;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String address;
    private String ownerDocumentId;
    private List<SocialMedia> socialMediaList;
    private List<BankAccount> bankAccountList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
