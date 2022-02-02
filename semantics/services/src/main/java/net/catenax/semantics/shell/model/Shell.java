package net.catenax.semantics.shell.model;


import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Value
@With
public class Shell {
    @Id
    UUID id;
    String idExternal;
    String idShort;

    @MappedCollection(idColumn = "FK_SHELL_ID")
    Set<ShellIdentifier> identifiers;

    @MappedCollection(idColumn = "FK_SHELL_ID")
    Set<ShellDescription> descriptions;

    @MappedCollection(idColumn = "FK_SHELL_ID")
    Set<Submodel> submodels;

    @CreatedDate
    Instant createdDate;

    @LastModifiedDate
    Instant lastModifiedDate;

}
