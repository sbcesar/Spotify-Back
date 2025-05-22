package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.dto.UsuarioRegisterDTO
import com.example.spotifyapitfg.mapper.UsuarioMapper
import com.example.spotifyapitfg.models.Biblioteca
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.service.FirebaseAuthService
import com.example.spotifyapitfg.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuario")
class UsuarioController {

    @Autowired
    private lateinit var firebaseAuthService: FirebaseAuthService

    @Autowired
    private lateinit var usuarioMapper: UsuarioMapper

    @Autowired
    private lateinit var usuarioService: UsuarioService

//    @GetMapping("/perfil")
//    fun getUserProfile(
//        authentication: Authentication
//    ) : ResponseEntity<Usuario> {
//        val usuario = usuarioService.getUsuarioFromToken(bearerToken)
//        return ResponseEntity.ok(usuario)
//    }

    @PostMapping("/register")
    fun registrarUsuario(
        @RequestBody usuarioRegisterDTO: UsuarioRegisterDTO
    ): ResponseEntity<UsuarioDTO> {
        val uid = firebaseAuthService.registrarUsuario(usuarioRegisterDTO.email, usuarioRegisterDTO.password)

        val usuario = Usuario(
            id = uid,
            nombre = usuarioRegisterDTO.nombre,
            email = usuarioRegisterDTO.email,
            biblioteca = Biblioteca()
        )

        val usuarioRegistrado = usuarioService.registrarUsuario(usuario)

        return ResponseEntity.ok(usuarioRegistrado)
    }

    @PostMapping("/login")
    fun loginUsuario(
        @RequestHeader("Authorization") bearerToken: String,
    ) : ResponseEntity<UsuarioDTO> {
        val token = bearerToken.removePrefix("Bearer ").trim()
        val usuarioLogueado = usuarioService.login(token)
        return ResponseEntity.ok(usuarioLogueado)
    }

    // Muestra toda la biblioteca (liked songs, playlist, artist)
//    @GetMapping("/biblioteca")
//    fun getUserBiblioteca(
//        authentication: Authentication
//    ) : ResponseEntity<Biblioteca> {
//        val usuario = usuarioService.getUsuarioFromToken(bearerToken)
//        // Hacer
//
//        return ResponseEntity(null, HttpStatus.OK)
//    }

//    @PutMapping("/actualizar")
//    fun updateUsername(
//        authentication: Authentication,
//        @RequestParam newUsername: String
//    ) : ResponseEntity<Usuario> {
//        val usuario = usuarioService.getUsuarioFromToken(bearerToken)
//        // Hacer
//
//        return ResponseEntity(usuario, HttpStatus.OK)
//    }

    // Devuelve una lista de los nombres de las canciones que le has dado like
//    @GetMapping("/liked-songs")
//    fun getLikedSongs(
//        authentication: Authentication
//    ) : ResponseEntity<List<String>> {
//        // Hacer
//
//        return ResponseEntity(listOf(), HttpStatus.OK)
//    }

    // El cliente solo necesita saber la confirmacion de la accion, ya sabe a que cancion le dio like
//    @PostMapping("/liked-songs/agregar/{cancionId}")
//    fun addLikedSong(
//        authentication: Authentication,
//        @PathVariable cancionId: String
//    ) : ResponseEntity<UsuarioDTO> {
//        val uid = authentication.name
//        val usuarioDTOActualizado = usuarioService.addSongToFavs(uid, cancionId)
//
//        return ResponseEntity.ok(usuarioDTOActualizado)
//    }


//    @DeleteMapping("/liked-songs/quitar/{cancionId}")
//    fun removeLikedSong(
//        authentication: Authentication,
//        @PathVariable cancionId: String
//    ) : ResponseEntity<Void> {
//        // Hacer
//
//        return ResponseEntity.noContent().build()
//    }

}