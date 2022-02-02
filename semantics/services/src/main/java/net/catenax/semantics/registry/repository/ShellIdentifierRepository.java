package net.catenax.semantics.registry.repository;

import net.catenax.semantics.registry.model.ShellIdentifier;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface ShellIdentifierRepository extends CrudRepository<ShellIdentifier, UUID> {
    @Modifying
    @Query("delete from SHELL_IDENTIFIER si where si.FK_SHELL_ID = :shellId")
    void deleteShellIdentifiersByShellId(UUID shellId);

    Set<ShellIdentifier> findByShellId(UUID shellId);
}
