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

/**
 * Controlador REST que gestiona acciones relacionadas con el usuario como registro, login, perfil y biblioteca.
 *
 * @property firebaseAuthService Servicio que maneja la autenticación con Firebase.
 * @property usuarioService Servicio para gestionar los datos del usuario.
 */
@RestController
@RequestMapping("/usuario")
class UsuarioController {

    @Autowired
    private lateinit var firebaseAuthService: FirebaseAuthService

    @Autowired
    private lateinit var usuarioService: UsuarioService

    /**
     * Devuelve el perfil del usuario autenticado.
     *
     * @param authentication Información del usuario autenticado.
     * @return [ResponseEntity] que contiene un [UsuarioDTO] con los datos del perfil.
     */
    @GetMapping("/perfil")
    fun getUserProfile(
        authentication: Authentication
    ) : ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val usuario = usuarioService.obtenerUsuarioPorId(uid)

        return ResponseEntity.ok(usuario)
    }

    /**
     * Devuelve la biblioteca completa del usuario autenticado, incluyendo sus artistas, álbumes, canciones y playlists favoritas.
     *
     * @param authentication Información del usuario autenticado.
     * @return [ResponseEntity] que contiene un [UsuarioBibliotecaMostrableDTO] con la biblioteca del usuario.
     */
    @GetMapping("/biblioteca")
    fun obtenerBibliotecaCompleta(
        authentication: Authentication
    ): ResponseEntity<UsuarioBibliotecaMostrableDTO> {
        val uid = authentication.name
        val usuario = usuarioService.obtenerUsuarioMostrable(uid)

        return ResponseEntity.ok(usuario)
    }

    /**
     * Registra un nuevo usuario en la aplicación utilizando correo electrónico, contraseña y nombre.
     *
     * @param usuarioRegisterDTO Objeto con los datos del nuevo usuario: email, password y nombre.
     * @return [ResponseEntity] que contiene un [UsuarioDTO] del usuario registrado.
     */
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

    /**
     * Inicia sesión de un usuario a través de un token JWT proporcionado en la cabecera Authorization.
     *
     * @param bearerToken Token JWT precedido por la palabra "Bearer ".
     * @return [ResponseEntity] que contiene un [UsuarioDTO] con la información del usuario autenticado.
     */
    @PostMapping("/login")
    fun loginUsuario(
        @RequestHeader("Authorization") bearerToken: String,
    ) : ResponseEntity<UsuarioDTO> {
        val token = bearerToken.removePrefix("Bearer ").trim()
        val usuarioLogueado = usuarioService.login(token)
        return ResponseEntity.ok(usuarioLogueado)
    }


}