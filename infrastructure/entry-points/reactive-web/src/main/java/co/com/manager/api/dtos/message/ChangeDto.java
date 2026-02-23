package co.com.manager.api.dtos.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDto {

    @NotNull(message = "Value is required")
    @Valid
    private ValueDto value;

    @NotBlank(message = "Field is required")
    private String field;
}
