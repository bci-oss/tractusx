package net.catenax.semantics.registry.model;



import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@Value
@With
public class ShellIdentifier {
    @Id
    UUID id;
    String key;
    String value;

    @Column( "FK_SHELL_ID")
    UUID shellId;
}
