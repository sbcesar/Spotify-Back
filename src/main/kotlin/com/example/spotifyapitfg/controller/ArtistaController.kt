package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Artista
import com.example.spotifyapitfg.service.ArtistaService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/artistas")
class ArtistaController {

    @Autowired
    private lateinit var artistaService: ArtistaService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    @GetMapping("/{id}")
    fun obtenerArtistaPorId(
        @PathVariable id: String
    ): ResponseEntity<Artista> {
        val artista = spotifySearchService.buscarArtistaPorId(id)
        return ResponseEntity.ok(artista)
    }

    @PostMapping("/like/{artistaId}")
    fun likeArtista(
        authentication: Authentication,
        @PathVariable artistaId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = artistaService.likeArtista(uid, artistaId)
        return ResponseEntity.ok(actualizado)
    }

    @DeleteMapping("/like/{artistaId}")
    fun unlikeArtista(
        authentication: Authentication,
        @PathVariable artistaId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = artistaService.unlikeArtista(uid, artistaId)
        return ResponseEntity.ok(actualizado)
    }
}