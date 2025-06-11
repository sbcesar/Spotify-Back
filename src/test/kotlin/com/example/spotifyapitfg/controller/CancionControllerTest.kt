package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.UsuarioDTO
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.service.CancionService
import com.example.spotifyapitfg.service.SpotifySearchService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CancionController::class)
@AutoConfigureMockMvc
class CancionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var cancionService: CancionService

    @MockBean
    private lateinit var spotifySearchService: SpotifySearchService

    private val auth = UsernamePasswordAuthenticationToken("user123", null, listOf(SimpleGrantedAuthority("ROLE_USER")))

    private val cancion = Cancion(
        id = "c1",
        nombre = "Canción Uno",
        artista = "Artista A",
        album = "Álbum A",
        imagenUrl = "https://example.com/imagen.jpg",
        duracionMs = 210000,
        previewUrl = "https://example.com/preview.mp3",
        popularidad = 80,
        urlSpotify = "https://open.spotify.com/track/c1",
        audioUrl = "https://example.com/audio.mp3"
    )

    @Test
    fun `GET obtenerCancionPorId devuelve cancion`() {
        whenever(spotifySearchService.buscarCancionPorId("c1")).thenReturn(cancion)

        mockMvc.perform(
            get("/canciones/c1")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("c1"))
            .andExpect(jsonPath("$.nombre").value("Canción Uno"))
    }

    @Test
    fun `GET getCanciones devuelve lista de canciones`() {
        whenever(cancionService.obtenerCanciones()).thenReturn(listOf(cancion))

        mockMvc.perform(
            get("/canciones/all")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].id").value("c1"))
    }

    @Test
    fun `POST likeCancion debe devolver usuario actualizado`() {
        val usuario = UsuarioDTO("user123", "User", "email@example.com", 0, 0, 0, null, "USER")
        whenever(cancionService.likeCancion("user123", "c1")).thenReturn(usuario)

        mockMvc.perform(
            post("/canciones/like/c1")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("user123"))
    }

    @Test
    fun `DELETE unlikeCancion debe devolver usuario actualizado`() {
        val usuario = UsuarioDTO("user123", "User", "email@example.com", 0, 0, 0, null, "USER")
        whenever(cancionService.unlikeCancion("user123", "c1")).thenReturn(usuario)

        mockMvc.perform(
            delete("/canciones/like/c1")
                .with(authentication(auth))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("user123"))
    }
}
