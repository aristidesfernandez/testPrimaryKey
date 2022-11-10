package co.com.ies.smol.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.com.ies.smol.IntegrationTest;
import co.com.ies.smol.domain.Model;
import co.com.ies.smol.repository.ModelRepository;
import co.com.ies.smol.service.criteria.ModelCriteria;
import co.com.ies.smol.service.dto.ModelDTO;
import co.com.ies.smol.service.mapper.ModelMapper;
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
 * Integration tests for the {@link ModelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ModelResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/models";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModelMockMvc;

    private Model model;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Model createEntity(EntityManager em) {
        Model model = new Model().name(DEFAULT_NAME);
        return model;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Model createUpdatedEntity(EntityManager em) {
        Model model = new Model().name(UPDATED_NAME);
        return model;
    }

    @BeforeEach
    public void initTest() {
        model = createEntity(em);
    }

    @Test
    @Transactional
    void createModel() throws Exception {
        int databaseSizeBeforeCreate = modelRepository.findAll().size();
        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);
        restModelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modelDTO)))
            .andExpect(status().isCreated());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeCreate + 1);
        Model testModel = modelList.get(modelList.size() - 1);
        assertThat(testModel.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createModelWithExistingId() throws Exception {
        // Create the Model with an existing ID
        model.setId(1L);
        ModelDTO modelDTO = modelMapper.toDto(model);

        int databaseSizeBeforeCreate = modelRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllModels() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        // Get all the modelList
        restModelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(model.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getModel() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        // Get the model
        restModelMockMvc
            .perform(get(ENTITY_API_URL_ID, model.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(model.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getModelsByIdFiltering() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        Long id = model.getId();

        defaultModelShouldBeFound("id.equals=" + id);
        defaultModelShouldNotBeFound("id.notEquals=" + id);

        defaultModelShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultModelShouldNotBeFound("id.greaterThan=" + id);

        defaultModelShouldBeFound("id.lessThanOrEqual=" + id);
        defaultModelShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllModelsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        // Get all the modelList where name equals to DEFAULT_NAME
        defaultModelShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the modelList where name equals to UPDATED_NAME
        defaultModelShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllModelsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        // Get all the modelList where name in DEFAULT_NAME or UPDATED_NAME
        defaultModelShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the modelList where name equals to UPDATED_NAME
        defaultModelShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllModelsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        // Get all the modelList where name is not null
        defaultModelShouldBeFound("name.specified=true");

        // Get all the modelList where name is null
        defaultModelShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllModelsByNameContainsSomething() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        // Get all the modelList where name contains DEFAULT_NAME
        defaultModelShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the modelList where name contains UPDATED_NAME
        defaultModelShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllModelsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        // Get all the modelList where name does not contain DEFAULT_NAME
        defaultModelShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the modelList where name does not contain UPDATED_NAME
        defaultModelShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultModelShouldBeFound(String filter) throws Exception {
        restModelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(model.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restModelMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultModelShouldNotBeFound(String filter) throws Exception {
        restModelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restModelMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingModel() throws Exception {
        // Get the model
        restModelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingModel() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        int databaseSizeBeforeUpdate = modelRepository.findAll().size();

        // Update the model
        Model updatedModel = modelRepository.findById(model.getId()).get();
        // Disconnect from session so that the updates on updatedModel are not directly saved in db
        em.detach(updatedModel);
        updatedModel.name(UPDATED_NAME);
        ModelDTO modelDTO = modelMapper.toDto(updatedModel);

        restModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modelDTO))
            )
            .andExpect(status().isOk());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
        Model testModel = modelList.get(modelList.size() - 1);
        assertThat(testModel.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingModel() throws Exception {
        int databaseSizeBeforeUpdate = modelRepository.findAll().size();
        model.setId(count.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchModel() throws Exception {
        int databaseSizeBeforeUpdate = modelRepository.findAll().size();
        model.setId(count.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModel() throws Exception {
        int databaseSizeBeforeUpdate = modelRepository.findAll().size();
        model.setId(count.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateModelWithPatch() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        int databaseSizeBeforeUpdate = modelRepository.findAll().size();

        // Update the model using partial update
        Model partialUpdatedModel = new Model();
        partialUpdatedModel.setId(model.getId());

        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModel))
            )
            .andExpect(status().isOk());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
        Model testModel = modelList.get(modelList.size() - 1);
        assertThat(testModel.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateModelWithPatch() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        int databaseSizeBeforeUpdate = modelRepository.findAll().size();

        // Update the model using partial update
        Model partialUpdatedModel = new Model();
        partialUpdatedModel.setId(model.getId());

        partialUpdatedModel.name(UPDATED_NAME);

        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModel))
            )
            .andExpect(status().isOk());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
        Model testModel = modelList.get(modelList.size() - 1);
        assertThat(testModel.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingModel() throws Exception {
        int databaseSizeBeforeUpdate = modelRepository.findAll().size();
        model.setId(count.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modelDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModel() throws Exception {
        int databaseSizeBeforeUpdate = modelRepository.findAll().size();
        model.setId(count.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModel() throws Exception {
        int databaseSizeBeforeUpdate = modelRepository.findAll().size();
        model.setId(count.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(modelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Model in the database
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteModel() throws Exception {
        // Initialize the database
        modelRepository.saveAndFlush(model);

        int databaseSizeBeforeDelete = modelRepository.findAll().size();

        // Delete the model
        restModelMockMvc
            .perform(delete(ENTITY_API_URL_ID, model.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Model> modelList = modelRepository.findAll();
        assertThat(modelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
