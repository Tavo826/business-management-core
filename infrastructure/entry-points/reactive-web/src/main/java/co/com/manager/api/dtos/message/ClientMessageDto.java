package co.com.manager.api.dtos.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientMessageDto {

    @NotBlank(message = "Object is required")
    private String object;

    @NotEmpty(message = "Entry list must not be empty")
    @Valid
    private List<EntryDto> entry;
}
