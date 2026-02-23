package co.com.manager.model.message.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageResponse {

    private String messagingProduct;
    private List<Contact> contacts;
    private List<Message> messages;
}
