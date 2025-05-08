package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.UsuarioMapper
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.stereotype.Service

@Service
class UsuarioService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var usuarioMapper: UsuarioMapper

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