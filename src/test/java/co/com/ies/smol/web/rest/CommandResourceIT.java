package co.com.ies.smol.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.com.ies.smol.IntegrationTest;
import co.com.ies.smol.domain.Command;
import co.com.ies.smol.repository.CommandRepository;
import co.com.ies.smol.service.criteria.CommandCriteria;
import co.com.ies.smol.service.dto.CommandDTO;
import co.com.ies.smol.service.mapper.CommandMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CommandResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommandResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/commands";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private CommandMapper commandMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommandMockMvc;

    private Command command;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Command createEntity(EntityManager em) {
        Command command = new Command().code(DEFAULT_CODE);
        return command;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Command createUpdatedEntity(EntityManager em) {
        Command command = new Command().code(UPDATED_CODE);
        return command;
    }

    @BeforeEach
    public void initTest() {
        command = createEntity(em);
    }

    @Test
    @Transactional
    void createCommand() throws Exception {
        int databaseSizeBeforeCreate = commandRepository.findAll().size();
        // Create the Command
        CommandDTO commandDTO = commandMapper.toDto(command);
        restCommandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandDTO)))
            .andExpect(status().isCreated());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeCreate + 1);
        Command testCommand = commandList.get(commandList.size() - 1);
        assertThat(testCommand.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    void createCommandWithExistingId() throws Exception {
        // Create the Command with an existing ID
        command.setId(1L);
        CommandDTO commandDTO = commandMapper.toDto(command);

        int databaseSizeBeforeCreate = commandRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCommands() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        // Get all the commandList
        restCommandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(command.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getCommand() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        // Get the command
        restCommandMockMvc
            .perform(get(ENTITY_API_URL_ID, command.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(command.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getCommandsByIdFiltering() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        Long id = command.getId();

        defaultCommandShouldBeFound("id.equals=" + id);
        defaultCommandShouldNotBeFound("id.notEquals=" + id);

        defaultCommandShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommandShouldNotBeFound("id.greaterThan=" + id);

        defaultCommandShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommandShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommandsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        // Get all the commandList where code equals to DEFAULT_CODE
        defaultCommandShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the commandList where code equals to UPDATED_CODE
        defaultCommandShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCommandsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        // Get all the commandList where code in DEFAULT_CODE or UPDATED_CODE
        defaultCommandShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the commandList where code equals to UPDATED_CODE
        defaultCommandShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCommandsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        // Get all the commandList where code is not null
        defaultCommandShouldBeFound("code.specified=true");

        // Get all the commandList where code is null
        defaultCommandShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandsByCodeContainsSomething() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        // Get all the commandList where code contains DEFAULT_CODE
        defaultCommandShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the commandList where code contains UPDATED_CODE
        defaultCommandShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCommandsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        // Get all the commandList where code does not contain DEFAULT_CODE
        defaultCommandShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the commandList where code does not contain UPDATED_CODE
        defaultCommandShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommandShouldBeFound(String filter) throws Exception {
        restCommandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(command.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));

        // Check, that the count call also returns 1
        restCommandMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommandShouldNotBeFound(String filter) throws Exception {
        restCommandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommandMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCommand() throws Exception {
        // Get the command
        restCommandMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommand() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        int databaseSizeBeforeUpdate = commandRepository.findAll().size();

        // Update the command
        Command updatedCommand = commandRepository.findById(command.getId()).get();
        // Disconnect from session so that the updates on updatedCommand are not directly saved in db
        em.detach(updatedCommand);
        updatedCommand.code(UPDATED_CODE);
        CommandDTO commandDTO = commandMapper.toDto(updatedCommand);

        restCommandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandDTO))
            )
            .andExpect(status().isOk());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
        Command testCommand = commandList.get(commandList.size() - 1);
        assertThat(testCommand.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void putNonExistingCommand() throws Exception {
        int databaseSizeBeforeUpdate = commandRepository.findAll().size();
        command.setId(count.incrementAndGet());

        // Create the Command
        CommandDTO commandDTO = commandMapper.toDto(command);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommand() throws Exception {
        int databaseSizeBeforeUpdate = commandRepository.findAll().size();
        command.setId(count.incrementAndGet());

        // Create the Command
        CommandDTO commandDTO = commandMapper.toDto(command);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommand() throws Exception {
        int databaseSizeBeforeUpdate = commandRepository.findAll().size();
        command.setId(count.incrementAndGet());

        // Create the Command
        CommandDTO commandDTO = commandMapper.toDto(command);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommandWithPatch() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        int databaseSizeBeforeUpdate = commandRepository.findAll().size();

        // Update the command using partial update
        Command partialUpdatedCommand = new Command();
        partialUpdatedCommand.setId(command.getId());

        partialUpdatedCommand.code(UPDATED_CODE);

        restCommandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommand.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommand))
            )
            .andExpect(status().isOk());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
        Command testCommand = commandList.get(commandList.size() - 1);
        assertThat(testCommand.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void fullUpdateCommandWithPatch() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        int databaseSizeBeforeUpdate = commandRepository.findAll().size();

        // Update the command using partial update
        Command partialUpdatedCommand = new Command();
        partialUpdatedCommand.setId(command.getId());

        partialUpdatedCommand.code(UPDATED_CODE);

        restCommandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommand.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommand))
            )
            .andExpect(status().isOk());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
        Command testCommand = commandList.get(commandList.size() - 1);
        assertThat(testCommand.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingCommand() throws Exception {
        int databaseSizeBeforeUpdate = commandRepository.findAll().size();
        command.setId(count.incrementAndGet());

        // Create the Command
        CommandDTO commandDTO = commandMapper.toDto(command);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commandDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommand() throws Exception {
        int databaseSizeBeforeUpdate = commandRepository.findAll().size();
        command.setId(count.incrementAndGet());

        // Create the Command
        CommandDTO commandDTO = commandMapper.toDto(command);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommand() throws Exception {
        int databaseSizeBeforeUpdate = commandRepository.findAll().size();
        command.setId(count.incrementAndGet());

        // Create the Command
        CommandDTO commandDTO = commandMapper.toDto(command);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(commandDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Command in the database
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommand() throws Exception {
        // Initialize the database
        commandRepository.saveAndFlush(command);

        int databaseSizeBeforeDelete = commandRepository.findAll().size();

        // Delete the command
        restCommandMockMvc
            .perform(delete(ENTITY_API_URL_ID, command.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Command> commandList = commandRepository.findAll();
        assertThat(commandList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
