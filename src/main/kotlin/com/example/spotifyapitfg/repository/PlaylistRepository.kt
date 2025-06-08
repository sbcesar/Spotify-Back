package com.example.spotifyapitfg.repository

import com.example.spotifyapitfg.models.Playlist
import org.springframework.data.mongodb.repository.MongoRepository

interface PlaylistRepository : MongoRepository<Playlist, String> {

    fun findByCreadorId(creadorId: String): List<Playlist>
}