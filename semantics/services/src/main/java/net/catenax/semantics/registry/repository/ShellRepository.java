package net.catenax.semantics.registry.repository;

import net.catenax.semantics.registry.model.Shell;
import net.catenax.semantics.registry.model.projection.ShellMinimal;
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

    @Query("select s.id, s.created_date from shell s where s.id_external = :idExternal")
    Optional<ShellMinimal> findMinimalRepresentationByIdExternal(String idExternal);

    @Query("select distinct s.id_external from shell s where s.id in (select distinct si.fk_shell_id from shell_identifier si where CONCAT(si.key, ':', si.value) in (:keyValueCombinations))")
    List<String> findExternalShellIdsByIdentifiers(@Param("keyValueCombinations") Set<String> keyValueCombinations);
}
