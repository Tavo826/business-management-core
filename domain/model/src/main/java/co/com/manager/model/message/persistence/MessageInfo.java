package co.com.manager.model.message.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageInfo {

    public String id;
    public String messageId;
    public String receivedMessage;
    public String senderPhone;
    public String responseMessage;
    public LocalDateTime sentTime;
}
