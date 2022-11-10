package co.com.ies.smol.web.rest;

import co.com.ies.smol.repository.CommandModelRepository;
import co.com.ies.smol.service.CommandModelQueryService;
import co.com.ies.smol.service.CommandModelService;
import co.com.ies.smol.service.criteria.CommandModelCriteria;
import co.com.ies.smol.service.dto.CommandModelDTO;
import co.com.ies.smol.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.com.ies.smol.domain.CommandModel}.
 */
@RestController
@RequestMapping("/api")
public class CommandModelResource {

    private final Logger log = LoggerFactory.getLogger(CommandModelResource.class);

    private static final String ENTITY_NAME = "commandModel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommandModelService commandModelService;

    private final CommandModelRepository commandModelRepository;

    private final CommandModelQueryService commandModelQueryService;

    public CommandModelResource(
        CommandModelService commandModelService,
        CommandModelRepository commandModelRepository,
        CommandModelQueryService commandModelQueryService
    ) {
        this.commandModelService = commandModelService;
        this.commandModelRepository = commandModelRepository;
        this.commandModelQueryService = commandModelQueryService;
    }

    /**
     * {@code POST  /command-models} : Create a new commandModel.
     *
     * @param commandModelDTO the commandModelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commandModelDTO, or with status {@code 400 (Bad Request)} if the commandModel has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/command-models")
    public ResponseEntity<CommandModelDTO> createCommandModel(@RequestBody CommandModelDTO commandModelDTO) throws URISyntaxException {
        log.debug("REST request to save CommandModel : {}", commandModelDTO);
        if (commandModelDTO.getId() != null) {
            throw new BadRequestAlertException("A new commandModel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CommandModelDTO result = commandModelService.save(commandModelDTO);
        return ResponseEntity
            .created(new URI("/api/command-models/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /command-models/:id} : Updates an existing commandModel.
     *
     * @param id the id of the commandModelDTO to save.
     * @param commandModelDTO the commandModelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandModelDTO,
     * or with status {@code 400 (Bad Request)} if the commandModelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commandModelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/command-models/{id}")
    public ResponseEntity<CommandModelDTO> updateCommandModel(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CommandModelDTO commandModelDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CommandModel : {}, {}", id, commandModelDTO);
        if (commandModelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandModelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandModelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CommandModelDTO result = commandModelService.update(commandModelDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandModelDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /command-models/:id} : Partial updates given fields of an existing commandModel, field will ignore if it is null
     *
     * @param id the id of the commandModelDTO to save.
     * @param commandModelDTO the commandModelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandModelDTO,
     * or with status {@code 400 (Bad Request)} if the commandModelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the commandModelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the commandModelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/command-models/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommandModelDTO> partialUpdateCommandModel(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CommandModelDTO commandModelDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CommandModel partially : {}, {}", id, commandModelDTO);
        if (commandModelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandModelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandModelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommandModelDTO> result = commandModelService.partialUpdate(commandModelDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandModelDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /command-models} : get all the commandModels.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandModels in body.
     */
    @GetMapping("/command-models")
    public ResponseEntity<List<CommandModelDTO>> getAllCommandModels(
        CommandModelCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get CommandModels by criteria: {}", criteria);
        Page<CommandModelDTO> page = commandModelQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /command-models/count} : count all the commandModels.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/command-models/count")
    public ResponseEntity<Long> countCommandModels(CommandModelCriteria criteria) {
        log.debug("REST request to count CommandModels by criteria: {}", criteria);
        return ResponseEntity.ok().body(commandModelQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /command-models/:id} : get the "id" commandModel.
     *
     * @param id the id of the commandModelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandModelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/command-models/{id}")
    public ResponseEntity<CommandModelDTO> getCommandModel(@PathVariable Long id) {
        log.debug("REST request to get CommandModel : {}", id);
        Optional<CommandModelDTO> commandModelDTO = commandModelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commandModelDTO);
    }

    /**
     * {@code DELETE  /command-models/:id} : delete the "id" commandModel.
     *
     * @param id the id of the commandModelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/command-models/{id}")
    public ResponseEntity<Void> deleteCommandModel(@PathVariable Long id) {
        log.debug("REST request to delete CommandModel : {}", id);
        commandModelService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
