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
public class TextDto {

    @JsonProperty("preview_url")
    private boolean previewUrl;

    @NotBlank(message = "Text body is required")
    private String body;
}
