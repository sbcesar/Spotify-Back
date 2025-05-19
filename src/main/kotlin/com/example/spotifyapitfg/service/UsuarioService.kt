package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.dto.UsuarioLoginDTO
import com.example.spotifyapitfg.error.exception.ConflictException
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.UsuarioMapper
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException

import org.springframework.stereotype.Service

@Service
class UsuarioService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var firebaseAuthService: FirebaseAuthService

    @Autowired
    private lateinit var usuarioMapper: UsuarioMapper

    fun registrarUsuario(usuario: Usuario): UsuarioDTO {
        if (usuarioRepository.existsById(usuario.id!!)) throw ConflictException("El usuario ya existe")

        val usuarioGuardado = usuarioRepository.save(usuario)

        return usuarioMapper.toDTO(usuarioGuardado)
    }

    fun login(idToken: String): UsuarioDTO {
        try {
            val uid = firebaseAuthService.login(idToken)

            val usuario = usuarioRepository.findById(uid).orElseThrow { UsernameNotFoundException("Usuario no encontrado en MongoDB") }

            return usuarioMapper.toDTO(usuario)
        } catch (e: Exception) {
            throw UsernameNotFoundException("No se pudo validar el token de Firebase: ${e.message}")
        }
    }

    fun addSongToFavs(uid: String, cancionId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("User not found") }

        if (!usuario.biblioteca.likedCanciones.contains(cancionId)) {
            usuario.biblioteca.likedCanciones.add(cancionId)
            usuarioRepository.save(usuario)
        }

        return usuarioMapper.toDTO(usuario)
    }
}