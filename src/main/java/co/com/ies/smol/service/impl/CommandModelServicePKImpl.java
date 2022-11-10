package co.com.ies.smol.service.impl;

import co.com.ies.smol.domain.CommandModel;
import co.com.ies.smol.repository.CommandModelRepository;
import co.com.ies.smol.repository.CommandModelRepositoryPK;
import co.com.ies.smol.service.CommandModelService;
import co.com.ies.smol.service.CommandModelServicePK;
import co.com.ies.smol.service.dto.CommandModelDTO;
import co.com.ies.smol.service.mapper.CommandModelMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public CommandModelDTO save(CommandModelDTO commandModelDTO) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandModelDTO> findAll() {
        System.out.println("--------findAll " + commandModelRepository.findAll());

        return null;
    }
}
