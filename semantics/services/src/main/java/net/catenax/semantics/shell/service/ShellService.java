package net.catenax.semantics.shell.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.ShellIdentifier;
import net.catenax.semantics.shell.model.Submodel;
import net.catenax.semantics.shell.model.projection.IdOnly;
import net.catenax.semantics.shell.repository.ShellIdentifierRepository;
import net.catenax.semantics.shell.repository.ShellRepository;
import net.catenax.semantics.shell.repository.SubmodelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ShellService {

    private final ShellRepository shellRepository;
    private final ShellIdentifierRepository shellIdentifierRepository;
    private final SubmodelRepository submodelRepository;

    public ShellService(ShellRepository shellRepository, ShellIdentifierRepository shellIdentifierRepository,
                        SubmodelRepository submodelRepository) {
        this.shellRepository = shellRepository;
        this.shellIdentifierRepository = shellIdentifierRepository;
        this.submodelRepository = submodelRepository;
    }

    public Shell save(Shell shell) {
        return shellRepository.save(shell);
    }

    public Shell findShellByExternalId(String externalShellId){
        return shellRepository.findByIdExternal(externalShellId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Shell for identifier %s not found", externalShellId)));
    }

    public List<Shell> findAllShells(){
        return ImmutableList.copyOf(shellRepository.findAll());
    }

    public Shell update(String externalShellId, Shell shell){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        return shellRepository.save(shell.withId(shellId.getId()));
    }

    public void deleteShell(String externalShellId) {
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        shellRepository.deleteById(shellId.getId());
    }

    public Set<ShellIdentifier> findShellIdentifiersByExternalShellId(String externalShellId){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        return shellIdentifierRepository.findByShellId(shellId.getId());
    }

    public void deleteAllIdentifiers(String externalShellId){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        shellIdentifierRepository.deleteShellIdentifiersByShellId(shellId.getId());
    }

    public Set<ShellIdentifier> save(String externalShellId, Set<ShellIdentifier> shellIdentifiers){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        shellIdentifierRepository.deleteShellIdentifiersByShellId(shellId.getId());

        List<ShellIdentifier> identifiersToUpdate = shellIdentifiers.stream().map(identifier -> identifier.withShellId(shellId.getId()))
                .collect(Collectors.toList());
        return ImmutableSet.copyOf(shellIdentifierRepository.saveAll(identifiersToUpdate));
    }

    public Submodel save(String externalShellId, Submodel submodel){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        return submodelRepository.save(submodel.withFkShellId(shellId.getId()));
    }

    public Submodel update(String externalShellId, String externalSubmodelId, Submodel submodel){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        Submodel submodelFromDb = findSubmodelByExternalId(externalSubmodelId, shellId.getId());
        return submodelRepository.save(submodel
                .withId(submodelFromDb.getId())
                .withFkShellId(shellId.getId())
        );
    }

    public void deleteSubmodel(String externalShellId, String externalSubModelId) {
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        Submodel submodel = findSubmodelByExternalId(externalSubModelId, shellId.getId());
        submodelRepository.deleteById(submodel.getId());
    }

    public Submodel findSubmodelByExternalId(String externalSubModelId, UUID shellId){
        return submodelRepository
                .findByIdExternalAndFkShellId(externalSubModelId, shellId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Submodel for identifier %s not found.", externalSubModelId)));
    }

    public IdOnly findShellIdByExternalId(String externalShellId){
        return shellRepository.findIdOnlyByIdExternal(externalShellId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Shell for identifier %s not found", externalShellId)));
    }


}
