package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Biblioteca
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuario")
class UsuarioController {

    @Autowired
    private lateinit var usuarioService: UsuarioService

    @GetMapping("/perfil")
    fun getUserProfile(
        @RequestHeader("Authorization") bearerToken: String
    ) : ResponseEntity<Usuario> {
        val usuario = usuarioService.getUsuarioFromToken(bearerToken)
        return ResponseEntity.ok(usuario)
    }

    // Muestra toda la biblioteca (liked songs, playlist, artist)
    @GetMapping("/biblioteca")
    fun getUserBiblioteca(
        @RequestHeader("Authorization") bearerToken: String
    ) : ResponseEntity<Biblioteca> {
        val usuario = usuarioService.getUsuarioFromToken(bearerToken)
        // Hacer

        return ResponseEntity(null, HttpStatus.OK)
    }

    @PutMapping("/actualizar")
    fun updateUsername(
        @RequestHeader("Authorization") bearerToken: String,
        @RequestParam newUsername: String
    ) : ResponseEntity<Usuario> {
        val usuario = usuarioService.getUsuarioFromToken(bearerToken)
        // Hacer

        return ResponseEntity(usuario, HttpStatus.OK)
    }

    // Devuelve una lista de los nombres de las canciones que le has dado like
    @GetMapping("/liked-songs")
    fun getLikedSongs(
        @RequestHeader("Authorization") bearerToken: String
    ) : ResponseEntity<List<String>> {
        // Hacer

        return ResponseEntity(listOf(), HttpStatus.OK)
    }

    // El cliente solo necesita saber la confirmacion de la accion, ya sabe a que cancion le dio like
    @PostMapping("/liked-songs/agregar/{cancionId}")
    fun addLikedSong(
        authentication: Authentication,
        @PathVariable cancionId: String
    ) : ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val usuarioDTOActualizado = usuarioService.addSongToFavs(uid, cancionId)

        return ResponseEntity.ok(usuarioDTOActualizado)
    }


    @DeleteMapping("/liked-songs/quitar/{cancionId}")
    fun removeLikedSong(
        @RequestHeader("Authorization") bearerToken: String,
        @PathVariable cancionId: String
    ) : ResponseEntity<Void> {
        // Hacer

        return ResponseEntity.noContent().build()
    }

}