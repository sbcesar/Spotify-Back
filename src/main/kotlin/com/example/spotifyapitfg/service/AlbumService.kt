package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AlbumService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var mapper: Mapper

    fun likeAlbum(uid: String, albumId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedAlbums.contains(albumId)) {
            usuario.biblioteca.likedAlbums.add(albumId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    fun unlikeAlbum(uid: String, albumId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (usuario.biblioteca.likedAlbums.contains(albumId)) {
            usuario.biblioteca.likedAlbums.remove(albumId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }
}