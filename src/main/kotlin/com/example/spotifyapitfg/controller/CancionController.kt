package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.service.CancionService
import com.example.spotifyapitfg.service.SpotifySearchService
import com.example.spotifyapitfg.models.Cancion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/canciones")
class CancionController {

    @Autowired
    private lateinit var cancionService: CancionService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    @GetMapping("/{id}")
    fun obtenerCancionPorId(
        @PathVariable id: String
    ): ResponseEntity<Cancion> {
        val cancion = spotifySearchService.buscarCancionPorId(id)
        return ResponseEntity.ok(cancion)
    }

    @GetMapping("/all")
    fun getCanciones(): ResponseEntity<List<Cancion>> {
        val canciones = cancionService.obtenerCanciones()

        return ResponseEntity.ok(canciones)
    }

    @PostMapping("/like/{cancionId}")
    fun likeCancion(
        authentication: Authentication,
        @PathVariable cancionId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = cancionService.likeCancion(uid, cancionId)
        return ResponseEntity.ok(actualizado)
    }

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