package net.catenax.semantics.shell.repository;

import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.projection.IdOnly;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ShellRepository extends PagingAndSortingRepository<Shell, UUID> {
    Optional<Shell> findByIdExternal(String idExternal);

    @Query("select s.id from shell s where s.id_external = :idExternal")
    Optional<IdOnly> findIdOnlyByIdExternal(String idExternal);

    @Query("select distinct s.id_external from shell s where s.id in (select distinct si.fk_shell_id from shell_identifier si where CONCAT(si.key, ':', si.value) in (:keyValueCombinations))")
    List<String> findExternalShellIdsByIdentifiers(@Param("keyValueCombinations") Set<String> keyValueCombinations);
}
