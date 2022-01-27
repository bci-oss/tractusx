package net.catenax.semantics.shell.model;


import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
public class Submodel {
    @Id
    UUID id;

    String idExternal;
    String idShort;
    String semanticId;

    public static Submodel of(String idExternal, String idShort, String semanticId){
        return new Submodel(null, idExternal, idShort, semanticId);
    }
}
