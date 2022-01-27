package net.catenax.semantics.shell.model;

import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
public class ShellDescription {
    @Id
    UUID id;
    String language;
    String value;

    public static ShellDescription of(String language, String value){
        return new ShellDescription(null, language, value);
    }
}
