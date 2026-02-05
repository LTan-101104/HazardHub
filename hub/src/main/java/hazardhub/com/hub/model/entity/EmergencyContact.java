package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.BaseEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "emergency_contacts")
public class EmergencyContact extends BaseEntity {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotNull(message = "User ID is required")
    @Indexed
    @Field("user_id")
    private UUID userId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be <= 100 characters")
    @Field("name")
    private String name;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must be <= 20 characters")
    @Field("phone")
    private String phone;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be <= 255 characters")
    @Field("email")
    private String email;

    @Size(max = 50, message = "Relationship must be <= 50 characters")
    @Field("relationship")
    private String relationship;

    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be >= 1")
    @Field("priority")
    @Builder.Default
    private Integer priority = 1;
}
