package net.catenax.semantics.shell.repository;

import net.catenax.semantics.shell.model.Shell;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShellRepository extends CrudRepository<Shell, UUID> {
    Optional<Shell> findByIdExternal(String idExternal);
}
