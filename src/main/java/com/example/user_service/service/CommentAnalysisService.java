package com.example.user_service.service;

import com.example.user_service.dto.CommentAnalysisResponse;

/**
 * Service interface para análisis de comentarios usando Azure AI Text Analytics
 */
public interface CommentAnalysisService {

    /**
     * Analiza el sentimiento de un comentario general sobre la plataforma
     * y lo guarda en la base de datos
     * 
     * @param userId  ID del usuario que hace el comentario
     * @param comment El comentario a analizar
     * @return Respuesta con el análisis de sentimiento
     */
    CommentAnalysisResponse analyzeGeneralComment(Integer userId, String comment);

    /**
     * Analiza el sentimiento de un comentario sobre un concierto específico
     * y lo guarda en la base de datos
     * 
     * @param userId      ID del usuario que hace el comentario
     * @param concertName Nombre del concierto
     * @param comment     El comentario a analizar
     * @return Respuesta con el análisis de sentimiento
     */
    CommentAnalysisResponse analyzeConcertComment(Integer userId, String concertName, String comment);
}
