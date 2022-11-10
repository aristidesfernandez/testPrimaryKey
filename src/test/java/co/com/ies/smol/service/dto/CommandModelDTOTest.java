package co.com.ies.smol.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.com.ies.smol.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommandModelDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommandModelDTO.class);
        CommandModelDTO commandModelDTO1 = new CommandModelDTO();
        commandModelDTO1.setId(1L);
        CommandModelDTO commandModelDTO2 = new CommandModelDTO();
        assertThat(commandModelDTO1).isNotEqualTo(commandModelDTO2);
        commandModelDTO2.setId(commandModelDTO1.getId());
        assertThat(commandModelDTO1).isEqualTo(commandModelDTO2);
        commandModelDTO2.setId(2L);
        assertThat(commandModelDTO1).isNotEqualTo(commandModelDTO2);
        commandModelDTO1.setId(null);
        assertThat(commandModelDTO1).isNotEqualTo(commandModelDTO2);
    }
}
