package co.com.ies.smol.web.rest;

import co.com.ies.smol.service.CommandModelServicePK;
import co.com.ies.smol.service.dto.CommandModelDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link co.com.ies.smol.domain.CommandModel}.
 */
@RestController
@RequestMapping("/test")
public class CommandModelResourcePK {

    private final Logger log = LoggerFactory.getLogger(CommandModelResource.class);

    private final CommandModelServicePK commandModelService;

    public CommandModelResourcePK(CommandModelServicePK commandModelService) {
        this.commandModelService = commandModelService;
    }

    @GetMapping("/command-models")
    public ResponseEntity<List<CommandModelDTO>> getAllCommandModels() {
        commandModelService.findAll();
        return ResponseEntity.ok(null);
    }
}
