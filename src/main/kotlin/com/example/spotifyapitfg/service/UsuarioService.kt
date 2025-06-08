package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.BibliotecaDTO
import com.example.spotifyapitfg.dto.BibliotecaMostrableDTO
import com.example.spotifyapitfg.dto.UsuarioBibliotecaMostrableDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.ConflictException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.models.Biblioteca
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.repository.PlaylistRepository
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UsuarioService {

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

    fun obtenerUsuarioPorId(uid: String): UsuarioDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { UsernameNotFoundException("Usuario no encontrado") }

        val biblioteca = usuario.biblioteca
        val bibliotecaDTO = BibliotecaDTO(
            playlistsCreadas = biblioteca.playlistsCreadas.toMutableList(),
            likedCanciones = biblioteca.likedCanciones.toMutableList(),
            likedPlaylists = biblioteca.likedPlaylists.toMutableList(),
            likedArtistas = biblioteca.likedArtistas.toMutableList(),
            likedAlbums = biblioteca.likedAlbums.toMutableList()
        )

        return UsuarioDTO(
            id = usuario.id,
            nombre = usuario.nombre,
            email = usuario.email,
            playlistCount = usuario.playlistCount,
            seguidores = usuario.seguidores,
            seguidos = usuario.seguidos,
            biblioteca = bibliotecaDTO,
            role = usuario.role.toString()
        )
    }

    fun obtenerNombrePorId(uid: String): String {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { RuntimeException("Usuario no encontrado con ID: $uid") }
        return usuario.nombre
    }

    fun obtenerUsuarioMostrable(uid: String): UsuarioBibliotecaMostrableDTO {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { UsernameNotFoundException("Usuario no encontrado") }

        val canciones = usuario.biblioteca.likedCanciones.mapNotNull {
            try {
                spotifySearchService.buscarCancionPorId(it)
            } catch (e: Exception) {
                null
            }
        }

        val artistas = usuario.biblioteca.likedArtistas.mapNotNull {
            try {
                spotifySearchService.buscarArtistaPorId(it)
            } catch (e: Exception) {
                null
            }
        }

        val albumes = usuario.biblioteca.likedAlbums.mapNotNull {
            try {
                spotifySearchService.buscarAlbumPorId(it)
            } catch (e: Exception) {
                null
            }
        }

        val likedPlaylists = usuario.biblioteca.likedPlaylists.mapNotNull {
            try { spotifySearchService.buscarPlaylistPorId(it) } catch (e: Exception) { null }
        }

        val creadasPlaylists = usuario.biblioteca.playlistsCreadas.mapNotNull {
            try { playlistRepository.findById(it).orElse(null) } catch (e: Exception) { null }
        }

        val todasLasPlaylists = (likedPlaylists + creadasPlaylists).distinct()

        return UsuarioBibliotecaMostrableDTO(
            id = usuario.id!!,
            nombre = usuario.nombre,
            email = usuario.email,
            playlistCount = usuario.playlistCount,
            seguidores = usuario.seguidores,
            seguidos = usuario.seguidos,
            biblioteca = BibliotecaMostrableDTO(
                canciones = canciones,
                artistas = artistas,
                albumes = albumes,
                playlists = todasLasPlaylists
            )
        )
    }

    fun registrarUsuario(usuario: Usuario): UsuarioDTO {
        if (usuarioRepository.existsById(usuario.id!!)) throw ConflictException("El usuario ya existe")

        val usuarioGuardado = usuarioRepository.save(usuario)

        return mapper.toDTO(usuarioGuardado)
    }

    fun login(idToken: String): UsuarioDTO {
        try {
            val uid = firebaseAuthService.login(idToken)

            val usuario = usuarioRepository.findById(uid).orElseThrow { UsernameNotFoundException("Usuario no encontrado en MongoDB") }

            return mapper.toDTO(usuario)
        } catch (e: Exception) {
            throw UsernameNotFoundException("No se pudo validar el token de Firebase: ${e.message}")
        }
    }

}