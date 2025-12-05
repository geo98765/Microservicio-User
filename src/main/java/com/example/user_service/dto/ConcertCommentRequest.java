package com.example.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para enviar comentarios sobre conciertos específicos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to submit and analyze a comment about a specific concert")
public class ConcertCommentRequest {

    @NotBlank(message = "Concert name cannot be empty")
    @Size(min = 2, max = 200, message = "Concert name must be between 2 and 200 characters")
    @Schema(description = "Name of the concert being commented on", example = "Metallica Live in Mexico", requiredMode = Schema.RequiredMode.REQUIRED)
    private String concertName;

    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 3, max = 5000, message = "Comment must be between 3 and 5000 characters")
    @Schema(description = "Comment about the concert", example = "El concierto fue increíble, la mejor experiencia de mi vida!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String comment;
}
