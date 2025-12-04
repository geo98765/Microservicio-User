package com.example.user_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.user_service.dto.ArtistResponse;
import com.example.user_service.dto.MusicGenreResponse;
import com.example.user_service.dto.UserPreferenceBasicResponse;
import com.example.user_service.dto.UserPreferenceResponse;
import com.example.user_service.model.UserPreference;

/**
 * Mapper for UserPreference entities and DTOs
 * This mapper ONLY does simple entity-to-DTO conversions
 * Business logic (like enriching with Spotify data) belongs in the Service
 * layer
 */
@Component
public class UserPreferenceMapper {

    private static final int MAX_FAVORITE_ARTISTS = 40;
    private static final int MAX_FAVORITE_GENRES = 30;

    /**
     * Convert UserPreference entity to Response DTO
     * Convierte entidad UserPreference a DTO de respuesta
     * 
     * @param preference The user preference entity
     * @param artists    List of artist responses (already mapped/enriched)
     * @param genres     List of genre responses (already mapped)
     * @return UserPreferenceResponse DTO
     */
    public UserPreferenceResponse toResponse(UserPreference preference,
            List<ArtistResponse> artists,
            List<MusicGenreResponse> genres) {
        return UserPreferenceResponse.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(preference.getProfile().getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .favoriteArtists(artists)
                .favoriteGenres(genres)
                .favoriteArtistsCount(artists.size())
                .favoriteGenresCount(genres.size())
                .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
                .maxFavoriteGenres(MAX_FAVORITE_GENRES)
                .build();
    }

    /**
     * Convert UserPreference entity to Basic Response DTO (only essential fields)
     * Convierte entidad UserPreference a DTO básico de respuesta (solo campos
     * esenciales)
     * 
     * @param preference The user preference entity
     * @return UserPreferenceBasicResponse DTO with only basic information
     */
    public UserPreferenceBasicResponse toBasicResponse(UserPreference preference) {
        return UserPreferenceBasicResponse.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(preference.getProfile().getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .build();
    }

    /**
     * Convert MusicGenre entity to Response DTO
     * Convierte entidad MusicGenre a DTO de respuesta
     * 
     * @param genre The music genre entity
     * @return MusicGenreResponse DTO
     */
    public MusicGenreResponse toGenreResponse(com.example.user_service.model.MusicGenre genre) {
        return MusicGenreResponse.builder()
                .musicGenreId(genre.getMusicGenreId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }
}
