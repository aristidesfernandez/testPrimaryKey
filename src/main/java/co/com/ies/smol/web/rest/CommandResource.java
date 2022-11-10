package co.com.ies.smol.web.rest;

import co.com.ies.smol.repository.CommandRepository;
import co.com.ies.smol.service.CommandQueryService;
import co.com.ies.smol.service.CommandService;
import co.com.ies.smol.service.criteria.CommandCriteria;
import co.com.ies.smol.service.dto.CommandDTO;
import co.com.ies.smol.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link co.com.ies.smol.domain.Command}.
 */
@RestController
@RequestMapping("/api")
public class CommandResource {

    private final Logger log = LoggerFactory.getLogger(CommandResource.class);

    private static final String ENTITY_NAME = "command";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommandService commandService;

    private final CommandRepository commandRepository;

    private final CommandQueryService commandQueryService;

    public CommandResource(CommandService commandService, CommandRepository commandRepository, CommandQueryService commandQueryService) {
        this.commandService = commandService;
        this.commandRepository = commandRepository;
        this.commandQueryService = commandQueryService;
    }

    /**
     * {@code POST  /commands} : Create a new command.
     *
     * @param commandDTO the commandDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commandDTO, or with status {@code 400 (Bad Request)} if the command has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/commands")
    public ResponseEntity<CommandDTO> createCommand(@Valid @RequestBody CommandDTO commandDTO) throws URISyntaxException {
        log.debug("REST request to save Command : {}", commandDTO);
        if (commandDTO.getId() != null) {
            throw new BadRequestAlertException("A new command cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CommandDTO result = commandService.save(commandDTO);
        return ResponseEntity
            .created(new URI("/api/commands/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /commands/:id} : Updates an existing command.
     *
     * @param id the id of the commandDTO to save.
     * @param commandDTO the commandDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandDTO,
     * or with status {@code 400 (Bad Request)} if the commandDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commandDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/commands/{id}")
    public ResponseEntity<CommandDTO> updateCommand(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CommandDTO commandDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Command : {}, {}", id, commandDTO);
        if (commandDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CommandDTO result = commandService.update(commandDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /commands/:id} : Partial updates given fields of an existing command, field will ignore if it is null
     *
     * @param id the id of the commandDTO to save.
     * @param commandDTO the commandDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandDTO,
     * or with status {@code 400 (Bad Request)} if the commandDTO is not valid,
     * or with status {@code 404 (Not Found)} if the commandDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the commandDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/commands/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommandDTO> partialUpdateCommand(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CommandDTO commandDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Command partially : {}, {}", id, commandDTO);
        if (commandDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommandDTO> result = commandService.partialUpdate(commandDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /commands} : get all the commands.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commands in body.
     */
    @GetMapping("/commands")
    public ResponseEntity<List<CommandDTO>> getAllCommands(
        CommandCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Commands by criteria: {}", criteria);
        Page<CommandDTO> page = commandQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /commands/count} : count all the commands.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/commands/count")
    public ResponseEntity<Long> countCommands(CommandCriteria criteria) {
        log.debug("REST request to count Commands by criteria: {}", criteria);
        return ResponseEntity.ok().body(commandQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /commands/:id} : get the "id" command.
     *
     * @param id the id of the commandDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/commands/{id}")
    public ResponseEntity<CommandDTO> getCommand(@PathVariable Long id) {
        log.debug("REST request to get Command : {}", id);
        Optional<CommandDTO> commandDTO = commandService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commandDTO);
    }

    /**
     * {@code DELETE  /commands/:id} : delete the "id" command.
     *
     * @param id the id of the commandDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/commands/{id}")
    public ResponseEntity<Void> deleteCommand(@PathVariable Long id) {
        log.debug("REST request to delete Command : {}", id);
        commandService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
