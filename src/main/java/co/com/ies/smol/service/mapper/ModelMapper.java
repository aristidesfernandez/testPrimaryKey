package co.com.ies.smol.service.mapper;

import co.com.ies.smol.domain.Model;
import co.com.ies.smol.service.dto.ModelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Model} and its DTO {@link ModelDTO}.
 */
@Mapper(componentModel = "spring")
public interface ModelMapper extends EntityMapper<ModelDTO, Model> {}
