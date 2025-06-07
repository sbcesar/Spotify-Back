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

    fun obtenerTodas(): List<PlaylistDTO> {
        val playlists = playlistRepository.findAll()
        return playlists.map { mapper.toDTO(it) }
    }

    fun obtenerPlaylistsCreadasPorUsuario(uid: String): List<PlaylistDTO> {
        val playlists = playlistRepository.findByCreadorId(uid)
        return playlists.map { mapper.toDTO(it) }
    }

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

    fun likePlaylist(uid: String, playlistId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (!usuario.biblioteca.likedPlaylists.contains(playlistId)) {
            usuario.biblioteca.likedPlaylists.add(playlistId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

    fun unlikePlaylist(uid: String, playlistId: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        if (usuario.biblioteca.likedPlaylists.contains(playlistId)) {
            usuario.biblioteca.likedPlaylists.remove(playlistId)
            usuarioRepository.save(usuario)
        }

        return mapper.toDTO(usuario)
    }

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