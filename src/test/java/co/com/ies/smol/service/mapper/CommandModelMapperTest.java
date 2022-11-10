package co.com.ies.smol.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandModelMapperTest {

    private CommandModelMapper commandModelMapper;

    @BeforeEach
    public void setUp() {
        commandModelMapper = new CommandModelMapperImpl();
    }
}
