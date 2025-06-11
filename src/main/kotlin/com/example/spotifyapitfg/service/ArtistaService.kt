package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los artistas favoritos
 * de los usuarios, como dar "like" o quitarlo.
 *
 * @property usuarioRepository Repositorio para acceder y actualizar los datos de usuario.
 * @property mapper Mapeador que convierte entidades de usuario a [UsuarioDTO].
 */
@Service
class ArtistaService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var mapper: Mapper

    /**
     * Añade un artista a la lista de favoritos del usuario, si aún no lo ha marcado como favorito.
     *
     * @param uid ID del usuario autenticado.
     * @param artistId ID del artista que se quiere marcar como favorito.
     * @return [UsuarioDTO] con los datos actualizados del usuario.
     * @throws NotFoundException si el usuario no existe.
     */
    fun likeArtista(uid: String, artistId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedArtistas.contains(artistId)) {
            usuario.biblioteca.likedArtistas.add(artistId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    /**
     * Quita un artista de la lista de favoritos del usuario, si está presente.
     *
     * @param uid ID del usuario autenticado.
     * @param artistId ID del artista que se quiere eliminar de favoritos.
     * @return [UsuarioDTO] con los datos actualizados del usuario.
     * @throws NotFoundException si el usuario no existe.
     */
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