package net.catenax.semantics.shell.model;


import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;
import java.util.UUID;

@Value
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

}
