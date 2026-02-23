package co.com.manager.api.dtos.message;

import co.com.manager.model.message.webhook.Change;
import co.com.manager.model.message.webhook.ClientMessage;
import co.com.manager.model.message.webhook.Contact;
import co.com.manager.model.message.webhook.Entry;
import co.com.manager.model.message.webhook.Message;
import co.com.manager.model.message.webhook.Metadata;
import co.com.manager.model.message.webhook.Profile;
import co.com.manager.model.message.webhook.Text;
import co.com.manager.model.message.webhook.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientMessageMapper {

    public ClientMessage toDomain(ClientMessageDto dto) {
        return ClientMessage.builder()
                .object(dto.getObject())
                .entry(mapEntries(dto.getEntry()))
                .build();
    }

    private List<Entry> mapEntries(List<EntryDto> dtos) {
        return dtos.stream().map(this::mapEntry).toList();
    }

    private Entry mapEntry(EntryDto dto) {
        return Entry.builder()
                .Id(dto.getId())
                .changes(mapChanges(dto.getChanges()))
                .build();
    }

    private List<Change> mapChanges(List<ChangeDto> dtos) {
        return dtos.stream().map(this::mapChange).toList();
    }

    private Change mapChange(ChangeDto dto) {
        return Change.builder()
                .value(mapValue(dto.getValue()))
                .field(dto.getField())
                .build();
    }

    private Value mapValue(ValueDto dto) {
        return Value.builder()
                .messagingProduct(dto.getMessagingProduct())
                .metadata(mapMetadata(dto.getMetadata()))
                .contacts(mapContacts(dto.getContacts()))
                .messages(mapMessages(dto.getMessages()))
                .build();
    }

    private Metadata mapMetadata(MetadataDto dto) {
        return Metadata.builder()
                .displayPhoneNumber(dto.getDisplayPhoneNumber())
                .phoneNumberId(dto.getPhoneNumberId())
                .build();
    }

    private List<Contact> mapContacts(List<ContactDto> dtos) {
        return dtos.stream().map(this::mapContact).toList();
    }

    private Contact mapContact(ContactDto dto) {
        return Contact.builder()
                .profile(mapProfile(dto.getProfile()))
                .whatsappId(dto.getWhatsappId())
                .build();
    }

    private Profile mapProfile(ProfileDto dto) {
        return Profile.builder()
                .name(dto.getName())
                .build();
    }

    private List<Message> mapMessages(List<MessageDto> dtos) {
        return dtos.stream().map(this::mapMessage).toList();
    }

    private Message mapMessage(MessageDto dto) {
        return Message.builder()
                .from(dto.getFrom())
                .id(dto.getId())
                .timestamp(dto.getTimestamp())
                .text(mapText(dto.getText()))
                .type(dto.getType())
                .build();
    }

    private Text mapText(TextDto dto) {
        return Text.builder()
                .previewUrl(dto.isPreviewUrl())
                .body(dto.getBody())
                .build();
    }
}
