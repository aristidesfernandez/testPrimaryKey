package co.com.ies.smol.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommandModelPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "command_id")
    private Long commandId;

    @Column(name = "model_id")
    private Long modelId;

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Override
    public String toString() {
        return "CommandModelPK [commandId=" + commandId + ", modelId=" + modelId + "]";
    }
}
