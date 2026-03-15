package co.com.manager.r2dbc.data.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("messages")
public class MessageInfoData {

    @Id
    public String id;
    public String messageId;
    public String receivedMessage;
    public String senderPhone;
    public String responseMessage;
    public LocalDateTime sentTime;
}
