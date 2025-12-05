package com.example.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO con el análisis de sentimiento de un comentario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing sentiment analysis results for a comment")
public class CommentAnalysisResponse {

    @Schema(description = "The original comment that was analyzed", example = "Me encanta RockStadium!")
    private String originalComment;

    @Schema(description = "Detected sentiment", example = "positive", allowableValues = { "positive", "negative",
            "neutral", "mixed" })
    private String sentiment;

    @Schema(description = "Confidence scores for each sentiment category", example = "{\"positive\": 0.98, \"neutral\": 0.01, \"negative\": 0.01}")
    private Map<String, Double> confidenceScores;

    @Schema(description = "Detected language of the comment", example = "es")
    private String detectedLanguage;

    @Schema(description = "Timestamp when the analysis was performed", example = "2025-12-05T12:05:00")
    private LocalDateTime analyzedAt;

    @Schema(description = "Optional context (e.g., concert name for concert comments)", example = "Metallica Live in Mexico")
    private String context;
}
