package net.catenax.semantics.shell.repository;

import net.catenax.semantics.shell.model.Submodel;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmodelRepository extends CrudRepository<Submodel, UUID> {

    Optional<Submodel> findByIdExternalAndFkShellId(String externalId, UUID shellId);

}
