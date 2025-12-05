package com.example.user_service.config;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Azure AI Configuration
 * Configura el cliente de Text Analytics para análisis de sentimiento
 */
@Configuration
public class AzureAIConfig {

    @Value("${azure.ai.endpoint}")
    private String endpoint;

    @Value("${azure.ai.key}")
    private String key;

    /**
     * Cliente para Azure AI Text Analytics (Language Services)
     * Usado para análisis de sentimiento de comentarios
     */
    @Bean
    public TextAnalyticsClient textAnalyticsClient() {
        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }
}
