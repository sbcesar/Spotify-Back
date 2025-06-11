package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Artista
import com.example.spotifyapitfg.service.ArtistaService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * Controlador REST para gestionar operaciones relacionadas con artistas.
 *
 * @property artistaService Servicio para gestionar los "likes" en artistas.
 * @property spotifySearchService Servicio que se comunica con Spotify para obtener datos de artistas.
 */
@RestController
@RequestMapping("/artistas")
class ArtistaController {

    @Autowired
    private lateinit var artistaService: ArtistaService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    /**
     * Obtiene los detalles de un artista por su ID.
     *
     * @param id ID del artista en Spotify.
     * @return El artista encontrado envuelto en un [ResponseEntity].
     */
    @GetMapping("/{id}")
    fun obtenerArtistaPorId(
        @PathVariable id: String
    ): ResponseEntity<Artista> {
        val artista = spotifySearchService.buscarArtistaPorId(id)
        return ResponseEntity.ok(artista)
    }

    /**
     * Permite al usuario marcar un artista como favorito.
     *
     * @param authentication Información del usuario autenticado.
     * @param artistaId ID del artista a marcar como favorito.
     * @return Usuario actualizado con el artista marcado como favorito.
     */
    @PostMapping("/like/{artistaId}")
    fun likeArtista(
        authentication: Authentication,
        @PathVariable artistaId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = artistaService.likeArtista(uid, artistaId)
        return ResponseEntity.ok(actualizado)
    }

    /**
     * Permite al usuario quitar un artista de sus favoritos.
     *
     * @param authentication Información del usuario autenticado.
     * @param artistaId ID del artista a eliminar de favoritos.
     * @return Usuario actualizado sin el artista en favoritos.
     */
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