package com.example.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.model.FavoriteArtist;

@Repository
public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Integer> {

    List<FavoriteArtist> findByProfileProfileId(Integer profileId);

    Page<FavoriteArtist> findByProfileProfileId(Integer profileId, Pageable pageable);

    Optional<FavoriteArtist> findByProfileProfileIdAndSpotifyId(Integer profileId, String spotifyId);

    boolean existsByProfileProfileIdAndSpotifyId(Integer profileId, String spotifyId);

    void deleteByProfileProfileIdAndSpotifyId(Integer profileId, String spotifyId);

    long countByProfileProfileId(Integer profileId);
}
