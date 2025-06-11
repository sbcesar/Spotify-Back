package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.models.Album
import com.example.spotifyapitfg.models.Artista
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.models.Playlist
import com.example.spotifyapitfg.security.FirebaseAuthenticationFilter
import com.example.spotifyapitfg.service.SpotifyAuthService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(controllers = [SpotifyController::class], excludeAutoConfiguration = [SecurityAutoConfiguration::class])
class SpotifyControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var spotifyAuthService: SpotifyAuthService

    @MockBean
    lateinit var spotifySearchService: SpotifySearchService

    @Test
    fun `GET obtenerToken devuelve 200 y token`() {
        whenever(spotifyAuthService.obtenerTokenDeAcceso()).thenReturn("fake-token")

        mockMvc.get("/spotify/token")
            .andExpect {
                status { isOk() }
                content { string("fake-token") }
            }
    }

    @Test
    fun `GET buscarCancion devuelve lista de canciones`() {
        val canciones = listOf(
            Cancion("1", "Song 1", "Artist", "Album", null, 200000, null, 50, "spotify-url", null)
        )

        whenever(spotifySearchService.buscarCanciones("test")).thenReturn(canciones)

        mockMvc.get("/spotify/buscar/canciones?query=test")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].nombre") { value("Song 1") }
            }
    }

    @Test
    fun `GET buscarAlbumes devuelve lista de albumes`() {
        val albumes = listOf(Album("1", "Album 1", "Artist", "", "", 3, 80, "", emptyList(), emptyList()))

        whenever(spotifySearchService.buscarAlbumes("album")).thenReturn(albumes)

        mockMvc.get("/spotify/buscar/albumes?query=album")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].nombre") { value("Album 1") }
            }
    }

    @Test
    fun `GET buscarArtistas devuelve lista de artistas`() {
        val artistas = listOf(Artista("1", "Artist 1", "Genre", 80, 99999, "", emptyList()))

        whenever(spotifySearchService.buscarArtistas("artist")).thenReturn(artistas)

        mockMvc.get("/spotify/buscar/artistas?query=artist")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].nombre") { value("Artist 1") }
            }
    }

    @Test
    fun `GET buscarPlaylists devuelve lista de playlists`() {
        val playlists = listOf(Playlist("1", "Playlist 1", "desc", emptyList(), "user1", "User", null))

        whenever(spotifySearchService.buscarPlaylists("mix")).thenReturn(playlists)

        mockMvc.get("/spotify/buscar/playlists?query=mix")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].nombre") { value("Playlist 1") }
            }
    }
}
