/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.catenax.semantics.registry.mapper;

import net.catenax.semantics.aas.registry.model.AssetAdministrationShellDescriptor;
import net.catenax.semantics.aas.registry.model.AssetAdministrationShellDescriptorCollection;
import net.catenax.semantics.aas.registry.model.IdentifierKeyValuePair;
import net.catenax.semantics.registry.dto.ShellCollectionDto;
import net.catenax.semantics.registry.model.Shell;
import net.catenax.semantics.registry.model.ShellIdentifier;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(uses = {SubmodelMapper.class}, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShellMapper {
    @Mappings({
            @Mapping(target = "idExternal", source = "apiDto.identification"),
            @Mapping(target = "identifiers", source = "apiDto.specificAssetIds"),
            @Mapping(target = "descriptions", source = "apiDto.description"),
            @Mapping(target = "submodels", source = "apiDto.submodelDescriptors")
    })
    Shell fromApiDto(AssetAdministrationShellDescriptor apiDto);

    ShellIdentifier fromApiDto(IdentifierKeyValuePair apiDto);

    Set<ShellIdentifier> fromApiDto(List<IdentifierKeyValuePair> apiDto);


    AssetAdministrationShellDescriptorCollection toApiDto(ShellCollectionDto shell);

    @InheritInverseConfiguration
    AssetAdministrationShellDescriptor toApiDto(Shell shell);

    List<AssetAdministrationShellDescriptor> toApiDto(List<Shell> shell);
    @InheritInverseConfiguration
    List<IdentifierKeyValuePair> toApiDto(Set<ShellIdentifier> shell);
}