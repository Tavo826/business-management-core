package co.com.manager.consumer.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TextDto {

    @JsonProperty("preview_url")
    private Boolean previewUrl;
    private String body;
}
