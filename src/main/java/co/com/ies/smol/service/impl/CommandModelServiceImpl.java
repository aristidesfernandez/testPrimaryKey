package co.com.ies.smol.service.impl;

import co.com.ies.smol.domain.CommandModel;
import co.com.ies.smol.repository.CommandModelRepository;
import co.com.ies.smol.service.CommandModelService;
import co.com.ies.smol.service.dto.CommandModelDTO;
import co.com.ies.smol.service.mapper.CommandModelMapper;
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
public class CommandModelServiceImpl implements CommandModelService {

    private final Logger log = LoggerFactory.getLogger(CommandModelServiceImpl.class);

    private final CommandModelRepository commandModelRepository;

    private final CommandModelMapper commandModelMapper;

    public CommandModelServiceImpl(CommandModelRepository commandModelRepository, CommandModelMapper commandModelMapper) {
        this.commandModelRepository = commandModelRepository;
        this.commandModelMapper = commandModelMapper;
    }

    @Override
    public CommandModelDTO save(CommandModelDTO commandModelDTO) {
        log.debug("Request to save CommandModel : {}", commandModelDTO);
        CommandModel commandModel = commandModelMapper.toEntity(commandModelDTO);
        commandModel = commandModelRepository.save(commandModel);
        return commandModelMapper.toDto(commandModel);
    }

    @Override
    public CommandModelDTO update(CommandModelDTO commandModelDTO) {
        log.debug("Request to update CommandModel : {}", commandModelDTO);
        CommandModel commandModel = commandModelMapper.toEntity(commandModelDTO);
        commandModel = commandModelRepository.save(commandModel);
        return commandModelMapper.toDto(commandModel);
    }

    @Override
    public Optional<CommandModelDTO> partialUpdate(CommandModelDTO commandModelDTO) {
        log.debug("Request to partially update CommandModel : {}", commandModelDTO);

        return commandModelRepository
            .findById(commandModelDTO.getId())
            .map(existingCommandModel -> {
                commandModelMapper.partialUpdate(existingCommandModel, commandModelDTO);

                return existingCommandModel;
            })
            .map(commandModelRepository::save)
            .map(commandModelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandModelDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CommandModels");
        return commandModelRepository.findAll(pageable).map(commandModelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommandModelDTO> findOne(Long id) {
        log.debug("Request to get CommandModel : {}", id);
        return commandModelRepository.findById(id).map(commandModelMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CommandModel : {}", id);
        commandModelRepository.deleteById(id);
    }
}
