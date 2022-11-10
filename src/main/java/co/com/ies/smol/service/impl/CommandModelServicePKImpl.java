package co.com.ies.smol.service.impl;

import co.com.ies.smol.domain.CommandModel;
import co.com.ies.smol.domain.CommandModelPK;
import co.com.ies.smol.domain.CommandToModel;
import co.com.ies.smol.repository.CommandModelRepositoryPK;
import co.com.ies.smol.service.CommandModelServicePK;
import co.com.ies.smol.service.dto.CommandModelDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CommandModel}.
 */
@Service
@Transactional
public class CommandModelServicePKImpl implements CommandModelServicePK {

    private final CommandModelRepositoryPK commandModelRepository;

    public CommandModelServicePKImpl(CommandModelRepositoryPK commandModelRepository) {
        this.commandModelRepository = commandModelRepository;
    }

    @Override
    public CommandModelDTO save() {
        CommandToModel commandModel = new CommandToModel();
        CommandModelPK commandModelPK = new CommandModelPK(2L, 2L);
        commandModel.setId(commandModelPK);
        commandModelRepository.save(commandModel);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandModelDTO> findAll() {
        System.out.println("--------findAll " + commandModelRepository.findAll());
        return null;
    }

    @Override
    public CommandModelDTO findByModelId(Long modelId) {
        System.out.println("--------findByModelId " + commandModelRepository.findByModelId(1L));
        return null;
    }
}
