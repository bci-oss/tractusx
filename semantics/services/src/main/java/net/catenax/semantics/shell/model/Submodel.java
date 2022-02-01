package net.catenax.semantics.shell.model;


import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;
import java.util.UUID;

@Value
@With
public class Submodel {
    @Id
    UUID id;

    String idExternal;
    String idShort;
    String semanticId;

    @MappedCollection(idColumn = "FK_SUBMODEL_ID")
    Set<SubmodelDescription> descriptions;

    @MappedCollection(idColumn = "FK_SUBMODEL_ID")
    Set<SubmodelEndpoint> endpoints;

    @Column( "FK_SHELL_ID")
    UUID shellId;
}
