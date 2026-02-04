package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.BaseEntity;
import hazardhub.com.hub.model.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "hazard_verifications")
public class HazardVerification extends BaseEntity {

    @Id
    private String id;

    @Field("hazard_id")
    private String hazardId;

    @Field("user_id")
    private String userId;

    @Field("verification_type")
    private VerificationType verificationType;

    private String comment;

    @Field("image_url")
    private String imageUrl;
}
