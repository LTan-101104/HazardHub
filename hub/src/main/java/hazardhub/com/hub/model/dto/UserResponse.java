package hazardhub.com.hub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String phone;
    private String displayName;
    private Map insuranceDispatchConfig;
    private Date createdAt;
    private Date updatedAt;
}
