package co.com.ies.smol.service;

import co.com.ies.smol.domain.*; // for static metamodels
import co.com.ies.smol.domain.Model;
import co.com.ies.smol.repository.ModelRepository;
import co.com.ies.smol.service.criteria.ModelCriteria;
import co.com.ies.smol.service.dto.ModelDTO;
import co.com.ies.smol.service.mapper.ModelMapper;
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
 * Service for executing complex queries for {@link Model} entities in the database.
 * The main input is a {@link ModelCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ModelDTO} or a {@link Page} of {@link ModelDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ModelQueryService extends QueryService<Model> {

    private final Logger log = LoggerFactory.getLogger(ModelQueryService.class);

    private final ModelRepository modelRepository;

    private final ModelMapper modelMapper;

    public ModelQueryService(ModelRepository modelRepository, ModelMapper modelMapper) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Return a {@link List} of {@link ModelDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ModelDTO> findByCriteria(ModelCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Model> specification = createSpecification(criteria);
        return modelMapper.toDto(modelRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ModelDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ModelDTO> findByCriteria(ModelCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Model> specification = createSpecification(criteria);
        return modelRepository.findAll(specification, page).map(modelMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ModelCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Model> specification = createSpecification(criteria);
        return modelRepository.count(specification);
    }

    /**
     * Function to convert {@link ModelCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Model> createSpecification(ModelCriteria criteria) {
        Specification<Model> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Model_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Model_.name));
            }
        }
        return specification;
    }
}
