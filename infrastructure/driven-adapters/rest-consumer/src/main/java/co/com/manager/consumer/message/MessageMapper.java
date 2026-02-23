package co.com.manager.consumer.message;

import co.com.manager.model.message.user.Contact;
import co.com.manager.model.message.user.Message;
import co.com.manager.model.message.user.Text;
import co.com.manager.model.message.user.UserMessageRequest;
import co.com.manager.model.message.user.UserMessageResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MessageMapper {

    public MessageRequest toRequest(UserMessageRequest domain) {
        return MessageRequest.builder()
                .messagingProduct(domain.getMessagingProduct())
                .recipientType(domain.getRecipientType())
                .to(domain.getTo())
                .type(domain.getType())
                .text(toTextDto(domain.getText()))
                .build();
    }

    public UserMessageResponse toDomain(MessageResponse dto) {
        return UserMessageResponse.builder()
                .messagingProduct(dto.getMessagingProduct())
                .contacts(toContactsDomain(dto.getContacts()))
                .messages(toMessagesDomain(dto.getMessages()))
                .build();
    }

    private TextDto toTextDto(Text text) {
        if (text == null) return null;
        return TextDto.builder()
                .previewUrl(text.getPreviewUrl())
                .body(text.getBody())
                .build();
    }

    private List<Contact> toContactsDomain(List<ContactDto> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .map(dto -> Contact.builder()
                        .input(dto.getInput())
                        .waId(dto.getWaId())
                        .build())
                .toList();
    }

    private List<Message> toMessagesDomain(List<MessageDto> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .map(dto -> Message.builder()
                        .id(dto.getId())
                        .build())
                .toList();
    }
}
