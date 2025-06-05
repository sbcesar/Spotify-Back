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
        val playlist1 = playlistRepository.findById(id1).orElseThrow { RuntimeException("Playlist 1 no encontrada") }
        val playlist2 = playlistRepository.findById(id2).orElseThrow { RuntimeException("Playlist 2 no encontrada") }

        val usuario = usuarioRepository.findById(creadorUid).orElseThrow { RuntimeException("Usuario no encontrado") }

        val cancionesTotales = (playlist1.canciones + playlist2.canciones).distinct().shuffled().take(20)

        val mixedPlaylist = Playlist(
            id = UUID.randomUUID().toString(),
            nombre = "Mezcla de ${playlist1.nombre} y ${playlist2.nombre}",
            descripcion = "Generada automáticamente a partir de 2 playlists.",
            canciones = cancionesTotales.toMutableList(),
            creadorId = creadorUid,
            creadorNombre = usuario.nombre,
            imagenUrl = playlist1.imagenUrl ?: playlist2.imagenUrl ?: "",
        )

        playlistRepository.save(mixedPlaylist)

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