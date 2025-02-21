package fish.payara.spring;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Scope("singleton")
public class ChatService {
    String SYSTEM_MESSAGE =
            """
         Context information is below.

			---------------------
			{context}
			---------------------
			
			Given the context information and no prior knowledge, answer the query.

			Follow these rules:

         You are an expert Java technology advisor specializing in enterprise Java platforms (Java EE, Jakarta EE), cloud deployment, and Payara products. Your knowledge encompasses:
                    
                                                                         Technical domains:
                                                                         - Java EE/Jakarta EE frameworks and specifications \s
                                                                         - Enterprise Java development
                                                                         - Microprofile implementations
                                                                         - Container technologies (Docker, Kubernetes)
                                                                         - Cloud platforms (AWS, GCP, Azure)
                                                                         - Payara Server and Payara Cloud
                    
                                                                         Core responsibilities:
                                                                         1. Provide technical guidance on enterprise Java implementations
                                                                         2. Advise on Payara product deployment and usage\s
                                                                         3. Share architectural best practices for Java cloud solutions
                                                                         4. Assist with DevSecOps strategies for Java applications
                                                                         5. Explain Payara-specific features and capabilities
                    
                                                                         Key constraints:
                                                                         - Only discuss topics within the specified technical domains
                                                                         - For complex queries, direct users to payara.fish
                                                                         - Maintain strictly technical focus
                                                                         - No discussions outside Java ecosystem and cloud technologies
                                                                         - Exclude non-technical topics entirely
                    
                                                                         Response approach:
                                                                         - Technical queries: Provide detailed implementation guidance
                                                                         - Product queries: Focus on technical capabilities and practical benefits
                                                                         - Architecture queries: Share proven patterns and best practices
                                                                         - Integration queries: Explain compatibility and deployment approaches
                                                                         - Respond in GitHub flavored markdown
    
			Query: {query}

			Answer:
""";

    static final Logger LOGGER = Logger.getLogger(PostgresEmbeddingService.class.getName());

    @Value("${advisor.allow-empty-context}")
    private String allowEmptyContext;

    @Autowired
    @Qualifier("customVectorStore")
    PgVectorStore vectorStore;

    @Autowired
    ChatModel chatModel;

    public String chat(ChatRequest request) {
        LOGGER.log(Level.INFO, "Making chat request-->  " + request);

        ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
                .promptTemplate(new SystemPromptTemplate(SYSTEM_MESSAGE))
                .allowEmptyContext(Boolean.parseBoolean(allowEmptyContext))
                .build();

        Advisor advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(queryAugmenter)
                .build();

        ChatResponse response = ChatClient.builder(chatModel)
                .build().prompt()
                .advisors(advisor)
                .user(request.userMessage())
                .call()
                .chatResponse();
        LOGGER.log(Level.INFO, "Finished chat request -->  " + response);
        if (response != null && response.getResult() != null) {
            LOGGER.log(Level.INFO, "Finished chat request -->  " + response.getResult().getOutput().getText());
            return response.getResult().getOutput().getText();
        }

        return null;
    }
}
