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
public class EntryDto {

    @NotBlank(message = "Entry ID is required")
    private String id;

    @NotEmpty(message = "Changes list must not be empty")
    @Valid
    private List<ChangeDto> changes;
}
