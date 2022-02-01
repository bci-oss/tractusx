package net.catenax.semantics.shell.repository;

import net.catenax.semantics.shell.model.Shell;
import net.catenax.semantics.shell.model.projection.IdOnly;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShellRepository extends CrudRepository<Shell, UUID> {
    Optional<Shell> findByIdExternal(String idExternal);

    @Query("select s.id from shell s where s.id_external = :idExternal")
    Optional<IdOnly> findIdOnlyByIdExternal(String idExternal);

}
