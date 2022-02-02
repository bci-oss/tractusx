package net.catenax.semantics.registry.model;

import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
public class SubmodelEndpoint {
    @Id
    UUID id;
    String interfaceName;
    String endpointAddress;
    String endpointProtocol;
    String endpointProtocolVersion;
    String subProtocol;
    String subProtocolBody;
    String subProtocolBodyEncoding;

}
