package co.com.ies.smol.service;

import co.com.ies.smol.domain.*; // for static metamodels
import co.com.ies.smol.domain.Command;
import co.com.ies.smol.repository.CommandRepository;
import co.com.ies.smol.service.criteria.CommandCriteria;
import co.com.ies.smol.service.dto.CommandDTO;
import co.com.ies.smol.service.mapper.CommandMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Command} entities in the database.
 * The main input is a {@link CommandCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CommandDTO} or a {@link Page} of {@link CommandDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommandQueryService extends QueryService<Command> {

    private final Logger log = LoggerFactory.getLogger(CommandQueryService.class);

    private final CommandRepository commandRepository;

    private final CommandMapper commandMapper;

    public CommandQueryService(CommandRepository commandRepository, CommandMapper commandMapper) {
        this.commandRepository = commandRepository;
        this.commandMapper = commandMapper;
    }

    /**
     * Return a {@link List} of {@link CommandDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CommandDTO> findByCriteria(CommandCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Command> specification = createSpecification(criteria);
        return commandMapper.toDto(commandRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CommandDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommandDTO> findByCriteria(CommandCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Command> specification = createSpecification(criteria);
        return commandRepository.findAll(specification, page).map(commandMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommandCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Command> specification = createSpecification(criteria);
        return commandRepository.count(specification);
    }

    /**
     * Function to convert {@link CommandCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Command> createSpecification(CommandCriteria criteria) {
        Specification<Command> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Command_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Command_.code));
            }
        }
        return specification;
    }
}
