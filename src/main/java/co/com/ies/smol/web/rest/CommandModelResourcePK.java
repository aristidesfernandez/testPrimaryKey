package co.com.ies.smol.web.rest;

import co.com.ies.smol.service.CommandModelServicePK;
import co.com.ies.smol.service.dto.CommandModelDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link co.com.ies.smol.domain.CommandModel}.
 */
@RestController
@RequestMapping("/test")
public class CommandModelResourcePK {

    private final CommandModelServicePK commandModelService;

    public CommandModelResourcePK(CommandModelServicePK commandModelService) {
        this.commandModelService = commandModelService;
    }

    @GetMapping("/command-models/save")
    public ResponseEntity<CommandModelDTO> createCommandModel() {
        commandModelService.save();
        return ResponseEntity.ok(null);
    }

    @GetMapping("/command-models")
    public ResponseEntity<List<CommandModelDTO>> getAllCommandModels(@PathVariable(value = "id", required = false) final Long id) {
        commandModelService.findAll();
        return ResponseEntity.ok(null);
    }

    @GetMapping("/command-models/{id}")
    public ResponseEntity<List<CommandModelDTO>> getAllCommandModels() {
        commandModelService.findAll();
        return ResponseEntity.ok(null);
    }
}
