package co.com.ies.smol.service;

import co.com.ies.smol.domain.*; // for static metamodels
import co.com.ies.smol.domain.CommandModel;
import co.com.ies.smol.repository.CommandModelRepository;
import co.com.ies.smol.service.criteria.CommandModelCriteria;
import co.com.ies.smol.service.dto.CommandModelDTO;
import co.com.ies.smol.service.mapper.CommandModelMapper;
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
 * Service for executing complex queries for {@link CommandModel} entities in the database.
 * The main input is a {@link CommandModelCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CommandModelDTO} or a {@link Page} of {@link CommandModelDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommandModelQueryService extends QueryService<CommandModel> {

    private final Logger log = LoggerFactory.getLogger(CommandModelQueryService.class);

    private final CommandModelRepository commandModelRepository;

    private final CommandModelMapper commandModelMapper;

    public CommandModelQueryService(CommandModelRepository commandModelRepository, CommandModelMapper commandModelMapper) {
        this.commandModelRepository = commandModelRepository;
        this.commandModelMapper = commandModelMapper;
    }

    /**
     * Return a {@link List} of {@link CommandModelDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CommandModelDTO> findByCriteria(CommandModelCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CommandModel> specification = createSpecification(criteria);
        return commandModelMapper.toDto(commandModelRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CommandModelDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommandModelDTO> findByCriteria(CommandModelCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CommandModel> specification = createSpecification(criteria);
        return commandModelRepository.findAll(specification, page).map(commandModelMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommandModelCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CommandModel> specification = createSpecification(criteria);
        return commandModelRepository.count(specification);
    }

    /**
     * Function to convert {@link CommandModelCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CommandModel> createSpecification(CommandModelCriteria criteria) {
        Specification<CommandModel> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CommandModel_.id));
            }
            if (criteria.getCommandId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCommandId(),
                            root -> root.join(CommandModel_.command, JoinType.LEFT).get(Command_.id)
                        )
                    );
            }
            if (criteria.getModelId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getModelId(), root -> root.join(CommandModel_.model, JoinType.LEFT).get(Model_.id))
                    );
            }
        }
        return specification;
    }
}
