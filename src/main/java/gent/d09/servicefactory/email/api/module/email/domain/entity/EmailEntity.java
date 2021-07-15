package gent.d09.servicefactory.email.api.module.email.domain.entity;

import gent.d09.servicefactory.email.api.module.email.domain.dto.EmailCreationDto;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@RegisterForReflection
@NoArgsConstructor
@Data()
@EqualsAndHashCode(callSuper = true)
@Table(name = "email_entity")
public class EmailEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;
    @Column(name = "application_id")
    public String applicationId;
    public String status;
    @Column(name = "status_message")
    public String statusMessage;
    public Boolean async;
    @CreationTimestamp
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    @CreationTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    public EmailEntity(EmailCreationDto dto, String applicationId){
        this.applicationId = applicationId;
        this.status = "Queued";
        this.statusMessage = "Queued";
        this.async = dto.getAsync() == null || dto.getAsync();
    }
}