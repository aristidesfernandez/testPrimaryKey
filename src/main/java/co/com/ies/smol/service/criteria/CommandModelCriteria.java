package co.com.ies.smol.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link co.com.ies.smol.domain.CommandModel} entity. This class is used
 * in {@link co.com.ies.smol.web.rest.CommandModelResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /command-models?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommandModelCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter commandId;

    private LongFilter modelId;

    private Boolean distinct;

    public CommandModelCriteria() {}

    public CommandModelCriteria(CommandModelCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.commandId = other.commandId == null ? null : other.commandId.copy();
        this.modelId = other.modelId == null ? null : other.modelId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CommandModelCriteria copy() {
        return new CommandModelCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getCommandId() {
        return commandId;
    }

    public LongFilter commandId() {
        if (commandId == null) {
            commandId = new LongFilter();
        }
        return commandId;
    }

    public void setCommandId(LongFilter commandId) {
        this.commandId = commandId;
    }

    public LongFilter getModelId() {
        return modelId;
    }

    public LongFilter modelId() {
        if (modelId == null) {
            modelId = new LongFilter();
        }
        return modelId;
    }

    public void setModelId(LongFilter modelId) {
        this.modelId = modelId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CommandModelCriteria that = (CommandModelCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(commandId, that.commandId) &&
            Objects.equals(modelId, that.modelId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commandId, modelId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandModelCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (commandId != null ? "commandId=" + commandId + ", " : "") +
            (modelId != null ? "modelId=" + modelId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
