package hazardhub.com.hub.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for partial updates to an emergency contact.
 *
 * <p>Optional fields are nullable and validated when provided.
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

    @Nullable
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @Nullable
    @Size(max = 50, message = "Relationship must be at most 50 characters")
    private String relationship;

    private Integer priority;
}
