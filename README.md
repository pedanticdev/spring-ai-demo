# Spring Boot AI Webinar Project

This project demonstrates how to build an AI-powered Spring Boot application that can:

* Load documents (PDFs) from a Firebase bucket.
* Embed these documents using Spring AI's `EmbeddingModel`.
* Store embeddings in a PGVector database for efficient similarity search.
* Expose a REST endpoint to chat with the application, leveraging the embeddings for contextually relevant responses.

## Prerequisites

* Java 21
* Maven
* Docker
* A PostgreSQL database
* A Firebase account with a project and a bucket
* An OpenAI API key

## Setup

1. **Clone the repository:**

   ```bash
   git clone git@github.com:pedanticdev/spring-ai-demo.git
   ```

2. **Configure environment variables:**

    * Create a file named `.env` in the root directory.
    * Add the following variables, replacing the placeholders with your actual values:

      ```properties
      OPEN_API_KEY=your_openai_api_key
      DB_URL=jdbc:postgresql://your_db_host:5432/your_db_name
      DB_USER=your_db_username
      DB_PASSWORD=your_db_password
      ```

3. **Add Firebase credentials:**

    * Download your Firebase service account key as a JSON file (e.g., `firebase-auth.json`).
    * Place this file in the `src/main/resources` directory.

4. **Build and run the application:**

   ```bash
   mvn spring-boot:run
   ```

## Usage

1. **Upload PDF documents to your Firebase bucket** under the `rag/uploaded/` path.
2. The application will automatically detect new documents, embed them, and store the embeddings in the database.
3. Send a POST request to the `/api/v1/chat` endpoint with a JSON payload like this:

   ```json
   {
     "userMessage": "your question",
     "metadata": {}
   }
   ```

   The application will respond with a contextually relevant answer based on the information in the uploaded documents.

## Additional Notes

* The application uses Spring AI's `RetrievalAugmentationAdvisor` to augment the chat prompt with relevant document chunks.
* The `PgVectorStore` is used to store and query embeddings.
* The `FirebaseBlobManager` handles loading documents from Firebase and moving them to a different path after embedding.
* The `OpenAiConfig` class configures the OpenAI API client.

## UI
This app exposes a REST endpoint that can be used with the corresponding JS (React) app found [here](https://github.com/pedanticdev/jee-chat-ui). 

## Contributing

Feel free to submit pull requests or issues if you find any bugs or have suggestions for improvement.

