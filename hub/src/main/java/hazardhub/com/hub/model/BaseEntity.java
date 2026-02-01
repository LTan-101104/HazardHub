package hazardhub.com.hub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Call before saving a new document
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.createdBy = "Admin";
    }

    // Call before updating an existing document
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = "Admin";
    }
}