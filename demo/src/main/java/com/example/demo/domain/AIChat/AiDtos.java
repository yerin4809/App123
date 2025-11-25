package your.pkg.ai.dto;
import java.util.List;

public class AiDtos {
    public record ChatMessage(String role, String content) {}
    public record ChatRequest(String message, List<ChatMessage> history) {}
    public record ChatResponse(String answer) {}
}
