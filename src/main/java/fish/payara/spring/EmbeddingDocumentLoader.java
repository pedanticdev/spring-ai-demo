package fish.payara.spring;

import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;

public interface EmbeddingDocumentLoader {
    List<Document> loadDocuments();

    Document loadDocument(String documentKey);
    List<Document> loadDocuments(String documentKey);

    default void moveEmbeddedDocument(List<String> documentKeys) {}

    default List<String> listObjects() {
        return new ArrayList<>();
    }
}
