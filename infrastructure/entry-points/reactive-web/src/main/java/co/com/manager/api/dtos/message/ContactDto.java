package co.com.manager.api.dtos.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    @NotNull(message = "Profile is required")
    @Valid
    private ProfileDto profile;

    @NotBlank(message = "WhatsApp ID is required")
    @JsonProperty("wa_id")
    private String whatsappId;
}