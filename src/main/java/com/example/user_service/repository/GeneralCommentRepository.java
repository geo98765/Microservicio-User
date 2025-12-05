package com.example.user_service.repository;

import com.example.user_service.model.GeneralComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar comentarios generales
 */
@Repository
public interface GeneralCommentRepository extends JpaRepository<GeneralComment, Integer> {

    /**
     * Obtiene todos los comentarios de un usuario específico
     */
    List<GeneralComment> findByUserId(Integer userId);

    /**
     * Obtiene comentarios por sentimiento
     */
    List<GeneralComment> findBySentiment(String sentiment);
}
