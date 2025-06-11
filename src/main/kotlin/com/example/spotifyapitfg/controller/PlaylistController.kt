package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.Mix
import com.example.spotifyapitfg.dto.PlaylistCreateDTO
import com.example.spotifyapitfg.dto.PlaylistDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Playlist
import com.example.spotifyapitfg.service.PlaylistService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * Controlador REST para gestionar operaciones sobre playlists, como creación, edición, mezcla, y likes.
 *
 * @property playlistService Servicio para gestionar las playlists de los usuarios.
 * @property spotifySearchService Servicio para buscar playlists en Spotify.
 */
@RestController
@RequestMapping("/playlists")
class PlaylistController {

    @Autowired
    private lateinit var playlistService: PlaylistService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    /**
     * Obtiene una playlist por su ID.
     *
     * @param id ID de la playlist.
     * @return Objeto [Playlist] con sus detalles.
     */
    @GetMapping("/{id}")
    fun obtenerPlaylistPorId(
        @PathVariable id: String
    ): ResponseEntity<Playlist> {
        val playlist = spotifySearchService.buscarPlaylistPorId(id)

        return ResponseEntity.ok(playlist)
    }

    /**
     * Obtiene todas las playlists del sistema (creadas por usuarios).
     *
     * @return Lista de objetos [PlaylistDTO].
     */
    @GetMapping("/todas")
    fun obtenerTodasLasPlaylists(): ResponseEntity<List<PlaylistDTO>> {
        val playlists = playlistService.obtenerTodas()

        return ResponseEntity.ok(playlists)
    }

    /**
     * Obtiene todas las playlists creadas por el usuario autenticado.
     *
     * @param authentication Información del usuario autenticado.
     * @return Lista de playlists del usuario.
     */
    @GetMapping("/creadas")
    fun obtenerPlaylistsCreadas(
        authentication: Authentication
    ): ResponseEntity<List<PlaylistDTO>> {
        val uid = authentication.name
        val creadas = playlistService.obtenerPlaylistsCreadasPorUsuario(uid)
        return ResponseEntity.ok(creadas)
    }

    /**
     * Permite al usuario marcar una playlist como favorita.
     *
     * @param authentication Información del usuario autenticado.
     * @param cancionId ID de la playlist a marcar como favorita.
     * @return Usuario actualizado con la playlist en su lista de favoritos.
     */
    @PostMapping("/like/{playlistId}")
    fun likePlaylist(
        authentication: Authentication,
        @PathVariable playlistId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = playlistService.likePlaylist(uid, playlistId)

        return ResponseEntity.ok(actualizado)
    }

    /**
     * Crea una nueva playlist personalizada por el usuario.
     *
     * @param authentication Información del usuario autenticado.
     * @param playlistCreateDTO Objeto PlaylistCreateDTO.
     * @return Nueva playlist personalizada.
     */
    @PostMapping("/crear")
    fun crearPlaylist(
        authentication: Authentication,
        @RequestBody playlistCreateDTO: PlaylistCreateDTO
    ): ResponseEntity<PlaylistDTO> {
        val uid = authentication.name
        val nuevaPlaylist = playlistService.crearPlaylist(uid, playlistCreateDTO)

        return ResponseEntity.ok(nuevaPlaylist)
    }

    /**
     * Mezcla dos playlists y crea una nueva combinada.
     *
     * @param playlistsIds Objeto que contiene los IDs de las playlists a mezclar.
     * @param authentication Información del usuario autenticado.
     * @return [ResponseEntity] con la nueva playlist mezclada.
     */
    @PostMapping("/mix")
    fun mezclarPlaylists(
        @RequestBody playlistsIds: Mix,
        authentication: Authentication
    ): ResponseEntity<PlaylistDTO> {
        val uid = authentication.name
        val mixedPlaylist = playlistService.mezclarPlaylists(playlistsIds.playlistId1, playlistsIds.playlistId2, uid)

        return ResponseEntity.ok(mixedPlaylist)
    }

    /**
     * Edita una playlist existente.
     *
     * @param playlistId ID de la playlist a editar.
     * @param dto Datos nuevos de la playlist.
     * @param authentication Información del usuario autenticado.
     * @return [ResponseEntity] con la playlist actualizada.
     */
    @PutMapping("/{playlistId}/editar")
    fun editarPlaylist(
        @PathVariable playlistId: String,
        @RequestBody dto: PlaylistCreateDTO,
        authentication: Authentication
    ): ResponseEntity<PlaylistDTO> {
        val uid = authentication.name
        val modificada = playlistService.modificarPlaylist(uid, playlistId, dto)

        return ResponseEntity.ok(modificada)
    }

    /**
     * Elimina una playlist creada por el usuario.
     *
     * @param playlistId ID de la playlist.
     * @param authentication Información del usuario autenticado.
     * @return [ResponseEntity] vacío si se eliminó correctamente.
     */
    @DeleteMapping("/{playlistId}")
    fun eliminarPlaylist(
        @PathVariable playlistId: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val uid = authentication.name
        playlistService.eliminarPlaylist(uid, playlistId)

        return ResponseEntity.noContent().build()
    }

    /**
     * Agrega una canción a una playlist.
     *
     * @param playlistId ID de la playlist.
     * @param cancionId ID de la canción.
     * @param authentication Información del usuario autenticado.
     * @return [ResponseEntity] con la playlist actualizada.
     */
    @PutMapping("/{playlistId}/agregarCancion/{cancionId}")
    fun agregarCancionAPlaylist(
        @PathVariable playlistId: String,
        @PathVariable cancionId: String,
        authentication: Authentication
    ): ResponseEntity<PlaylistDTO> {
        val uid = authentication.name
        val actualizada = playlistService.agregarCancion(playlistId, cancionId, uid)

        return ResponseEntity.ok(actualizada)
    }

    /**
     * Elimina una canción de una playlist.
     *
     * @param playlistId ID de la playlist.
     * @param cancionId ID de la canción.
     * @param authentication Información del usuario autenticado.
     * @return [ResponseEntity] con la playlist actualizada.
     */
    @PutMapping("/{playlistId}/eliminarCancion/{cancionId}")
    fun eliminarCancionDePlaylist(
        @PathVariable playlistId: String,
        @PathVariable cancionId: String,
        authentication: Authentication
    ): ResponseEntity<PlaylistDTO> {
        val uid = authentication.name
        val actualizada = playlistService.eliminarCancion(playlistId, cancionId, uid)

        return ResponseEntity.ok(actualizada)
    }

    /**
     * Elimina un "like" de una playlist.
     *
     * @param authentication Información del usuario autenticado.
     * @param playlistId ID de la playlist.
     * @return [ResponseEntity] con el usuario actualizado.
     */
    @DeleteMapping("/like/{playlistId}")
    fun unlikePlaylist(
        authentication: Authentication,
        @PathVariable playlistId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = playlistService.unlikePlaylist(uid, playlistId)

        return ResponseEntity.ok(actualizado)
    }
}