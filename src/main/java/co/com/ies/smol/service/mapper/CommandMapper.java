package co.com.ies.smol.service.mapper;

import co.com.ies.smol.domain.Command;
import co.com.ies.smol.service.dto.CommandDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Command} and its DTO {@link CommandDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommandMapper extends EntityMapper<CommandDTO, Command> {}
