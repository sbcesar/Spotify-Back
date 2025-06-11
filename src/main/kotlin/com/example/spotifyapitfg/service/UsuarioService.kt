package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.BibliotecaDTO
import com.example.spotifyapitfg.dto.BibliotecaMostrableDTO
import com.example.spotifyapitfg.dto.UsuarioBibliotecaMostrableDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.error.exception.ConflictException
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.repository.PlaylistRepository
import com.example.spotifyapitfg.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los usuarios,
 * como la obtención de datos, registro, login y actualización de estado PREMIUM.
 *
 * @property usuarioRepository Repositorio de usuarios.
 * @property spotifySearchService Servicio para obtener datos desde Spotify.
 * @property playlistRepository Repositorio de playlists de la aplicación.
 * @property firebaseAuthService Servicio de autenticación con Firebase.
 * @property mapper Mapeador para convertir entidades a DTOs.
 */
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

    /**
     * Obtiene los datos básicos del usuario y su biblioteca en formato DTO.
     *
     * @param uid ID del usuario.
     * @return [UsuarioDTO] con la información del usuario y su biblioteca.
     * @throws UsernameNotFoundException si el usuario no existe.
     */
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

    /**
     * Obtiene un DTO completo del usuario con su biblioteca expandida,
     * incluyendo los datos reales de canciones, artistas, álbumes y playlists.
     *
     * @param uid ID del usuario autenticado.
     * @return [UsuarioBibliotecaMostrableDTO] con todos los datos visibles de la biblioteca.
     * @throws UsernameNotFoundException si el usuario no existe.
     */
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

    /**
     * Registra un nuevo usuario si su ID no está ya presente en la base de datos.
     *
     * @param usuario Objeto [Usuario] a registrar.
     * @return [UsuarioDTO] con los datos del usuario registrado.
     * @throws ConflictException si ya existe un usuario con el mismo ID.
     */
    fun registrarUsuario(usuario: Usuario): UsuarioDTO {
        if (usuarioRepository.existsById(usuario.id!!)) throw ConflictException("El usuario ya existe")

        val usuarioGuardado = usuarioRepository.save(usuario)

        return mapper.toDTO(usuarioGuardado)
    }

    /**
     * Inicia sesión validando el token de Firebase y devuelve el DTO del usuario.
     *
     * @param idToken Token JWT emitido por Firebase.
     * @return [UsuarioDTO] con los datos del usuario autenticado.
     * @throws UsernameNotFoundException si el token no es válido o el usuario no se encuentra en la base de datos.
     */
    fun login(idToken: String): UsuarioDTO {
        try {
            val uid = firebaseAuthService.login(idToken)

            val usuario = usuarioRepository.findById(uid).orElseThrow { UsernameNotFoundException("Usuario no encontrado en MongoDB") }

            return mapper.toDTO(usuario)
        } catch (e: Exception) {
            throw UsernameNotFoundException("No se pudo validar el token de Firebase: ${e.message}")
        }
    }

    /**
     * Actualiza el rol de un usuario a `PREMIUM`.
     *
     * @param usuarioId ID del usuario que ha completado el pago.
     * @throws NotFoundException si el usuario no existe.
     */
    fun actualizarPremium(usuarioId: String) {
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NotFoundException("Usuario no encontrado") }

        usuario?.let {
            it.role = Role.PREMIUM
            usuarioRepository.save(it)
        }
    }
}