package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.repository.CancionRepository
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CancionService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var cancionRepository: CancionRepository

    @Autowired
    private lateinit var mapper: Mapper

    fun obtenerCanciones(): List<Cancion> {
        return cancionRepository.findAll()
    }

    fun likeCancion(uid: String, cancionId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedCanciones.contains(cancionId)) {
            usuario.biblioteca.likedCanciones.add(cancionId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    fun unlikeCancion(uid: String, cancionId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (usuario.biblioteca.likedCanciones.contains(cancionId)) {
            usuario.biblioteca.likedCanciones.remove(cancionId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }
}