package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.dto.BibliotecaMostrableDTO
import com.example.spotifyapitfg.dto.UsuarioBibliotecaMostrableDTO
import com.example.spotifyapitfg.service.FirebaseAuthService
import com.example.spotifyapitfg.service.UsuarioService
import com.example.spotifyapitfg.utils.crearUsuarioDTOTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(UsuarioController::class)
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usuarioService: UsuarioService

    @MockBean
    private lateinit var firebaseAuthService: FirebaseAuthService


    @Test
    fun `getUserProfile debe devolver 200 y usuarioDTO`() {
        val uid = "user123"
        val usuarioDTO = crearUsuarioDTOTest(id = uid)


        val auth = UsernamePasswordAuthenticationToken(uid, null, listOf(SimpleGrantedAuthority("ROLE_USER")))

        whenever(usuarioService.obtenerUsuarioPorId(uid)).thenReturn(usuarioDTO)

        mockMvc.perform(
            get("/usuario/perfil")
                .with(authentication(auth))
        )

            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(uid))
    }

    @Test
    fun `GET mostrar debe devolver 200 y datos del usuario`() {
        val uid = "user123"
        val usuarioMostrable = UsuarioBibliotecaMostrableDTO(
            id = uid,
            nombre = "Test User",
            email = "test@example.com",
            playlistCount = 2,
            seguidores = 10,
            seguidos = 5,
            biblioteca = BibliotecaMostrableDTO(
                canciones = emptyList(),
                artistas = emptyList(),
                albumes = emptyList(),
                playlists = emptyList()
            )
        )

        val auth = UsernamePasswordAuthenticationToken(uid, null, listOf(SimpleGrantedAuthority("ROLE_USER")))

        whenever(usuarioService.obtenerUsuarioMostrable(uid)).thenReturn(usuarioMostrable)

        mockMvc.perform(
            get("/usuario/biblioteca")
                .with(authentication(auth))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(uid))
            .andExpect(jsonPath("$.nombre").value("Test User"))

        verify(usuarioService).obtenerUsuarioMostrable(uid)
    }

}
