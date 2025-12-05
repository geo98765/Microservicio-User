package com.example.user_service.controller;

import com.example.user_service.dto.CommentAnalysisResponse;
import com.example.user_service.dto.ConcertCommentRequest;
import com.example.user_service.dto.GeneralCommentRequest;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.CommentAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para análisis de comentarios usando Azure AI
 * Endpoints protegidos que requieren autenticación
 */
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comments", description = "Comment analysis endpoints using Azure AI Text Analytics")
public class CommentController {

        private final CommentAnalysisService commentAnalysisService;
        private final UserRepository userRepository;

        /**
         * Endpoint para analizar comentarios generales sobre la plataforma
         * Requiere autenticación HTTP Basic
         */
        @PostMapping("/general")
        @Operation(summary = "Analyze general comment sentiment", description = "Analyzes the sentiment of a general comment about the RockStadium platform using Azure AI Text Analytics. Requires authentication.", security = @SecurityRequirement(name = "basicAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comment analyzed successfully", content = @Content(schema = @Schema(implementation = CommentAnalysisResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body or validation error"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
                        @ApiResponse(responseCode = "500", description = "Internal server error or Azure AI service error")
        })
        public ResponseEntity<CommentAnalysisResponse> analyzeGeneralComment(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "General comment to analyze", required = true) @Valid @RequestBody GeneralCommentRequest request) {

                log.info("Received request to analyze general comment");

                // Obtener usuario autenticado
                Integer userId = getAuthenticatedUserId();

                CommentAnalysisResponse response = commentAnalysisService.analyzeGeneralComment(
                                userId,
                                request.getComment());

                log.info("General comment analyzed successfully with sentiment: {}", response.getSentiment());
                return ResponseEntity.ok(response);
        }

        /**
         * Endpoint para analizar comentarios sobre conciertos específicos
         * Requiere autenticación HTTP Basic
         */
        @PostMapping("/concert")
        @Operation(summary = "Analyze concert comment sentiment", description = "Analyzes the sentiment of a comment about a specific concert using Azure AI Text Analytics. Requires authentication.", security = @SecurityRequirement(name = "basicAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comment analyzed successfully", content = @Content(schema = @Schema(implementation = CommentAnalysisResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body or validation error"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
                        @ApiResponse(responseCode = "500", description = "Internal server error or Azure AI service error")
        })
        public ResponseEntity<CommentAnalysisResponse> analyzeConcertComment(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Concert comment to analyze", required = true) @Valid @RequestBody ConcertCommentRequest request) {

                log.info("Received request to analyze concert comment for: {}", request.getConcertName());

                // Obtener usuario autenticado
                Integer userId = getAuthenticatedUserId();

                CommentAnalysisResponse response = commentAnalysisService.analyzeConcertComment(
                                userId,
                                request.getConcertName(),
                                request.getComment());

                log.info("Concert comment analyzed successfully with sentiment: {}", response.getSentiment());
                return ResponseEntity.ok(response);
        }

        /**
         * Exception handler para errores del servicio de análisis
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handleAnalysisException(RuntimeException ex) {
                log.error("Error analyzing comment: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error analyzing comment: " + ex.getMessage());
        }

        /**
         * Obtiene el ID del usuario autenticado desde el contexto de seguridad
         */
        private Integer getAuthenticatedUserId() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();

                // Obtener usuario por email
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found: " + email));

                return user.getUserId();
        }
}
