package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

}