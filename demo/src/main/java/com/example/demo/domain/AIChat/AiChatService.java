package your.pkg.ai;

import your.pkg.ai.dto.AiDtos.ChatMessage;
import your.pkg.ai.dto.AiDtos.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class AiChatService {

    private final WebClient webClient;
    private final String model;

    public AiChatService(
            @Value("${ai.base-url}") String baseUrl,
            @Value("${ai.api-key}") String apiKey,
            @Value("${ai.model}") String model) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = model;
    }

    public ChatResponse chat(String userMsg, List<ChatMessage> history) {
        // OpenAI Chat Completions 스타일 페이로드 구성
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role","system","content","You are a helpful assistant for a healthcare app."));
        if (history != null) {
            for (ChatMessage m : history) {
                messages.add(Map.of("role", m.role(), "content", m.content()));
            }
        }
        messages.add(Map.of("role","user","content", userMsg));

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", messages,
                "temperature", 0.7,
                "stream", false
        );

        Map<?,?> res = webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // res에서 첫 번째 답 추출
        try {
            List<?> choices = (List<?>) res.get("choices");
            Map<?,?> first = (Map<?,?>) choices.get(0);
            Map<?,?> msg = (Map<?,?>) first.get("message");
            String content = String.valueOf(msg.get("content"));
            return new ChatResponse(content);
        } catch (Exception e) {
            return new ChatResponse("[AI 응답 파싱 에러] " + e.getMessage());
        }
    }
}
