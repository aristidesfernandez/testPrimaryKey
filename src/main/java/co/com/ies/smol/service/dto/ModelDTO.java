package co.com.ies.smol.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link co.com.ies.smol.domain.Model} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModelDTO implements Serializable {

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModelDTO)) {
            return false;
        }

        ModelDTO modelDTO = (ModelDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, modelDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModelDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
