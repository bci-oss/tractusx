package net.catenax.semantics.shell.model;



import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
public class ShellIdentifier {
    @Id
    UUID id;
    String key;
    String value;

}
