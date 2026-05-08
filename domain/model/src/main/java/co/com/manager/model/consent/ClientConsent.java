package co.com.manager.model.consent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientConsent {

    private String id;
    private String businessNit;
    private String customerPhone;
    private String policyVersion;
    private String policyUrl;
    private LocalDateTime noticeSentAt;

    public static String buildId(String businessNit, String customerPhone) {
        return businessNit + ":" + customerPhone;
    }
}
