package co.com.manager.model.message.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageRequest {

    private String messagingProduct;
    private String recipientType;
    private String to;
    private String type;
    private Text text;
}
