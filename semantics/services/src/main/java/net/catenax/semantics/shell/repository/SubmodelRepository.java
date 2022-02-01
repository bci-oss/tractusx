package net.catenax.semantics.shell.repository;

import net.catenax.semantics.shell.model.Submodel;
import net.catenax.semantics.shell.model.projection.IdOnly;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmodelRepository extends CrudRepository<Submodel, UUID> {

    Optional<Submodel> findByShellIdAndIdExternal(UUID shellId, String externalId);

    @Query("select s.id from submodel s where s.fk_shell_id = :shellId and s.id_external = :externalId")
    Optional<IdOnly> findIdOnlyByShellIdAndIdExternal(UUID shellId, String externalId);
}
