package co.com.ies.smol.repository;

import co.com.ies.smol.domain.CommandModelPK;
import co.com.ies.smol.domain.CommandToModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommandModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandModelRepositoryPK extends JpaRepository<CommandToModel, CommandModelPK>, JpaSpecificationExecutor<CommandToModel> {
    @Query(nativeQuery = true, value = "SELECT * FROM command_to_model WHERE model_id = ?1")
    Optional<CommandToModel> findByModelId(Long modelId);
}
