package net.catenax.semantics.shell.model;


import lombok.*;
import lombok.experimental.WithBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
public class Shell {
    @Id
    UUID id;
    String idExternal;
    String idShort;

    @MappedCollection(idColumn = "FK_SHELL_ID")
    Set<ShellIdentifier> shellIdentifiers;

    public static Shell of(String idExternal, String idShort, @Nullable Set<ShellIdentifier> shellIdentifiers){
        return new Shell(null, idExternal, idShort , shellIdentifiers);
    }
}
