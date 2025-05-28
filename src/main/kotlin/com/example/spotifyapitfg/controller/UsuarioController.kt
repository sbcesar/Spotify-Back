package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioBibliotecaMostrableDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.dto.UsuarioRegisterDTO
import com.example.spotifyapitfg.models.Biblioteca
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.service.FirebaseAuthService
import com.example.spotifyapitfg.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuario")
class UsuarioController {

    @Autowired
    private lateinit var firebaseAuthService: FirebaseAuthService

    @Autowired
    private lateinit var usuarioService: UsuarioService

    @GetMapping("/perfil")
    fun getUserProfile(
        authentication: Authentication
    ) : ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val usuario = usuarioService.obtenerUsuarioPorId(uid)

        return ResponseEntity.ok(usuario)
    }

    @GetMapping("/biblioteca")
    fun obtenerBibliotecaCompleta(
        authentication: Authentication
    ): ResponseEntity<UsuarioBibliotecaMostrableDTO> {
        val uid = authentication.name
        val usuario = usuarioService.obtenerUsuarioMostrable(uid)

        return ResponseEntity.ok(usuario)
    }

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


}