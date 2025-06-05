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

@RestController
@RequestMapping("/playlists")
class PlaylistController {

    @Autowired
    private lateinit var playlistService: PlaylistService

    @Autowired
    private lateinit var spotifySearchService: SpotifySearchService

    @GetMapping("/{id}")
    fun obtenerPlaylistPorId(
        @PathVariable id: String
    ): ResponseEntity<Playlist> {
        val playlist = spotifySearchService.buscarPlaylistPorId(id)

        return ResponseEntity.ok(playlist)
    }

    @GetMapping("/todas")
    fun obtenerTodasLasPlaylists(): ResponseEntity<List<PlaylistDTO>> {
        val playlists = playlistService.obtenerTodas()

        return ResponseEntity.ok(playlists)
    }

    @GetMapping("/creadas")
    fun obtenerPlaylistsCreadas(
        authentication: Authentication
    ): ResponseEntity<List<PlaylistDTO>> {
        val uid = authentication.name
        val creadas = playlistService.obtenerPlaylistsCreadasPorUsuario(uid)
        return ResponseEntity.ok(creadas)
    }

    @PostMapping("/like/{playlistId}")
    fun likePlaylist(
        authentication: Authentication,
        @PathVariable playlistId: String
    ): ResponseEntity<UsuarioDTO> {
        val uid = authentication.name
        val actualizado = playlistService.likePlaylist(uid, playlistId)

        return ResponseEntity.ok(actualizado)
    }

    @PostMapping("/crear")
    fun crearPlaylist(
        authentication: Authentication,
        @RequestBody playlistCreateDTO: PlaylistCreateDTO
    ): ResponseEntity<PlaylistDTO> {
        val uid = authentication.name
        val nuevaPlaylist = playlistService.crearPlaylist(uid, playlistCreateDTO)

        return ResponseEntity.ok(nuevaPlaylist)
    }

    @PostMapping("/mix")
    fun mezclarPlaylists(
        @RequestBody playlistsIds: Mix,
        authentication: Authentication
    ): ResponseEntity<PlaylistDTO> {
        val uid = authentication.name
        val mixedPlaylist = playlistService.mezclarPlaylists(playlistsIds.playlistId1, playlistsIds.playlistId2, uid)

        return ResponseEntity.ok(mixedPlaylist)
    }

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

    @DeleteMapping("/{playlistId}")
    fun eliminarPlaylist(
        @PathVariable playlistId: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val uid = authentication.name
        playlistService.eliminarPlaylist(uid, playlistId)

        return ResponseEntity.noContent().build()
    }

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