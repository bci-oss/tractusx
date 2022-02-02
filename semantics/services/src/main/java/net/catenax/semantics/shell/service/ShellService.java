package net.catenax.semantics.shell.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.catenax.semantics.shell.dto.ShellCollectionDto;
import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.ShellIdentifier;
import net.catenax.semantics.shell.model.Submodel;
import net.catenax.semantics.shell.model.projection.IdOnly;
import net.catenax.semantics.shell.repository.ShellIdentifierRepository;
import net.catenax.semantics.shell.repository.ShellRepository;
import net.catenax.semantics.shell.repository.SubmodelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional
    public Shell save(Shell shell) {
        return shellRepository.save(shell);
    }

    @Transactional(readOnly = true)
    public Shell findShellByExternalId(String externalShellId){
        return shellRepository.findByIdExternal(externalShellId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Shell for identifier %s not found", externalShellId)));
    }

    @Transactional(readOnly = true)
    public ShellCollectionDto findAllShells(int page, int pageSize){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.ASC, "createdDate");
        Page<Shell> shellsPage = shellRepository.findAll(pageable);
        return ShellCollectionDto.builder()
                .currentPage(pageable.getPageNumber())
                .totalItems((int)shellsPage.getTotalElements())
                .totalPages(shellsPage.getTotalPages())
                .itemCount(shellsPage.getNumberOfElements())
                .items(shellsPage.getContent())
                .build();
    }

    @Transactional
    public Shell update(String externalShellId, Shell shell){
        Shell shellFromDb = findShellByExternalId(externalShellId);
        return shellRepository.save(
                shell.withId(shellFromDb.getId()).withCreatedDate(shellFromDb.getCreatedDate())
        );
    }

    @Transactional
    public void deleteShell(String externalShellId) {
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        shellRepository.deleteById(shellId.getId());
    }

    @Transactional(readOnly = true)
    public Set<ShellIdentifier> findShellIdentifiersByExternalShellId(String externalShellId){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        return shellIdentifierRepository.findByShellId(shellId.getId());
    }

    @Transactional
    public void deleteAllIdentifiers(String externalShellId){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        shellIdentifierRepository.deleteShellIdentifiersByShellId(shellId.getId());
    }

    @Transactional
    public Set<ShellIdentifier> save(String externalShellId, Set<ShellIdentifier> shellIdentifiers){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        shellIdentifierRepository.deleteShellIdentifiersByShellId(shellId.getId());

        List<ShellIdentifier> identifiersToUpdate = shellIdentifiers.stream().map(identifier -> identifier.withShellId(shellId.getId()))
                .collect(Collectors.toList());
        return ImmutableSet.copyOf(shellIdentifierRepository.saveAll(identifiersToUpdate));
    }

    @Transactional
    public Submodel save(String externalShellId, Submodel submodel){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        return submodelRepository.save(submodel.withShellId(shellId.getId()));
    }

    @Transactional
    public Submodel update(String externalShellId, String externalSubmodelId, Submodel submodel){
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        IdOnly subModelId = findSubmodelId(shellId.getId(), externalSubmodelId);
        return submodelRepository.save(submodel
                .withId(subModelId.getId())
                .withShellId(shellId.getId())
        );
    }

    @Transactional
    public void deleteSubmodel(String externalShellId, String externalSubModelId) {
        IdOnly shellId = findShellIdByExternalId(externalShellId);
        IdOnly submodelId = findSubmodelId(shellId.getId(), externalSubModelId);
        submodelRepository.deleteById(submodelId.getId());
    }

    @Transactional(readOnly = true)
    public Submodel findSubmodelByExternalId(String externalShellId, String externalSubModelId){
        IdOnly shellIdByExternalId = findShellIdByExternalId(externalShellId);
        return submodelRepository
                .findByShellIdAndIdExternal(shellIdByExternalId.getId(), externalSubModelId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Submodel for identifier %s not found.", externalSubModelId)));
    }

    @Transactional(readOnly = true)
    public IdOnly findSubmodelId(UUID shellId, String externalSubModelId ){
        return submodelRepository
                .findIdOnlyByShellIdAndIdExternal(shellId, externalSubModelId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Submodel for identifier %s not found.", externalSubModelId)));
    }

    @Transactional(readOnly = true)
    public IdOnly findShellIdByExternalId(String externalShellId){
        return shellRepository.findIdOnlyByIdExternal(externalShellId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Shell for identifier %s not found", externalShellId)));
    }
}
