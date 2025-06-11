package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.models.Album
import com.example.spotifyapitfg.models.Artista
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.models.Playlist
import com.example.spotifyapitfg.service.SpotifyAuthService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador REST para manejar la autenticación con Spotify y realizar búsquedas de contenido.
 *
 * @property spotifyAuthService Servicio para obtener tokens de acceso de Spotify.
 * @property spotifySearchService Servicio para realizar búsquedas en la API de Spotify.
 */
@RestController
@RequestMapping("/spotify")
class SpotifyController {

    @Autowired
    private lateinit var spotifyAuthService: SpotifyAuthService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    /**
     * Obtiene un token de acceso válido desde Spotify utilizando el flujo de autenticación del servidor.
     *
     * @return [ResponseEntity] con el token JWT proporcionado por Spotify.
     */
    @GetMapping("/token")
    fun obtenerToken(): ResponseEntity<String> {
        val token = spotifyAuthService.obtenerTokenDeAcceso()
        return ResponseEntity.ok(token)
    }

    /**
     * Busca canciones en Spotify que coincidan con el término de búsqueda dado.
     *
     * @param query Término de búsqueda introducido por el usuario.
     * @return [ResponseEntity] con una lista de canciones que coinciden.
     */
    @GetMapping("/buscar/canciones")
    fun buscarCancion(@RequestParam query: String): ResponseEntity<List<Cancion>> {
        val resultados = spotifySearchService.buscarCanciones(query)

        return ResponseEntity.ok(resultados)
    }

    /**
     * Busca álbumes en Spotify que coincidan con el término de búsqueda dado.
     *
     * @param query Término de búsqueda introducido por el usuario.
     * @return [ResponseEntity] con una lista de álbumes que coinciden.
     */
    @GetMapping("/buscar/albumes")
    fun buscarAlbumes(@RequestParam query: String): ResponseEntity<List<Album>> {
        val albumes = spotifySearchService.buscarAlbumes(query)
        return ResponseEntity.ok(albumes)
    }

    /**
     * Busca artistas en Spotify que coincidan con el término de búsqueda dado.
     *
     * @param query Término de búsqueda introducido por el usuario.
     * @return [ResponseEntity] con una lista de artistas que coinciden.
     */
    @GetMapping("/buscar/artistas")
    fun buscarArtistas(@RequestParam query: String): ResponseEntity<List<Artista>> {
        val artistas = spotifySearchService.buscarArtistas(query)
        return ResponseEntity.ok(artistas)
    }

    /**
     * Busca playlists públicas en Spotify que coincidan con el término de búsqueda dado.
     *
     * @param query Término de búsqueda introducido por el usuario.
     * @return [ResponseEntity] con una lista de playlists que coinciden.
     */
    @GetMapping("/buscar/playlists")
    fun buscarPlaylists(@RequestParam query: String): ResponseEntity<List<Playlist>> {
        val resultados = spotifySearchService.buscarPlaylists(query)
        return ResponseEntity.ok(resultados)
    }
}