package com.example.user_service.service;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.SentimentConfidenceScores;
import com.azure.core.exception.AzureException;
import com.example.user_service.dto.CommentAnalysisResponse;
import com.example.user_service.model.ConcertComment;
import com.example.user_service.model.GeneralComment;
import com.example.user_service.repository.ConcertCommentRepository;
import com.example.user_service.repository.GeneralCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de análisis de comentarios usando Azure AI Text
 * Analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentAnalysisServiceImpl implements CommentAnalysisService {

    private final TextAnalyticsClient textAnalyticsClient;
    private final GeneralCommentRepository generalCommentRepository;
    private final ConcertCommentRepository concertCommentRepository;

    @Override
    @Transactional
    public CommentAnalysisResponse analyzeGeneralComment(Integer userId, String comment) {
        log.info("Analyzing general comment sentiment for user: {}", userId);

        // Analizar sentimiento
        CommentAnalysisResponse response = analyzeComment(comment, null);

        // Guardar en base de datos
        GeneralComment generalComment = GeneralComment.builder()
                .userId(userId)
                .comment(comment)
                .sentiment(response.getSentiment())
                .confidencePositive(response.getConfidenceScores().get("positive"))
                .confidenceNeutral(response.getConfidenceScores().get("neutral"))
                .confidenceNegative(response.getConfidenceScores().get("negative"))
                .detectedLanguage(response.getDetectedLanguage())
                .build();

        generalCommentRepository.save(generalComment);
        log.info("General comment saved to database with ID: {}", generalComment.getId());

        return response;
    }

    @Override
    @Transactional
    public CommentAnalysisResponse analyzeConcertComment(Integer userId, String concertName, String comment) {
        log.info("Analyzing concert comment sentiment for user: {} and concert: {}", userId, concertName);

        // Analizar sentimiento
        CommentAnalysisResponse response = analyzeComment(comment, concertName);

        // Guardar en base de datos
        ConcertComment concertComment = ConcertComment.builder()
                .userId(userId)
                .concertName(concertName)
                .comment(comment)
                .sentiment(response.getSentiment())
                .confidencePositive(response.getConfidenceScores().get("positive"))
                .confidenceNeutral(response.getConfidenceScores().get("neutral"))
                .confidenceNegative(response.getConfidenceScores().get("negative"))
                .detectedLanguage(response.getDetectedLanguage())
                .build();

        concertCommentRepository.save(concertComment);
        log.info("Concert comment saved to database with ID: {}", concertComment.getId());

        return response;
    }

    /**
     * Método privado que realiza el análisis de sentimiento usando Azure AI
     */
    private CommentAnalysisResponse analyzeComment(String comment, String context) {
        try {
            // Analizar sentimiento del comentario
            DocumentSentiment sentiment = textAnalyticsClient.analyzeSentiment(comment);

            // Extraer scores de confianza
            SentimentConfidenceScores scores = sentiment.getConfidenceScores();
            Map<String, Double> confidenceScores = new HashMap<>();
            confidenceScores.put("positive", (double) scores.getPositive());
            confidenceScores.put("neutral", (double) scores.getNeutral());
            confidenceScores.put("negative", (double) scores.getNegative());

            // Construir respuesta
            CommentAnalysisResponse response = CommentAnalysisResponse.builder()
                    .originalComment(comment)
                    .sentiment(sentiment.getSentiment().toString().toLowerCase())
                    .confidenceScores(confidenceScores)
                    .detectedLanguage(detectLanguage(comment))
                    .analyzedAt(LocalDateTime.now())
                    .context(context)
                    .build();

            log.info("Analysis completed successfully. Sentiment: {}", response.getSentiment());
            return response;

        } catch (AzureException e) {
            log.error("Error analyzing comment with Azure AI: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze comment sentiment: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during comment analysis: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error analyzing comment: " + e.getMessage(), e);
        }
    }

    /**
     * Detecta el idioma del comentario usando Azure AI
     */
    private String detectLanguage(String text) {
        try {
            var detectedLanguage = textAnalyticsClient.detectLanguage(text);
            return detectedLanguage.getIso6391Name();
        } catch (Exception e) {
            log.warn("Could not detect language, using default: {}", e.getMessage());
            return "unknown";
        }
    }
}
