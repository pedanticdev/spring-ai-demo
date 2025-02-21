package fish.payara.spring;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PostgresEmbeddingService implements EmbeddingService {

    static final Logger LOGGER = Logger.getLogger(PostgresEmbeddingService.class.getName());

    @Autowired
    @Qualifier("customVectorStore")
    PgVectorStore vectorStore;

    @Autowired
    private FirebaseBlobManager firebaseBlobManager;

    @Override
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    public void embedNewDocs() {
        LOGGER.info("About to start embedding docs");
        try {
            List<String> objectKeys = firebaseBlobManager.listObjects();
            List<String> embeddedDocs = new ArrayList<>();

            Iterator<String> iterator = objectKeys.iterator();
            while (iterator.hasNext()) {
                String objectKey = iterator.next();
                LOGGER.log(Level.INFO, "Embedding doc " + objectKey);

                List<Document> documents = firebaseBlobManager.loadDocuments(objectKey);
                LOGGER.info("Embedding docs loaded: " + documents.size());

                if (!documents.isEmpty()) {
                    vectorStore.add(splitDocuments(documents));
                    embeddedDocs.add(objectKey);
                    iterator.remove();
                } else {
                    LOGGER.log(Level.INFO, "No documents to embed for objectKey: " + objectKey);
                }
            }
            firebaseBlobManager.moveEmbeddedDocument(embeddedDocs);

        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error embedding docs", e);
        }
    }

    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }
}
