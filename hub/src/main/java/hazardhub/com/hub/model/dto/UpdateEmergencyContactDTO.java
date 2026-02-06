package hazardhub.com.hub.model.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * DTO for partial updates to an emergency contact.
 *
 * <p>
 * Clearable fields ({@code email}, {@code relationship}) use {@link Optional}
 * to distinguish three states:
 * <ul>
 * <li>{@code null} – field was absent from the request → skip (keep existing
 * value)</li>
 * <li>{@code Optional.empty()} – field was explicitly set to {@code null} →
 * clear the value</li>
 * <li>{@code Optional.of(value)} – field was provided with a value →
 * update</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmergencyContactDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Optional<@Email(message = "Email must be valid") @Size(max = 255, message = "Email must be at most 255 characters") String> email;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Optional<@Size(max = 50, message = "Relationship must be at most 50 characters") String> relationship;

    private Integer priority;
}
