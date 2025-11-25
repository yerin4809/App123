package your.pkg.ai;

import your.pkg.ai.dto.AiDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService service;
    public AiChatController(AiChatService service) { this.service = service; }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest req) {
        ChatResponse res = service.chat(req.message(), req.history());
        return ResponseEntity.ok(res);
    }
}
