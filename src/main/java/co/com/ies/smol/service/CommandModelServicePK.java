package co.com.ies.smol.service;

import co.com.ies.smol.service.dto.CommandModelDTO;
import java.util.List;

/**
 * Service Interface for managing {@link co.com.ies.smol.domain.CommandModel}.
 */
public interface CommandModelServicePK {
    CommandModelDTO save();

    CommandModelDTO findByModelId(Long modelId);

    List<CommandModelDTO> findAll();
}
