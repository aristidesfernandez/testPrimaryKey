package co.com.ies.smol.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import co.com.ies.smol.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommandDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommandDTO.class);
        CommandDTO commandDTO1 = new CommandDTO();
        commandDTO1.setId(1L);
        CommandDTO commandDTO2 = new CommandDTO();
        assertThat(commandDTO1).isNotEqualTo(commandDTO2);
        commandDTO2.setId(commandDTO1.getId());
        assertThat(commandDTO1).isEqualTo(commandDTO2);
        commandDTO2.setId(2L);
        assertThat(commandDTO1).isNotEqualTo(commandDTO2);
        commandDTO1.setId(null);
        assertThat(commandDTO1).isNotEqualTo(commandDTO2);
    }
}
