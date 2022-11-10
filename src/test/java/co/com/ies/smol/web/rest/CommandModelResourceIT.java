package co.com.ies.smol.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.com.ies.smol.IntegrationTest;
import co.com.ies.smol.domain.Command;
import co.com.ies.smol.domain.CommandModel;
import co.com.ies.smol.domain.Model;
import co.com.ies.smol.repository.CommandModelRepository;
import co.com.ies.smol.service.criteria.CommandModelCriteria;
import co.com.ies.smol.service.dto.CommandModelDTO;
import co.com.ies.smol.service.mapper.CommandModelMapper;
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
 * Integration tests for the {@link CommandModelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommandModelResourceIT {

    private static final String ENTITY_API_URL = "/api/command-models";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommandModelRepository commandModelRepository;

    @Autowired
    private CommandModelMapper commandModelMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommandModelMockMvc;

    private CommandModel commandModel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommandModel createEntity(EntityManager em) {
        CommandModel commandModel = new CommandModel();
        return commandModel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommandModel createUpdatedEntity(EntityManager em) {
        CommandModel commandModel = new CommandModel();
        return commandModel;
    }

    @BeforeEach
    public void initTest() {
        commandModel = createEntity(em);
    }

    @Test
    @Transactional
    void createCommandModel() throws Exception {
        int databaseSizeBeforeCreate = commandModelRepository.findAll().size();
        // Create the CommandModel
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);
        restCommandModelMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeCreate + 1);
        CommandModel testCommandModel = commandModelList.get(commandModelList.size() - 1);
    }

    @Test
    @Transactional
    void createCommandModelWithExistingId() throws Exception {
        // Create the CommandModel with an existing ID
        commandModel.setId(1L);
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);

        int databaseSizeBeforeCreate = commandModelRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommandModelMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCommandModels() throws Exception {
        // Initialize the database
        commandModelRepository.saveAndFlush(commandModel);

        // Get all the commandModelList
        restCommandModelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commandModel.getId().intValue())));
    }

    @Test
    @Transactional
    void getCommandModel() throws Exception {
        // Initialize the database
        commandModelRepository.saveAndFlush(commandModel);

        // Get the commandModel
        restCommandModelMockMvc
            .perform(get(ENTITY_API_URL_ID, commandModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commandModel.getId().intValue()));
    }

    @Test
    @Transactional
    void getCommandModelsByIdFiltering() throws Exception {
        // Initialize the database
        commandModelRepository.saveAndFlush(commandModel);

        Long id = commandModel.getId();

        defaultCommandModelShouldBeFound("id.equals=" + id);
        defaultCommandModelShouldNotBeFound("id.notEquals=" + id);

        defaultCommandModelShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommandModelShouldNotBeFound("id.greaterThan=" + id);

        defaultCommandModelShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommandModelShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommandModelsByCommandIsEqualToSomething() throws Exception {
        Command command;
        if (TestUtil.findAll(em, Command.class).isEmpty()) {
            commandModelRepository.saveAndFlush(commandModel);
            command = CommandResourceIT.createEntity(em);
        } else {
            command = TestUtil.findAll(em, Command.class).get(0);
        }
        em.persist(command);
        em.flush();
        commandModel.setCommand(command);
        commandModelRepository.saveAndFlush(commandModel);
        Long commandId = command.getId();

        // Get all the commandModelList where command equals to commandId
        defaultCommandModelShouldBeFound("commandId.equals=" + commandId);

        // Get all the commandModelList where command equals to (commandId + 1)
        defaultCommandModelShouldNotBeFound("commandId.equals=" + (commandId + 1));
    }

    @Test
    @Transactional
    void getAllCommandModelsByModelIsEqualToSomething() throws Exception {
        Model model;
        if (TestUtil.findAll(em, Model.class).isEmpty()) {
            commandModelRepository.saveAndFlush(commandModel);
            model = ModelResourceIT.createEntity(em);
        } else {
            model = TestUtil.findAll(em, Model.class).get(0);
        }
        em.persist(model);
        em.flush();
        commandModel.setModel(model);
        commandModelRepository.saveAndFlush(commandModel);
        Long modelId = model.getId();

        // Get all the commandModelList where model equals to modelId
        defaultCommandModelShouldBeFound("modelId.equals=" + modelId);

        // Get all the commandModelList where model equals to (modelId + 1)
        defaultCommandModelShouldNotBeFound("modelId.equals=" + (modelId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommandModelShouldBeFound(String filter) throws Exception {
        restCommandModelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commandModel.getId().intValue())));

        // Check, that the count call also returns 1
        restCommandModelMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommandModelShouldNotBeFound(String filter) throws Exception {
        restCommandModelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommandModelMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCommandModel() throws Exception {
        // Get the commandModel
        restCommandModelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommandModel() throws Exception {
        // Initialize the database
        commandModelRepository.saveAndFlush(commandModel);

        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();

        // Update the commandModel
        CommandModel updatedCommandModel = commandModelRepository.findById(commandModel.getId()).get();
        // Disconnect from session so that the updates on updatedCommandModel are not directly saved in db
        em.detach(updatedCommandModel);
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(updatedCommandModel);

        restCommandModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandModelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isOk());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
        CommandModel testCommandModel = commandModelList.get(commandModelList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingCommandModel() throws Exception {
        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();
        commandModel.setId(count.incrementAndGet());

        // Create the CommandModel
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandModelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommandModel() throws Exception {
        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();
        commandModel.setId(count.incrementAndGet());

        // Create the CommandModel
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommandModel() throws Exception {
        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();
        commandModel.setId(count.incrementAndGet());

        // Create the CommandModel
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandModelMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommandModelWithPatch() throws Exception {
        // Initialize the database
        commandModelRepository.saveAndFlush(commandModel);

        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();

        // Update the commandModel using partial update
        CommandModel partialUpdatedCommandModel = new CommandModel();
        partialUpdatedCommandModel.setId(commandModel.getId());

        restCommandModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommandModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandModel))
            )
            .andExpect(status().isOk());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
        CommandModel testCommandModel = commandModelList.get(commandModelList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateCommandModelWithPatch() throws Exception {
        // Initialize the database
        commandModelRepository.saveAndFlush(commandModel);

        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();

        // Update the commandModel using partial update
        CommandModel partialUpdatedCommandModel = new CommandModel();
        partialUpdatedCommandModel.setId(commandModel.getId());

        restCommandModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommandModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandModel))
            )
            .andExpect(status().isOk());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
        CommandModel testCommandModel = commandModelList.get(commandModelList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingCommandModel() throws Exception {
        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();
        commandModel.setId(count.incrementAndGet());

        // Create the CommandModel
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commandModelDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommandModel() throws Exception {
        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();
        commandModel.setId(count.incrementAndGet());

        // Create the CommandModel
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommandModel() throws Exception {
        int databaseSizeBeforeUpdate = commandModelRepository.findAll().size();
        commandModel.setId(count.incrementAndGet());

        // Create the CommandModel
        CommandModelDTO commandModelDTO = commandModelMapper.toDto(commandModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandModelMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandModelDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommandModel in the database
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommandModel() throws Exception {
        // Initialize the database
        commandModelRepository.saveAndFlush(commandModel);

        int databaseSizeBeforeDelete = commandModelRepository.findAll().size();

        // Delete the commandModel
        restCommandModelMockMvc
            .perform(delete(ENTITY_API_URL_ID, commandModel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CommandModel> commandModelList = commandModelRepository.findAll();
        assertThat(commandModelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
