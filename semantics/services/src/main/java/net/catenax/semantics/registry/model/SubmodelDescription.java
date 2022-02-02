package net.catenax.semantics.registry.model;

import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
public class SubmodelDescription {
    @Id
    UUID id;
    String language;
    String text;
}
