package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Album
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.service.AlbumService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/albumes")
class AlbumController {

    @Autowired
    private lateinit var albumService: AlbumService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    @GetMapping("/{id}")
    fun obtenerAlbumPorId(
        @PathVariable id: String
    ): ResponseEntity<Album> {
        val album = spotifySearchService.buscarAlbumPorId(id)
        return ResponseEntity.ok(album)
    }

    @PostMapping("/like/{albumId}")
    fun likeAlbum(
        authentication: Authentication,
        @PathVariable albumId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = albumService.likeAlbum(uid, albumId)
        return ResponseEntity.ok(actualizado)
    }

    @DeleteMapping("/like/{albumId}")
    fun unlikeAlbum(
        authentication: Authentication,
        @PathVariable albumId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = albumService.unlikeAlbum(uid, albumId)
        return ResponseEntity.ok(actualizado)
    }
}