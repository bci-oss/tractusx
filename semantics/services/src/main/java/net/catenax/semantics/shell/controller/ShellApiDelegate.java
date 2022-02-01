package net.catenax.semantics.shell.controller;

import net.catenax.semantics.aas.registry.api.RegistryApiDelegate;
import net.catenax.semantics.aas.registry.model.AssetAdministrationShellDescriptor;
import net.catenax.semantics.aas.registry.model.SubmodelDescriptor;
import net.catenax.semantics.shell.mapper.ShellMapper;
import net.catenax.semantics.shell.mapper.SubmodelMapper;
import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.Submodel;
import net.catenax.semantics.shell.service.ShellService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShellApiDelegate implements RegistryApiDelegate {

    private final ShellService shellService;
    private final ShellMapper shellMapper;
    private final SubmodelMapper submodelMapper;

    public ShellApiDelegate(final ShellService shellService, final ShellMapper shellMapper, SubmodelMapper submodelMapper){
        this.shellService = shellService;
        this.shellMapper = shellMapper;
        this.submodelMapper = submodelMapper;
    }

    @Override
    public ResponseEntity<Void> deleteAssetAdministrationShellDescriptorById(String aasIdentifier) {
        shellService.deleteShell(aasIdentifier);
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> deleteSubmodelDescriptorById(String aasIdentifier, String submodelIdentifier) {
        shellService.deleteSubmodel(aasIdentifier,submodelIdentifier);
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<AssetAdministrationShellDescriptor>> getAllAssetAdministrationShellDescriptors() {
        return new ResponseEntity<>(shellMapper.toApiDto(shellService.findAllShells()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SubmodelDescriptor>> getAllSubmodelDescriptors(String aasIdentifier) {
        Shell saved = shellService.findShellByExternalId(aasIdentifier);
        return new ResponseEntity<>(submodelMapper.toApiDto(saved.getSubmodels()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AssetAdministrationShellDescriptor> getAssetAdministrationShellDescriptorById(String aasIdentifier) {
        Shell saved = shellService.findShellByExternalId(aasIdentifier);
        return new ResponseEntity<>(shellMapper.toApiDto(saved), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorById(String aasIdentifier, String submodelIdentifier) {
        Shell shell = shellService.findShellByExternalId(aasIdentifier);
        Submodel submodel =  shellService.findSubmodelByExternalId(submodelIdentifier, shell.getId());
        return new ResponseEntity<>(submodelMapper.toApiDto(submodel), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AssetAdministrationShellDescriptor> postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        Shell saved = shellService.save(shellMapper.fromApi(assetAdministrationShellDescriptor));
        return new ResponseEntity<>(shellMapper.toApiDto(saved), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SubmodelDescriptor> postSubmodelDescriptor(String aasIdentifier, SubmodelDescriptor submodelDescriptor) {
        Submodel savedSubModel = shellService.save(aasIdentifier, submodelMapper.fromApiDto(submodelDescriptor));
        return new ResponseEntity<>(submodelMapper.toApiDto(savedSubModel), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        shellService.update(aasIdentifier, shellMapper.fromApi(assetAdministrationShellDescriptor)
                // the external id in the payload is not allowed to be
                .withIdExternal(aasIdentifier));
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> putSubmodelDescriptorById(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor submodelDescriptor) {
        shellService.update(aasIdentifier, submodelIdentifier, submodelMapper.fromApiDto(submodelDescriptor)
                // the external id in the payload is not allowed to be
                .withIdExternal(submodelIdentifier));
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }
}
