package hazardhub.com.hub.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDTO {

    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be <= 100 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must be <= 20 characters")
    private String phone;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be <= 255 characters")
    private String email;

    @Size(max = 50, message = "Relationship must be <= 50 characters")
    private String relationship;

    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be >= 1")
    @Builder.Default
    private Integer priority = 1;

    private Instant createdAt;
    private Instant updatedAt;
}
