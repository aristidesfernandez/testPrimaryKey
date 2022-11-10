package co.com.ies.smol.service.mapper;

import co.com.ies.smol.domain.Command;
import co.com.ies.smol.domain.CommandModel;
import co.com.ies.smol.domain.Model;
import co.com.ies.smol.service.dto.CommandDTO;
import co.com.ies.smol.service.dto.CommandModelDTO;
import co.com.ies.smol.service.dto.ModelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CommandModel} and its DTO {@link CommandModelDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommandModelMapper extends EntityMapper<CommandModelDTO, CommandModel> {
    @Mapping(target = "command", source = "command", qualifiedByName = "commandId")
    @Mapping(target = "model", source = "model", qualifiedByName = "modelId")
    CommandModelDTO toDto(CommandModel s);

    @Named("commandId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommandDTO toDtoCommandId(Command command);

    @Named("modelId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ModelDTO toDtoModelId(Model model);
}
