package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.repository.CancionRepository
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio encargado de gestionar operaciones relacionadas con las canciones,
 * tanto para obtenerlas como para gestionar los favoritos de los usuarios.
 *
 * @property usuarioRepository Repositorio de usuarios para acceder y modificar la biblioteca.
 * @property cancionRepository Repositorio de canciones almacenadas en la base de datos.
 * @property mapper Mapeador que convierte entidades de usuario a [UsuarioDTO].
 */
@Service
class CancionService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var cancionRepository: CancionRepository

    @Autowired
    private lateinit var mapper: Mapper

    /**
     * Obtiene todas las canciones disponibles en la base de datos.
     *
     * @return Lista de objetos [Cancion].
     */
    fun obtenerCanciones(): List<Cancion> {
        return cancionRepository.findAll()
    }

    /**
     * Añade una canción a la lista de favoritas del usuario, si aún no está marcada.
     *
     * @param uid ID del usuario autenticado.
     * @param cancionId ID de la canción que se desea marcar como favorita.
     * @return [UsuarioDTO] con los datos actualizados del usuario.
     * @throws NotFoundException si el usuario no existe.
     */
    fun likeCancion(uid: String, cancionId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedCanciones.contains(cancionId)) {
            usuario.biblioteca.likedCanciones.add(cancionId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    /**
     * Elimina una canción de la lista de favoritas del usuario, si está presente.
     *
     * @param uid ID del usuario autenticado.
     * @param cancionId ID de la canción que se desea eliminar de favoritos.
     * @return [UsuarioDTO] con los datos actualizados del usuario.
     * @throws NotFoundException si el usuario no existe.
     */
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