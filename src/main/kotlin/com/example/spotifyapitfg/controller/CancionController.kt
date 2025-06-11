package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.service.CancionService
import com.example.spotifyapitfg.service.SpotifySearchService
import com.example.spotifyapitfg.models.Cancion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * Controlador REST para gestionar operaciones relacionadas con canciones.
 *
 * @property cancionService Servicio para manejar las canciones del sistema.
 * @property spotifySearchService Servicio para buscar canciones en Spotify.
 */
@RestController
@RequestMapping("/canciones")
class CancionController {

    @Autowired
    private lateinit var cancionService: CancionService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    /**
     * Obtiene los detalles de una canción por su ID.
     *
     * @param id ID de la canción en Spotify.
     * @return La canción encontrada envuelta en un [ResponseEntity].
     */
    @GetMapping("/{id}")
    fun obtenerCancionPorId(
        @PathVariable id: String
    ): ResponseEntity<Cancion> {
        val cancion = spotifySearchService.buscarCancionPorId(id)
        return ResponseEntity.ok(cancion)
    }

    /**
     * Devuelve una lista de todas las canciones almacenadas.
     *
     * @return Lista de canciones.
     */
    @GetMapping("/all")
    fun getCanciones(): ResponseEntity<List<Cancion>> {
        val canciones = cancionService.obtenerCanciones()

        return ResponseEntity.ok(canciones)
    }

    /**
     * Permite al usuario marcar una canción como favorita.
     *
     * @param authentication Información del usuario autenticado.
     * @param cancionId ID de la canción a marcar como favorita.
     * @return Usuario actualizado con la canción en su lista de favoritos.
     */
    @PostMapping("/like/{cancionId}")
    fun likeCancion(
        authentication: Authentication,
        @PathVariable cancionId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = cancionService.likeCancion(uid, cancionId)
        return ResponseEntity.ok(actualizado)
    }

    /**
     * Permite al usuario quitar una canción de sus favoritos.
     *
     * @param authentication Información del usuario autenticado.
     * @param cancionId ID de la canción a quitar de favoritos.
     * @return Usuario actualizado sin la canción en favoritos.
     */
    @DeleteMapping("/like/{cancionId}")
    fun unlikeCancion(
        authentication: Authentication,
        @PathVariable cancionId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = cancionService.unlikeCancion(uid, cancionId)
        return ResponseEntity.ok(actualizado)
    }
}