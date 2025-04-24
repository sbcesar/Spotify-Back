package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UsuarioService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var authService: FirebaseAuthService

    fun getUsuarioFromToken(idToken: String): Usuario {
        val uid = authService.getUidFromToken(idToken.removePrefix("Bearer ").trim())
        return usuarioRepository.findById(uid).orElseThrow( /* Error 404 Not Found */ )
    }
}