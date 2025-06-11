package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los álbumes favoritos
 * de los usuarios, como marcar o desmarcar un álbum como "liked".
 *
 * @property usuarioRepository Repositorio de usuarios para acceder y modificar sus datos.
 * @property mapper Mapeador para convertir entidades de usuario a DTOs.
 */
@Service
class AlbumService(
    private val usuarioRepository: UsuarioRepository,
    private val mapper: Mapper
) {

//    @Autowired
//    private lateinit var usuarioRepository: UsuarioRepository
//
//    @Autowired
//    private lateinit var mapper: Mapper

    /**
     * Añade un álbum a la lista de favoritos del usuario, si aún no existe.
     *
     * @param uid ID del usuario autenticado.
     * @param albumId ID del álbum que se quiere marcar como favorito.
     * @return [UsuarioDTO] con la información actualizada del usuario.
     * @throws NotFoundException si el usuario no existe.
     */
    fun likeAlbum(uid: String, albumId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedAlbums.contains(albumId)) {
            usuario.biblioteca.likedAlbums.add(albumId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    /**
     * Elimina un álbum de la lista de favoritos del usuario, si existe.
     *
     * @param uid ID del usuario autenticado.
     * @param albumId ID del álbum que se quiere quitar de favoritos.
     * @return [UsuarioDTO] con la información actualizada del usuario.
     * @throws NotFoundException si el usuario no existe.
     */
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