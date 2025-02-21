package fish.payara.spring;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class OpenAiConfig {


    @Bean
    public Storage getStorage() {

        ClassPathResource resourceAsStream = new ClassPathResource("firebase-auth.json");
        System.out.println(resourceAsStream);
        try {
            return StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(
                            resourceAsStream.getInputStream()))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
