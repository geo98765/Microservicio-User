package com.example.user_service.repository;

import com.example.user_service.model.ConcertComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar comentarios de conciertos
 */
@Repository
public interface ConcertCommentRepository extends JpaRepository<ConcertComment, Integer> {

    /**
     * Obtiene todos los comentarios de un usuario específico
     */
    List<ConcertComment> findByUserId(Integer userId);

    /**
     * Obtiene comentarios de un concierto específico
     */
    List<ConcertComment> findByConcertName(String concertName);

    /**
     * Obtiene comentarios por sentimiento
     */
    List<ConcertComment> findBySentiment(String sentiment);
}
