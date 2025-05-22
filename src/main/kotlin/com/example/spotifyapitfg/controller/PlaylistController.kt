package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Playlist
import com.example.spotifyapitfg.service.PlaylistService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/playlists")
class PlaylistController {

    @Autowired
    private lateinit var playlistService: PlaylistService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    @GetMapping("/{id}")
    fun obtenerPlaylistPorId(
        @PathVariable id: String
    ): ResponseEntity<Playlist> {
        val playlist = spotifySearchService.buscarPlaylistPorId(id)
        return ResponseEntity.ok(playlist)
    }

    @PostMapping("/like/{playlistId}")
    fun likePlaylist(
        authentication: Authentication,
        @PathVariable playlistId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = playlistService.likePlaylist(uid, playlistId)
        return ResponseEntity.ok(actualizado)
    }

    @DeleteMapping("/like/{playlistId}")
    fun unlikePlaylist(
        authentication: Authentication,
        @PathVariable playlistId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = playlistService.likePlaylist(uid, playlistId)
        return ResponseEntity.ok(actualizado)
    }
}