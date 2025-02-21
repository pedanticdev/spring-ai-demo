package fish.payara.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/chat",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatResource.class);

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<String> post(@Valid @RequestBody ChatRequest chatRequest) {
        String chat = chatService.chat(chatRequest);
        LOGGER.info("Chat request: {}", chat);

        return ResponseEntity.ok(chat);

    }
}
