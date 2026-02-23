package co.com.manager.api.dtos.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValueDto {

    @NotBlank(message = "Messaging product is required")
    @JsonProperty("messaging_product")
    private String messagingProduct;

    @NotNull(message = "Metadata is required")
    @Valid
    private MetadataDto metadata;

    @NotEmpty(message = "Contacts list must not be empty")
    @Valid
    private List<ContactDto> contacts;

    @NotEmpty(message = "Messages list must not be empty")
    @Valid
    private List<MessageDto> messages;
}
