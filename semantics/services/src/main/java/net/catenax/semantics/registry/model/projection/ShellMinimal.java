package net.catenax.semantics.registry.model.projection;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class ShellMinimal {
    UUID id;
    Instant createdDate;
}
