package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ArtistaService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var mapper: Mapper

    fun likeArtista(uid: String, artistId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedArtistas.contains(artistId)) {
            usuario.biblioteca.likedArtistas.add(artistId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    fun unlikeArtista(uid: String, artistId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (usuario.biblioteca.likedArtistas.contains(artistId)) {
            usuario.biblioteca.likedArtistas.remove(artistId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }
}