package co.com.ies.smol.repository;

import co.com.ies.smol.domain.CommandModel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommandModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandModelRepository extends JpaRepository<CommandModel, Long>, JpaSpecificationExecutor<CommandModel> {}
