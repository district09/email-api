package gent.d09.servicefactory.email.api.module.email.domain.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@Data
public abstract class Event {
    private Long id;
    private String correlationId;
}
