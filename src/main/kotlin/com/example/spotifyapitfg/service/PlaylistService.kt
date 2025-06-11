package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.PlaylistCreateDTO
import com.example.spotifyapitfg.dto.PlaylistDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.ForbiddenException
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.models.Playlist
import com.example.spotifyapitfg.repository.PlaylistRepository
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.http.*
import java.util.*

/**
 * Servicio encargado de gestionar las operaciones relacionadas con las playlists de los usuarios,
 * como creación, modificación, mezcla, favoritos y administración de canciones.
 *
 * @property usuarioRepository Repositorio de usuarios.
 * @property spotifySearchService Servicio para buscar recursos en Spotify.
 * @property playlistRepository Repositorio de playlists propias de la aplicación.
 * @property firebaseAuthService Servicio para autenticación y autorización basada en Firebase.
 * @property authService Servicio para obtener el token de acceso de Spotify.
 * @property mapper Mapeador de entidades a DTOs.
 */
@Service
class PlaylistService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    @Autowired
    private lateinit var playlistRepository: PlaylistRepository

    @Autowired
    private lateinit var firebaseAuthService: FirebaseAuthService

    @Autowired
    private lateinit var authService: SpotifyAuthService

    @Autowired
    private lateinit var mapper: Mapper

    /**
     * Obtiene todas las playlists creadas en la aplicación.
     *
     * @return Lista de [PlaylistDTO] con información básica de cada playlist.
     */
    fun obtenerTodas(): List<PlaylistDTO> {
        val playlists = playlistRepository.findAll()
        return playlists.map { mapper.toDTO(it) }
    }

    /**
     * Obtiene las playlists creadas por un usuario específico.
     *
     * @param uid ID del usuario.
     * @return Lista de [PlaylistDTO] creadas por el usuario.
     */
    fun obtenerPlaylistsCreadasPorUsuario(uid: String): List<PlaylistDTO> {
        val playlists = playlistRepository.findByCreadorId(uid)
        return playlists.map { mapper.toDTO(it) }
    }

    /**
     * Crea una nueva playlist asociada a un usuario.
     *
     * @param uid ID del usuario creador.
     * @param playlistCreateDTO Datos necesarios para crear la playlist.
     * @return [PlaylistDTO] con la playlist recién creada.
     */
    fun crearPlaylist(uid: String, playlistCreateDTO: PlaylistCreateDTO): PlaylistDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        val playlist = Playlist(
            id = UUID.randomUUID().toString(),
            nombre = playlistCreateDTO.nombre,
            descripcion = playlistCreateDTO.descripcion,
            canciones = emptyList(),
            creadorId = uid,
            creadorNombre = usuario.nombre,
            imagenUrl = playlistCreateDTO.imagenUrl
        )

        playlistRepository.save(playlist)

        usuario.biblioteca.playlistsCreadas.add(playlist.id!!)
        usuarioRepository.save(usuario)

        return mapper.toDTO(playlist)
    }

    /**
     * Mezcla dos playlists (locales o liked de Spotify) y crea una nueva playlist combinada.
     * Se toman hasta 20 canciones únicas aleatorias de ambas.
     *
     * @param id1 ID de la primera playlist.
     * @param id2 ID de la segunda playlist.
     * @param creadorUid ID del usuario que solicita la mezcla.
     * @return [PlaylistDTO] resultante de la mezcla.
     */
    fun mezclarPlaylists(id1: String, id2: String, creadorUid: String): PlaylistDTO {
        val usuario = usuarioRepository.findById(creadorUid)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        val token = authService.obtenerTokenDeAcceso()
        val headers = HttpHeaders().apply { setBearerAuth(token) }

        // Buscar playlist 1
        val playlist1: Playlist = playlistRepository.findById(id1).orElse(null)
            ?: if (usuario.biblioteca.likedPlaylists.contains(id1)) {
                spotifySearchService.buscarPlaylistSpotifyComoLocal(id1, headers)
            } else null
                ?: throw RuntimeException("Playlist 1 no encontrada o no permitida")

        // Buscar playlist 2
        val playlist2: Playlist = playlistRepository.findById(id2).orElse(null)
            ?: if (usuario.biblioteca.likedPlaylists.contains(id2)) {
                spotifySearchService.buscarPlaylistSpotifyComoLocal(id2, headers)
            } else null
                ?: throw RuntimeException("Playlist 2 no encontrada o no permitida")

        // Mezclar canciones (máximo 20)
        val cancionesTotales = (playlist1.canciones + playlist2.canciones)
            .distinctBy { it.id }
            .shuffled()
            .take(20)

        // Crear nueva playlist
        val mixedPlaylist = Playlist(
            id = UUID.randomUUID().toString(),
            nombre = "Mezcla de ${playlist1.nombre} y ${playlist2.nombre}",
            descripcion = "Generada automáticamente a partir de 2 playlists.",
            canciones = cancionesTotales.toMutableList(),
            creadorId = creadorUid,
            creadorNombre = usuario.nombre,
            imagenUrl = playlist1.imagenUrl ?: playlist2.imagenUrl ?: ""
        )

        // Guardar nueva playlist
        playlistRepository.save(mixedPlaylist)

        // Asociar al usuario como creador
        usuario.biblioteca.playlistsCreadas.add(mixedPlaylist.id!!)
        usuarioRepository.save(usuario)

        return mapper.toDTO(mixedPlaylist)
    }

    /**
     * Modifica los datos básicos (nombre, descripción, imagen) de una playlist existente.
     * Solo el creador o un admin puede editarla.
     *
     * @param uid ID del usuario que intenta editar.
     * @param id ID de la playlist a modificar.
     * @param dto Datos nuevos de la playlist.
     * @return [PlaylistDTO] con la información actualizada.
     * @throws ForbiddenException si el usuario no tiene permisos.
     */
    fun modificarPlaylist(uid: String, id: String, dto: PlaylistCreateDTO): PlaylistDTO {
        val playlist = playlistRepository.findById(id)
            .orElseThrow { NotFoundException("Playlist no encontrada") }

        if (playlist.creadorId != uid && !firebaseAuthService.usuarioEsAdmin(uid)) {
            throw ForbiddenException("No tienes permiso para modificar esta playlist")
        }

        val actualizada = playlist.copy(
            nombre = dto.nombre,
            descripcion = dto.descripcion,
            imagenUrl = dto.imagenUrl ?: ""
        )

        playlistRepository.save(actualizada)
        return mapper.toDTO(actualizada)
    }

    /**
     * Elimina una playlist si el usuario es el creador o administrador.
     *
     * @param uid ID del usuario solicitante.
     * @param id ID de la playlist a eliminar.
     * @throws ForbiddenException si el usuario no tiene permisos.
     */
    fun eliminarPlaylist(uid: String, id: String) {
        val playlist = playlistRepository.findById(id)
            .orElseThrow { NotFoundException("Playlist no encontrada") }

        if (playlist.creadorId != uid && !firebaseAuthService.usuarioEsAdmin(uid)) {
            throw ForbiddenException("No tienes permiso para eliminar esta playlist")
        }

        val usuario = usuarioRepository.findById(uid).orElseThrow()
        usuario.biblioteca.playlistsCreadas.remove(id)
        usuarioRepository.save(usuario)

        playlistRepository.deleteById(id)
    }

    /**
     * Marca una playlist como favorita para el usuario.
     *
     * @param uid ID del usuario autenticado.
     * @param playlistId ID de la playlist a marcar.
     * @return [UsuarioDTO] con la información del usuario actualizada.
     */
    fun likePlaylist(uid: String, playlistId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedPlaylists.contains(playlistId)) {
            usuario.biblioteca.likedPlaylists.add(playlistId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    /**
     * Elimina una playlist de la lista de favoritas del usuario.
     *
     * @param uid ID del usuario autenticado.
     * @param playlistId ID de la playlist a eliminar de favoritos.
     * @return [UsuarioDTO] actualizado.
     */
    fun unlikePlaylist(uid: String, playlistId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (usuario.biblioteca.likedPlaylists.contains(playlistId)) {
            usuario.biblioteca.likedPlaylists.remove(playlistId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    /**
     * Agrega una canción a una playlist creada por el usuario.
     *
     * @param playlistId ID de la playlist.
     * @param cancionId ID de la canción que se desea agregar.
     * @param uid ID del usuario autenticado.
     * @return [PlaylistDTO] con la playlist actualizada.
     * @throws ForbiddenException si el usuario no es el creador.
     * @throws IllegalStateException si la canción ya está en la playlist.
     */
    fun agregarCancion(playlistId: String, cancionId: String, uid: String): PlaylistDTO {
        val playlist = playlistRepository.findById(playlistId)
            .orElseThrow { NotFoundException("Playlist no encontrada") }

        if (playlist.creadorId != uid) {
            throw ForbiddenException("No puedes modificar una playlist que no creaste")
        }

        val cancion = spotifySearchService.buscarCancionPorId(cancionId)

        if (playlist.canciones.any { it.id == cancion.id }) {
            throw IllegalStateException("La canción ya está en la playlist")
        }

        val actualizada = playlist.copy(canciones = playlist.canciones + cancion)
        playlistRepository.save(actualizada)
        return mapper.toDTO(actualizada)
    }

    /**
     * Elimina una canción de una playlist creada por el usuario.
     *
     * @param playlistId ID de la playlist.
     * @param cancionId ID de la canción a eliminar.
     * @param uid ID del usuario autenticado.
     * @return [PlaylistDTO] con la playlist actualizada.
     * @throws ForbiddenException si el usuario no es el creador.
     */
    fun eliminarCancion(playlistId: String, cancionId: String, uid: String): PlaylistDTO {
        val playlist = playlistRepository.findById(playlistId)
            .orElseThrow { NotFoundException("Playlist no encontrada") }

        if (playlist.creadorId != uid) {
            throw ForbiddenException("No puedes modificar una playlist que no creaste")
        }

        val nuevasCanciones = playlist.canciones.filterNot { it.id == cancionId }
        val actualizada = playlist.copy(canciones = nuevasCanciones)

        playlistRepository.save(actualizada)
        return mapper.toDTO(actualizada)
    }
}