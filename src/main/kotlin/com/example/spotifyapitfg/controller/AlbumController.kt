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

/**
 * Controlador REST para manejar operaciones relacionadas con álbumes.
 *
 * @property albumService Servicio para gestionar los "likes" en álbumes.
 * @property spotifySearchService Servicio que interactúa con la API de Spotify para buscar álbumes.
 */
@RestController
@RequestMapping("/albumes")
class AlbumController {

    @Autowired
    private lateinit var albumService: AlbumService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    /**
     * Obtiene los detalles de un álbum por su ID.
     *
     * @param id ID del álbum en Spotify.
     * @return El álbum encontrado envuelto en un [ResponseEntity].
     */
    @GetMapping("/{id}")
    fun obtenerAlbumPorId(
        @PathVariable id: String
    ): ResponseEntity<Album> {
        val album = spotifySearchService.buscarAlbumPorId(id)
        return ResponseEntity.ok(album)
    }

    /**
     * Permite al usuario actual marcar un álbum como favorito.
     *
     * @param authentication Información del usuario autenticado.
     * @param albumId ID del álbum a marcar como favorito.
     * @return Usuario actualizado con el álbum marcado como favorito.
     */
    @PostMapping("/like/{albumId}")
    fun likeAlbum(
        authentication: Authentication,
        @PathVariable albumId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = albumService.likeAlbum(uid, albumId)
        return ResponseEntity.ok(actualizado)
    }

    /**
     * Permite al usuario actual quitar un álbum de sus favoritos.
     *
     * @param authentication Información del usuario autenticado.
     * @param albumId ID del álbum a eliminar de favoritos.
     * @return Usuario actualizado sin el álbum en favoritos.
     */
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