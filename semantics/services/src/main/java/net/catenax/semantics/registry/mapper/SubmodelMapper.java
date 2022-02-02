package net.catenax.semantics.registry.mapper;

import net.catenax.semantics.aas.registry.model.*;
import net.catenax.semantics.registry.model.*;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface SubmodelMapper {
    @Mappings({
            @Mapping(target="idExternal", source="apiDto.identification"),
            @Mapping(target="descriptions", source="apiDto.description"),
            @Mapping(target="semanticId", source = "apiDto.semanticId")
    })
    Submodel fromApiDto(SubmodelDescriptor apiDto);

    @Mappings({
            @Mapping(target="interfaceName", source = "apiDto.interface"),
            @Mapping(target="endpointAddress", source = "apiDto.protocolInformation.endpointAddress"),
            @Mapping(target="endpointProtocol", source = "apiDto.protocolInformation.endpointProtocol"),
            @Mapping(target="endpointProtocolVersion", source = "apiDto.protocolInformation.endpointProtocolVersion"),
            @Mapping(target="subProtocol", source = "apiDto.protocolInformation.subprotocol"),
            @Mapping(target="subProtocolBody", source = "apiDto.protocolInformation.subprotocolBody"),
            @Mapping(target="subProtocolBodyEncoding", source = "apiDto.protocolInformation.subprotocolBodyEncoding"),
    })
    SubmodelEndpoint fromApiDto(Endpoint apiDto);

    @InheritInverseConfiguration
    List<SubmodelDescriptor> toApiDto(Set<Submodel> shell);

    @InheritInverseConfiguration
    SubmodelDescriptor toApiDto(Submodel shell);

    @InheritInverseConfiguration
    Endpoint toApiDto(SubmodelEndpoint apiDto);

    default String map(Reference reference){
        return reference != null && reference.getValue() != null && !reference.getValue().isEmpty() ? reference.getValue().get(0) : null;
    }

    default Reference map(String semanticId){
        if(semanticId == null ||  semanticId.isBlank()) {
            return null;
        }
        Reference reference = new Reference();
        reference.setValue(List.of(semanticId));
        return reference;
    }

}