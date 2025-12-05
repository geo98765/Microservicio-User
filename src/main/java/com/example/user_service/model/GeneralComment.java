package com.example.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para almacenar comentarios generales sobre la plataforma
 * con su análisis de sentimiento de Azure AI
 */
@Entity
@Table(name = "general_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "comment", nullable = false, length = 5000)
    private String comment;

    @Column(name = "sentiment", length = 20)
    private String sentiment;

    @Column(name = "confidence_positive")
    private Double confidencePositive;

    @Column(name = "confidence_neutral")
    private Double confidenceNeutral;

    @Column(name = "confidence_negative")
    private Double confidenceNegative;

    @Column(name = "detected_language", length = 10)
    private String detectedLanguage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
