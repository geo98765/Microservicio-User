package com.example.user_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.user_service.dto.AddFavoriteGenreRequest;
import com.example.user_service.dto.ArtistResponse;
import com.example.user_service.dto.DeleteFavoriteGenreRequest;
import com.example.user_service.dto.MusicGenreResponse;
import com.example.user_service.dto.SuccessResponse;
import com.example.user_service.dto.UserPreferenceBasicResponse;
import com.example.user_service.dto.UserPreferenceRequest;
import com.example.user_service.dto.UserPreferenceResponse;

/**
 * Service interface for user preferences management
 */
public interface UserPreferenceService {

    // ===== SEARCH PREFERENCES =====

    /**
     * Create or update user preferences
     */
    UserPreferenceBasicResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request);

    /**
     * Get user preferences (with optional pagination for lists)
     */
    UserPreferenceResponse getPreferences(Integer userId, boolean includeFullLists);

    // ===== FAVORITE ARTISTS =====

    /**
     * Add favorite artist
     * 
     * @return Only the added artist information
     */
    ArtistResponse addFavoriteArtist(Integer userId, String spotifyId);

    /**
     * Remove favorite artist
     * 
     * @return Success message
     */
    SuccessResponse removeFavoriteArtist(Integer userId, String spotifyId);

    /**
     * Get all favorite artists with pagination
     * Obtener todos los artistas favoritos con paginación
     */
    Page<ArtistResponse> getFavoriteArtists(Integer userId, Pageable pageable);

    // ===== FAVORITE GENRES =====

    /**
     * Add favorite genre (by ID or name)
     * 
     * @return Only the added genre information
     */
    MusicGenreResponse addFavoriteGenre(Integer userId, AddFavoriteGenreRequest request);

    /**
     * Remove favorite genre (by ID or name)
     * 
     * @return Success message
     */
    SuccessResponse removeFavoriteGenre(Integer userId, DeleteFavoriteGenreRequest request);

    /**
     * Get all favorite genres with pagination
     * Obtener todos los géneros favoritos con paginación
     */
    Page<MusicGenreResponse> getFavoriteGenres(Integer userId, Pageable pageable);

    /**
     * Get all available music genres
     * Obtener todos los géneros musicales disponibles
     */
    List<MusicGenreResponse> getAllGenres();
}
