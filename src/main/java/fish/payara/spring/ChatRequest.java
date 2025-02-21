package fish.payara.spring;

public record ChatRequest(String userMessage, ChatRequestMetadata metadata) {
}
