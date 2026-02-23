package co.com.manager.api.dtos.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDto {

    @NotBlank(message = "Display phone number is required")
    @JsonProperty("display_phone_number")
    private String displayPhoneNumber;

    @NotBlank(message = "Phone number ID is required")
    @JsonProperty("phone_number_id")
    private String phoneNumberId;
}
