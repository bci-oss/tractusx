package net.catenax.semantics.shell.mapper;

import net.catenax.semantics.aas.registry.model.AssetAdministrationShellDescriptor;
import net.catenax.semantics.aas.registry.model.IdentifierKeyValuePair;
import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.ShellIdentifier;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;

@Mapper(uses = {SubmodelMapper.class}, componentModel = "spring")
public interface ShellMapper {
    @Mappings({
            @Mapping(target = "idExternal", source = "apiDto.identification"),
            @Mapping(target = "identifiers", source = "apiDto.specificAssetIds"),
            @Mapping(target = "descriptions", source = "apiDto.description"),
            @Mapping(target = "submodels", source = "apiDto.submodelDescriptors")
    })
    Shell fromApi(AssetAdministrationShellDescriptor apiDto);

    ShellIdentifier fromApi(IdentifierKeyValuePair apiDto);

    Set<ShellIdentifier> fromApi(List<IdentifierKeyValuePair> apiDto);

    @InheritInverseConfiguration
    AssetAdministrationShellDescriptor toApiDto(Shell shell);

    List<AssetAdministrationShellDescriptor> toApiDto(List<Shell> shell);
    @InheritInverseConfiguration
    List<IdentifierKeyValuePair> toApiDto(Set<ShellIdentifier> shell);
}
