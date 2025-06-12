package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.models.Mix
import com.example.spotifyapitfg.dto.PlaylistCreateDTO
import com.example.spotifyapitfg.dto.PlaylistDTO
import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.service.PlaylistService
import com.example.spotifyapitfg.service.SpotifySearchService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(PlaylistController::class)
@AutoConfigureMockMvc
class PlaylistControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var playlistService: PlaylistService

    @MockBean
    private lateinit var spotifySearchService: SpotifySearchService





    private val auth = UsernamePasswordAuthenticationToken("user123", null, listOf(SimpleGrantedAuthority("ROLE_USER")))

    private val objectMapper = ObjectMapper()

    private val cancionesDeEjemplo = listOf(
        Cancion(
            id = "c1",
            nombre = "Canción Uno",
            artista = "Artista A",
            album = "Álbum A",
            imagenUrl = "https://example.com/imagen1.jpg",
            duracionMs = 210000,
            previewUrl = "https://example.com/preview1.mp3",
            popularidad = 80,
            urlSpotify = "https://open.spotify.com/track/c1",
            audioUrl = "https://example.com/audio1.mp3"
        ),
        Cancion(
            id = "c2",
            nombre = "Canción Dos",
            artista = "Artista B",
            album = "Álbum B",
            imagenUrl = "https://example.com/imagen2.jpg",
            duracionMs = 180000,
            previewUrl = "https://example.com/preview2.mp3",
            popularidad = 70,
            urlSpotify = "https://open.spotify.com/track/c2",
            audioUrl = "https://example.com/audio2.mp3"
        ),
        Cancion(
            id = "c3",
            nombre = "Canción Tres",
            artista = "Artista C",
            album = "Álbum C",
            imagenUrl = "https://example.com/imagen3.jpg",
            duracionMs = 240000,
            previewUrl = "https://example.com/preview3.mp3",
            popularidad = 90,
            urlSpotify = "https://open.spotify.com/track/c3",
            audioUrl = "https://example.com/audio3.mp3"
        )
    )


    @Test
    fun `POST crear playlist debe devolver 200 y playlistDTO`() {
        val dto = PlaylistCreateDTO("Test Playlist", "desc", "")
        val response = PlaylistDTO("1", dto.nombre, dto.descripcion, emptyList(), "user123", "User", "")

        whenever(playlistService.crearPlaylist("user123", dto)).thenReturn(response)

        mockMvc.perform(
            post("/playlists/crear")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.nombre").value("Test Playlist"))
    }

    @Test
    fun `PUT editar playlist debe devolver 200 y playlist modificada`() {
        val dto = PlaylistCreateDTO("Updated", "new desc", "")
        val modified = PlaylistDTO("1", "Updated", "new desc", cancionesDeEjemplo, "user123", "User", "")

        whenever(playlistService.modificarPlaylist("user123", "1", dto)).thenReturn(modified)

        mockMvc.perform(
            put("/playlists/1/editar")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nombre").value("Updated"))
    }

    @Test
    fun `POST mix debe devolver playlist combinada`() {
        val mix = Mix("1", "2")
        val result = PlaylistDTO("3", "Mix", "result", cancionesDeEjemplo, "user123", "User", "")

        whenever(playlistService.mezclarPlaylists("1", "2", "user123")).thenReturn(result)

        mockMvc.perform(
            post("/playlists/mix")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mix))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("3"))
    }

    @Test
    fun `PUT agregarCancion debe devolver playlist actualizada`() {
        val result = PlaylistDTO("1", "Name", "desc", cancionesDeEjemplo, "user123", "User", "")
        whenever(playlistService.agregarCancion("1", "c1", "user123")).thenReturn(result)

        mockMvc.perform(
            put("/playlists/1/agregarCancion/c1")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.canciones[0].id").value("c1"))
    }

    @Test
    fun `DELETE playlist debe devolver 204`() {
        mockMvc.perform(
            delete("/playlists/1")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `POST like playlist debe devolver usuario actualizado`() {
        val usuarioDTO = UsuarioDTO("user123", "User", "email", 0, 0, 0, null, "USER")
        whenever(playlistService.likePlaylist("user123", "1")).thenReturn(usuarioDTO)

        mockMvc.perform(
            post("/playlists/like/1")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("user123"))
    }

    @Test
    fun `DELETE unlike playlist debe devolver usuario actualizado`() {
        val usuarioDTO = UsuarioDTO("user123", "User", "email", 0, 0, 0, null, "USER")
        whenever(playlistService.unlikePlaylist("user123", "1")).thenReturn(usuarioDTO)

        mockMvc.perform(
            delete("/playlists/like/1")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("user123"))
    }

    @Test
    fun `GET creadas debe devolver playlists del usuario`() {
        val list = listOf(PlaylistDTO("1", "Mi Playlist", "desc", emptyList(), "user123", "User", ""))
        whenever(playlistService.obtenerPlaylistsCreadasPorUsuario("user123")).thenReturn(list)

        mockMvc.perform(
            get("/playlists/creadas")
                .with(authentication(auth))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value("1"))

    }
}
