package co.com.manager.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {

    private String displayPhoneNumber;
    private String phoneNumberId;
}
