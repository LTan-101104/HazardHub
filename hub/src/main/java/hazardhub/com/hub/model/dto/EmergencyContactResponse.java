package hazardhub.com.hub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactResponse {
    private String id;
    private String userId;
    private String name;
    private String phone;
    private String email;
    private Date createdAt;
    private Date updatedAt;
}