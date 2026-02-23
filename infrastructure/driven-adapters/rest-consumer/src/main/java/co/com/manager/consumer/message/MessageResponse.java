package co.com.manager.consumer.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MessageResponse {

    @JsonProperty("messaging_product")
    private String messagingProduct;
    private List<ContactDto> contacts;
    private List<MessageDto> messages;
}