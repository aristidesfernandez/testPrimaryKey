package co.com.ies.smol.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "command_to_model")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommandToModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CommandModelPK id;

    public CommandModelPK getId() {
        return id;
    }

    public void setId(CommandModelPK id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommandToModel [id=" + id + "]";
    }
}
