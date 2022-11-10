package co.com.ies.smol.domain;

import static org.assertj.core.api.Assertions.assertThat;

import co.com.ies.smol.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommandModelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommandModel.class);
        CommandModel commandModel1 = new CommandModel();
        commandModel1.setId(1L);
        CommandModel commandModel2 = new CommandModel();
        commandModel2.setId(commandModel1.getId());
        assertThat(commandModel1).isEqualTo(commandModel2);
        commandModel2.setId(2L);
        assertThat(commandModel1).isNotEqualTo(commandModel2);
        commandModel1.setId(null);
        assertThat(commandModel1).isNotEqualTo(commandModel2);
    }
}
