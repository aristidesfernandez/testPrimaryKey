package co.com.ies.smol.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "command_to_model")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommandToModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CommandModelPK id;

    @ManyToOne
    @JoinColumn(name = "command_id", insertable = false, updatable = false)
    private Command command;

    @ManyToOne
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    private Model model;

    public Command getCommand() {
        return this.command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public CommandModelPK getId() {
        return id;
    }

    public void setId(CommandModelPK id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CommandToModel other = (CommandToModel) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "CommandToModel [id=" + id + ", command=" + command + ", model=" + model + "]";
    }
}
