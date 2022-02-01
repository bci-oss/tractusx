package net.catenax.semantics.shell.service;

import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.ShellIdentifier;
import net.catenax.semantics.shell.model.Submodel;
import net.catenax.semantics.shell.repository.ShellRepository;
import net.catenax.semantics.shell.repository.SubmodelRepository;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ShellService {

    private final ShellRepository shellRepository;
    private final SubmodelRepository submodelRepository;
    public ShellService(ShellRepository shellRepository, SubmodelRepository submodelRepository) {
        this.shellRepository = shellRepository;
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
        return StreamSupport.stream(shellRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public Shell update(String externalShellId, Shell shell){
        Shell shellFromDb = findShellByExternalId(externalShellId);
        return shellRepository.save(shell.withId(shellFromDb.getId()));
    }

    public void deleteShell(String externalShellId) {
        Shell shell = findShellByExternalId(externalShellId);
        shellRepository.deleteById(shell.getId());
    }

    public void deleteAllIdentifiers(String externalShellId){
        Shell shell = findShellByExternalId(externalShellId);
        shellRepository.save(shell.withIdentifiers(Set.of()));
    }

    public Set<ShellIdentifier> save(String externalShellId, Set<ShellIdentifier> shellIdentifiers){
        Shell shell = findShellByExternalId(externalShellId);
        return shellRepository.save(shell.withIdentifiers(shellIdentifiers)).getIdentifiers();
    }

    public Submodel save(String externalShellId, Submodel submodel){
        Shell shell = findShellByExternalId(externalShellId);
        return submodelRepository.save(submodel.withFkShellId(shell.getId()));
    }

    public Submodel update(String externalShellId, String externalSubmodelId, Submodel submodel){
        Shell shell = findShellByExternalId(externalShellId);
        Submodel submodelFromDb = findSubmodelByExternalId(externalSubmodelId, shell.getId());
        return submodelRepository.save(submodel
                .withId(submodelFromDb.getId())
                .withFkShellId(shell.getId())
        );
    }

    public void deleteSubmodel(String externalShellId, String externalSubModelId) {
        Shell shell = findShellByExternalId(externalShellId);
        Submodel submodel = findSubmodelByExternalId(externalSubModelId, shell.getId());
        submodelRepository.deleteById(submodel.getId());
    }

    public Submodel findSubmodelByExternalId(String externalSubModelId, UUID shellId){
        return submodelRepository
                .findByIdExternalAndFkShellId(externalSubModelId, shellId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Submodel for identifier %s not found.", externalSubModelId)));
    }


}
