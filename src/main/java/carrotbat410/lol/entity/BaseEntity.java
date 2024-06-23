package carrotbat410.lol.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    //TODO @CreationTimestamp vs @PrePersist
//    @CreationTimestamp
    private LocalDateTime created_at;

//    @UpdateTimestamp
    private LocalDateTime updated_at;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        created_at = now;
        updated_at = now;
    }

    @PreUpdate
    public void updateTimestamp() {
        updated_at = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }
}
