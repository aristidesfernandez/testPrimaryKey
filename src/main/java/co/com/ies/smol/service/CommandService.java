package co.com.ies.smol.service;

import co.com.ies.smol.service.dto.CommandDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.com.ies.smol.domain.Command}.
 */
public interface CommandService {
    /**
     * Save a command.
     *
     * @param commandDTO the entity to save.
     * @return the persisted entity.
     */
    CommandDTO save(CommandDTO commandDTO);

    /**
     * Updates a command.
     *
     * @param commandDTO the entity to update.
     * @return the persisted entity.
     */
    CommandDTO update(CommandDTO commandDTO);

    /**
     * Partially updates a command.
     *
     * @param commandDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CommandDTO> partialUpdate(CommandDTO commandDTO);

    /**
     * Get all the commands.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CommandDTO> findAll(Pageable pageable);

    /**
     * Get the "id" command.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CommandDTO> findOne(Long id);

    /**
     * Delete the "id" command.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
