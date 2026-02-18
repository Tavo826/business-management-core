package co.com.manager.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Value {

    private String messagingProduct;
    private Metadata metadata;
    private List<Contact> contacts;
    private List<Message> messages;
}
