package net.catenax.semantics.shell.model;

import lombok.Value;
import org.springframework.data.annotation.Id;

import javax.annotation.Nullable;
import java.util.UUID;

@Value
public class SubmodelEndpoint {
    @Id
    UUID id;
    String interfaceName;
    String endpointAddress;
    String endpointProtocolVersion;
    String subProtocol;
    String subProtocolBody;
    String subProtocolEncoding;

    public static SubmodelEndpoint of(String interfaceName, String endpointAddress,
                                      @Nullable String endpointProtocolVersion, @Nullable String subProtocol,
                                      @Nullable String subProtocolBody, @Nullable String subProtocolEncoding) {
        return new SubmodelEndpoint(null, interfaceName,
                endpointAddress, endpointProtocolVersion,
                subProtocol, subProtocolBody, subProtocolEncoding);
    }
}
