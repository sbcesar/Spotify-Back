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

@RestController
@RequestMapping("/spotify")
class SpotifyController {

    @Autowired
    private lateinit var spotifyAuthService: SpotifyAuthService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    @GetMapping("/token")
    fun obtenerToken(): ResponseEntity<String> {
        val token = spotifyAuthService.obtenerTokenDeAcceso()
        return ResponseEntity.ok(token)
    }

    @GetMapping("/buscar/canciones")
    fun buscarCancion(@RequestParam query: String): ResponseEntity<List<Cancion>> {
        val resultados = spotifySearchService.buscarCanciones(query)

        return ResponseEntity.ok(resultados)
    }

    @GetMapping("/buscar/albumes")
    fun buscarAlbumes(@RequestParam query: String): ResponseEntity<List<Album>> {
        val albumes = spotifySearchService.buscarAlbumes(query)
        return ResponseEntity.ok(albumes)
    }

    @GetMapping("/buscar/artistas")
    fun buscarArtistas(@RequestParam query: String): ResponseEntity<List<Artista>> {
        val artistas = spotifySearchService.buscarArtistas(query)
        return ResponseEntity.ok(artistas)
    }

    @GetMapping("/buscar/playlists")
    fun buscarPlaylists(@RequestParam query: String): ResponseEntity<List<Playlist>> {
        val resultados = spotifySearchService.buscarPlaylists(query)
        return ResponseEntity.ok(resultados)
    }
}