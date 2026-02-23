package co.com.manager.api.dtos.message;

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
public class MessageDto {

    @NotBlank(message = "From is required")
    private String from;

    @NotBlank(message = "Message ID is required")
    private String id;

    @NotBlank(message = "Timestamp is required")
    private String timestamp;

    @NotNull(message = "Text is required")
    @Valid
    private TextDto text;

    @NotBlank(message = "Type is required")
    private String type;
}