package co.com.ies.smol.repository;

import co.com.ies.smol.domain.Command;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Command entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandRepository extends JpaRepository<Command, Long>, JpaSpecificationExecutor<Command> {}
