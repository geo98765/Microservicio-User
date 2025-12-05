package com.example.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para enviar comentarios generales sobre la página
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to submit and analyze a general comment about the platform")
public class GeneralCommentRequest {

    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 3, max = 5000, message = "Comment must be between 3 and 5000 characters")
    @Schema(description = "General comment about the platform", example = "Me encanta RockStadium! Es la mejor aplicación para encontrar conciertos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String comment;
}
