package com.example.user_service.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException; // Import added correctly
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.dto.AddFavoriteGenreRequest;
import com.example.user_service.dto.ArtistResponse;
import com.example.user_service.dto.DeleteFavoriteGenreRequest;
import com.example.user_service.dto.MusicGenreResponse;
import com.example.user_service.dto.SuccessResponse;
import com.example.user_service.dto.UserPreferenceBasicResponse;
import com.example.user_service.dto.UserPreferenceRequest;
import com.example.user_service.dto.UserPreferenceResponse;
import com.example.user_service.mapper.UserPreferenceMapper;
import com.example.user_service.model.FavoriteArtist;
import com.example.user_service.model.FavoriteGenre;
import com.example.user_service.model.MusicGenre;
import com.example.user_service.model.Profile;
import com.example.user_service.model.User; // Import added
import com.example.user_service.model.UserPreference;
import com.example.user_service.repository.FavoriteArtistRepository;
import com.example.user_service.repository.FavoriteGenreRepository;
import com.example.user_service.repository.MusicGenreRepository;
import com.example.user_service.repository.ProfileRepository;
import com.example.user_service.repository.UserPreferenceRepository;
import com.example.user_service.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final ProfileRepository profileRepository;
    private final FavoriteArtistRepository favoriteArtistRepository;
    private final FavoriteGenreRepository favoriteGenreRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final SpotifyService spotifyService;
    private final UserPreferenceMapper mapper;
    private final UserRepository userRepository;

    private static final int MAX_FAVORITE_ARTISTS = 40;
    private static final int MAX_FAVORITE_GENRES = 30;
    private static final BigDecimal DEFAULT_SEARCH_RADIUS = BigDecimal.valueOf(25.0);

    // ===== SEARCH PREFERENCES =====

    @Override
    @Transactional
    public UserPreferenceBasicResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request) {
        log.info("Configuring preferences for user: {}", userId);

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        UserPreference preference = getOrCreateUserPreference(profile);

        if (request.getSearchRadiusKm() != null) {
            preference.setSearchRadius(request.getSearchRadiusKm());
        }

        if (request.getEmailNotifications() != null) {
            preference.setEmailNotifications(request.getEmailNotifications());
        }

        preference = userPreferenceRepository.save(preference);
        log.info("✅ Preferences updated for user {}", userId);

        return mapper.toBasicResponse(preference);
    }

    @Override
    @Transactional(readOnly = true)
    public UserPreferenceResponse getPreferences(Integer userId, boolean includeFullLists) {
        log.info("Getting preferences for user: {} (includeLists: {})", userId, includeFullLists);

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        UserPreference preference = userPreferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreferenceTransactional(profile));

        if (includeFullLists) {
            return buildPreferenceResponse(profile, preference);
        } else {
            return buildPreferenceSummary(profile, preference);
        }
    }

    // ===== FAVORITE ARTISTS =====

    @Override
    @Transactional
    public ArtistResponse addFavoriteArtist(Integer userId, String spotifyId) {
        log.info("➕ Adding favorite artist {} for user {}", spotifyId, userId);

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        long currentCount = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_ARTISTS) {
            throw new IllegalStateException(
                    String.format("You have reached the limit of %d favorite artists", MAX_FAVORITE_ARTISTS));
        }

        // Verify artist exists in Spotify and get details
        ArtistResponse artistResponse = spotifyService.getArtistById(spotifyId);
        if (artistResponse == null) {
            throw new IllegalArgumentException("Artist not found in Spotify with ID: " + spotifyId);
        }

        if (favoriteArtistRepository.existsByProfileProfileIdAndSpotifyId(
                profile.getProfileId(), spotifyId)) {
            throw new IllegalStateException("This artist is already in your favorites");
        }

        FavoriteArtist favoriteArtist = FavoriteArtist.builder()
                .profile(profile)
                .spotifyId(spotifyId)
                .build();

        favoriteArtistRepository.save(favoriteArtist);
        log.info("✅ Artist added to favorites. Total: {}", currentCount + 1);

        return artistResponse;
    }

    @Override
    @Transactional
    public SuccessResponse removeFavoriteArtist(Integer userId, String spotifyId) {
        log.info("➖ Removing favorite artist {} from user {}", spotifyId, userId);

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        FavoriteArtist favoriteArtist = favoriteArtistRepository
                .findByProfileProfileIdAndSpotifyId(profile.getProfileId(), spotifyId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Favorite artist not found with spotifyId: '%s'", spotifyId)));

        // Try to get artist name for response, but don't fail if not possible
        String artistName = "Unknown Artist";
        try {
            ArtistResponse ar = spotifyService.getArtistById(spotifyId);
            if (ar != null)
                artistName = ar.getName();
        } catch (Exception e) {
            log.warn("Could not fetch artist name for removal message: {}", e.getMessage());
        }

        favoriteArtistRepository.deleteByProfileProfileIdAndSpotifyId(
                profile.getProfileId(), spotifyId);

        log.info("✅ Artist removed from favorites");

        return SuccessResponse.of("Artist removed successfully", artistName);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistResponse> getFavoriteArtists(Integer userId, Pageable pageable) {
        log.info("Getting favorite artists for user: {} (page: {}, size: {})",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        Page<FavoriteArtist> favoritesPage = favoriteArtistRepository
                .findByProfileProfileId(profile.getProfileId(), pageable);

        // Enrich with Spotify data
        return favoritesPage.map(fa -> {
            try {
                return spotifyService.getArtistById(fa.getSpotifyId());
            } catch (Exception e) {
                // Fallback if Spotify fails
                return ArtistResponse.builder()
                        .spotifyId(fa.getSpotifyId())
                        .name("Unknown Artist")
                        .build();
            }
        });
    }

    // ===== FAVORITE GENRES =====

    @Override
    @Transactional
    public MusicGenreResponse addFavoriteGenre(Integer userId, AddFavoriteGenreRequest request) {
        log.info("➕ Adding favorite genre for user {}: {}", userId, request);

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        if (!request.isValid()) {
            throw new IllegalArgumentException("Either genreId or genreName must be provided");
        }

        long currentCount = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_GENRES) {
            throw new IllegalStateException(
                    String.format("You have reached the limit of %d favorite genres", MAX_FAVORITE_GENRES));
        }

        MusicGenre genre = findGenreByIdOrName(request);

        if (favoriteGenreRepository.existsByProfileProfileIdAndMusicGenre_MusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId())) {
            throw new IllegalStateException("This genre is already in your favorites");
        }

        FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                .profile(profile)
                .musicGenre(genre)
                .build();

        favoriteGenreRepository.save(favoriteGenre);
        log.info("✅ Genre '{}' added to favorites. Total: {}", genre.getName(), currentCount + 1);

        return mapper.toGenreResponse(genre);
    }

    @Override
    @Transactional
    public SuccessResponse removeFavoriteGenre(Integer userId, DeleteFavoriteGenreRequest request) {
        log.info("➖ Removing favorite genre from user {}: {}", userId, request);

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        if (!request.isValid()) {
            throw new IllegalArgumentException("Either genreId or genreName must be provided");
        }

        MusicGenre genre = findGenreByIdOrName(request);

        FavoriteGenre favoriteGenre = favoriteGenreRepository
                .findByProfileProfileIdAndMusicGenre_MusicGenreId(
                        profile.getProfileId(), genre.getMusicGenreId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Favorite genre not found with %s: '%s'",
                                request.getGenreId() != null ? "genreId" : "genreName",
                                request.getGenreId() != null ? request.getGenreId() : request.getGenreName())));

        String genreName = genre.getName();

        favoriteGenreRepository.deleteByProfileProfileIdAndMusicGenre_MusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId());

        log.info("✅ Genre '{}' removed from favorites", genreName);

        return SuccessResponse.of("Genre removed successfully", genreName);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicGenreResponse> getFavoriteGenres(Integer userId, Pageable pageable) {
        log.info("Getting favorite genres for user: {} (page: {}, size: {})",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        validateUserOwnership(userId);
        Profile profile = getProfileByUserId(userId);

        Page<FavoriteGenre> favoritesPage = favoriteGenreRepository
                .findByProfileProfileId(profile.getProfileId(), pageable);

        return favoritesPage.map(fg -> {
            if (fg.getMusicGenre() != null) {
                return mapper.toGenreResponse(fg.getMusicGenre());
            }
            return MusicGenreResponse.builder()
                    .musicGenreId(null)
                    .name("Unknown Genre")
                    .build();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<MusicGenreResponse> getAllGenres() {
        log.info("Getting all available genres");

        return musicGenreRepository.findAll().stream()
                .map(mapper::toGenreResponse)
                .collect(Collectors.toList());
    }

    // ===== HELPER METHODS =====

    private void validateUserOwnership(Integer targetUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        String authenticatedEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            log.debug("✅ Admin user accessing preferences for user ID: {}", targetUserId);
            // Even admins should get 404 if user doesn't exist, so we check existence
            if (!userRepository.existsById(targetUserId)) {
                throw new EntityNotFoundException(
                        String.format("User not found with id: '%s'", targetUserId));
            }
            return;
        }

        // Fetch target user to verify existence (throws 404 if not found)
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User not found with id: '%s'", targetUserId)));

        // Check ownership
        if (!targetUser.getEmail().equals(authenticatedEmail)) {
            log.warn("❌ Unauthorized access attempt by user: {} for user ID: {}",
                    authenticatedEmail, targetUserId);
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        log.debug("✅ User validated: {} accessing own preferences (ID: {})",
                authenticatedEmail, targetUserId);
    }

    private Profile getProfileByUserId(Integer userId) {
        return profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profile not found with userId: '%s'", userId)));
    }

    private UserPreference getOrCreateUserPreference(Profile profile) {
        return userPreferenceRepository.findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreference(profile));
    }

    private UserPreference createDefaultPreference(Profile profile) {
        log.info("Creating default preferences for profile: {}", profile.getProfileId());

        UserPreference preference = UserPreference.builder()
                .profile(profile)
                .searchRadius(DEFAULT_SEARCH_RADIUS)
                .emailNotifications(true)
                .build();

        return userPreferenceRepository.save(preference);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected UserPreference createDefaultPreferenceTransactional(Profile profile) {
        return createDefaultPreference(profile);
    }

    private UserPreferenceResponse buildPreferenceResponse(Profile profile, UserPreference preference) {
        List<FavoriteArtist> favoriteArtists = favoriteArtistRepository.findByProfileProfileId(profile.getProfileId());
        List<FavoriteGenre> favoriteGenres = favoriteGenreRepository.findByProfileProfileId(profile.getProfileId());

        List<ArtistResponse> artistResponses = favoriteArtists.stream()
                .map(fa -> {
                    try {
                        return spotifyService.getArtistById(fa.getSpotifyId());
                    } catch (Exception e) {
                        return ArtistResponse.builder()
                                .spotifyId(fa.getSpotifyId())
                                .name("Unknown")
                                .build();
                    }
                })
                .collect(Collectors.toList());

        List<MusicGenreResponse> genreResponses = favoriteGenres.stream()
                .map(fg -> {
                    if (fg.getMusicGenre() != null) {
                        return mapper.toGenreResponse(fg.getMusicGenre());
                    }
                    return MusicGenreResponse.builder()
                            .musicGenreId(null)
                            .name("Unknown Genre")
                            .build();
                })
                .collect(Collectors.toList());

        return mapper.toResponse(preference, artistResponses, genreResponses);
    }

    private UserPreferenceResponse buildPreferenceSummary(Profile profile, UserPreference preference) {
        long artistsCount = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        long genresCount = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());

        return UserPreferenceResponse.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(profile.getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .favoriteArtists(Collections.emptyList())
                .favoriteGenres(Collections.emptyList())
                .favoriteArtistsCount((int) artistsCount)
                .favoriteGenresCount((int) genresCount)
                .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
                .maxFavoriteGenres(MAX_FAVORITE_GENRES)
                .build();
    }

    private MusicGenre findGenreByIdOrName(AddFavoriteGenreRequest request) {
        if (request.getGenreId() != null) {
            return musicGenreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with id: '%s'", request.getGenreId())));
        }

        if (request.getGenreName() != null && !request.getGenreName().trim().isEmpty()) {
            String genreName = request.getGenreName().trim();
            return musicGenreRepository.findByNameIgnoreCase(genreName)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with name: '%s'", genreName)));
        }

        throw new IllegalArgumentException("Either genreId or genreName must be provided");
    }

    private MusicGenre findGenreByIdOrName(DeleteFavoriteGenreRequest request) {
        if (request.getGenreId() != null) {
            return musicGenreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with id: '%s'", request.getGenreId())));
        }

        if (request.getGenreName() != null && !request.getGenreName().trim().isEmpty()) {
            String genreName = request.getGenreName().trim();
            return musicGenreRepository.findByNameIgnoreCase(genreName)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with name: '%s'", genreName)));
        }

        throw new IllegalArgumentException("Either genreId or genreName must be provided");
    }
}
