package net.catenax.semantics.shell.controller;

import net.catenax.semantics.aas.registry.api.RegistryApiDelegate;
import net.catenax.semantics.aas.registry.model.AssetAdministrationShellDescriptor;
import net.catenax.semantics.aas.registry.model.SubmodelDescriptor;
import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.ShellIdentifier;
import net.catenax.semantics.shell.service.ShellService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShellApiDelegate implements RegistryApiDelegate {

    private final ShellService shellService;

    public ShellApiDelegate(final ShellService shellService){
        this.shellService = shellService;
    }

    @Override
    public ResponseEntity<Void> deleteAssetAdministrationShellDescriptorById(String aasIdentifier) {
        return RegistryApiDelegate.super.deleteAssetAdministrationShellDescriptorById(aasIdentifier);
    }

    @Override
    public ResponseEntity<Void> deleteSubmodelDescriptorById(String aasIdentifier, String submodelIdentifier) {
        return RegistryApiDelegate.super.deleteSubmodelDescriptorById(aasIdentifier, submodelIdentifier);
    }

    @Override
    public ResponseEntity<List<AssetAdministrationShellDescriptor>> getAllAssetAdministrationShellDescriptors() {
        return RegistryApiDelegate.super.getAllAssetAdministrationShellDescriptors();
    }

    @Override
    public ResponseEntity<List<SubmodelDescriptor>> getAllSubmodelDescriptors(String aasIdentifier) {
        return RegistryApiDelegate.super.getAllSubmodelDescriptors(aasIdentifier);
    }

    @Override
    public ResponseEntity<AssetAdministrationShellDescriptor> getAssetAdministrationShellDescriptorById(String aasIdentifier) {
        return RegistryApiDelegate.super.getAssetAdministrationShellDescriptorById(aasIdentifier);
    }

    @Override
    public ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorById(String aasIdentifier, String submodelIdentifier) {
        return RegistryApiDelegate.super.getSubmodelDescriptorById(aasIdentifier, submodelIdentifier);
    }

    @Override
    public ResponseEntity<AssetAdministrationShellDescriptor> postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor shell) {

        Set<ShellIdentifier> shellIdentifiers = null;
        if ( shell.getSpecificAssetIds() != null && !shell.getSpecificAssetIds().isEmpty()){
            shellIdentifiers = shell.getSpecificAssetIds().stream().map( entry -> ShellIdentifier.of(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toSet());
        }

        Shell shellEntity = Shell.of(shell.getIdentification(), shell.getIdShort(), shellIdentifiers);

        Shell saved = shellService.save(shellEntity);
        AssetAdministrationShellDescriptor assetAdministrationShellDescriptor = new AssetAdministrationShellDescriptor();
        assetAdministrationShellDescriptor.setIdentification(saved.getIdExternal());
        assetAdministrationShellDescriptor.setIdShort(saved.getIdShort());
        return new ResponseEntity<>(assetAdministrationShellDescriptor, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SubmodelDescriptor> postSubmodelDescriptor(String aasIdentifier, SubmodelDescriptor submodelDescriptor) {
        return RegistryApiDelegate.super.postSubmodelDescriptor(aasIdentifier, submodelDescriptor);
    }

    @Override
    public ResponseEntity<Void> putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        return RegistryApiDelegate.super.putAssetAdministrationShellDescriptorById(aasIdentifier, assetAdministrationShellDescriptor);
    }

    @Override
    public ResponseEntity<Void> putSubmodelDescriptorById(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor submodelDescriptor) {
        return RegistryApiDelegate.super.putSubmodelDescriptorById(aasIdentifier, submodelIdentifier, submodelDescriptor);
    }
}
