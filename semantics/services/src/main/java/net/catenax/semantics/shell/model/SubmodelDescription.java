package net.catenax.semantics.shell.model;

import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
public class SubmodelDescription {
    @Id
    UUID id;
    String language;
    String value;

    public static SubmodelDescription of(String language, String value){
        return new SubmodelDescription(null, language, value);
    }
}
