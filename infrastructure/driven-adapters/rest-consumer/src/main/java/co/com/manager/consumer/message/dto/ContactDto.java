package co.com.manager.consumer.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ContactDto {

    private String input;
    @JsonProperty("wa_id")
    private String waId;
}
