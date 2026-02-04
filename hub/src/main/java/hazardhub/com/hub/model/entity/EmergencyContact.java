package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.BaseEntity;
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

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "emergency_contacts")
public class EmergencyContact extends BaseEntity {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("name")
    private String name;

    @Field("phone")
    private String phone;

    @Field("email")
    private String email;

    @Field("relationship")
    private String relationship;

    @Field("priority")
    @Builder.Default
    private Integer priority = 1;
}
