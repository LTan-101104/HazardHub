package hazardhub.com.hub.model.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact {
    @DocumentId
    private String id; // UUID string

    private String userId; // Firebase UID (FK)
    private String name;
    private String phone;
    private String email;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
}