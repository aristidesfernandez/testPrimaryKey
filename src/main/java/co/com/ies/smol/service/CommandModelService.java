package co.com.ies.smol.service;

import co.com.ies.smol.service.dto.CommandModelDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.com.ies.smol.domain.CommandModel}.
 */
public interface CommandModelService {
    /**
     * Save a commandModel.
     *
     * @param commandModelDTO the entity to save.
     * @return the persisted entity.
     */
    CommandModelDTO save(CommandModelDTO commandModelDTO);

    /**
     * Updates a commandModel.
     *
     * @param commandModelDTO the entity to update.
     * @return the persisted entity.
     */
    CommandModelDTO update(CommandModelDTO commandModelDTO);

    /**
     * Partially updates a commandModel.
     *
     * @param commandModelDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CommandModelDTO> partialUpdate(CommandModelDTO commandModelDTO);

    /**
     * Get all the commandModels.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CommandModelDTO> findAll(Pageable pageable);

    /**
     * Get the "id" commandModel.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CommandModelDTO> findOne(Long id);

    /**
     * Delete the "id" commandModel.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
