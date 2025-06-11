package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.error.exception.ConflictException
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.repository.PlaylistRepository
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.example.spotifyapitfg.utils.crearUsuarioDTOTest
import com.example.spotifyapitfg.utils.crearUsuarioTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

@ExtendWith(MockitoExtension::class)
class UsuarioServiceTest {

    @Mock
    lateinit var usuarioRepository: UsuarioRepository

    @Mock
    lateinit var spotifySearchService: SpotifySearchService

    @Mock
    lateinit var playlistRepository: PlaylistRepository

    @Mock
    lateinit var firebaseAuthService: FirebaseAuthService

    @Mock
    lateinit var mapper: Mapper

    @InjectMocks
    lateinit var usuarioService: UsuarioService

    @Test
    fun `obtenerUsuarioMostrable devuelve DTO con datos mostrables del usuario`() {
        val uid = "user123"
        val usuario = crearUsuarioTest(id = uid).apply {
            biblioteca.likedCanciones.add("song1")
            biblioteca.likedArtistas.add("artist1")
            biblioteca.likedAlbums.add("album1")
            biblioteca.likedPlaylists.add("playlist1")
            biblioteca.playlistsCreadas.add("playlist2")
        }

        whenever(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))
        whenever(spotifySearchService.buscarCancionPorId("song1")).thenReturn(mock())
        whenever(spotifySearchService.buscarArtistaPorId("artist1")).thenReturn(mock())
        whenever(spotifySearchService.buscarAlbumPorId("album1")).thenReturn(mock())
        whenever(spotifySearchService.buscarPlaylistPorId("playlist1")).thenReturn(mock())
        whenever(playlistRepository.findById("playlist2")).thenReturn(Optional.of(mock()))

        val result = usuarioService.obtenerUsuarioMostrable(uid)

        assertEquals(uid, result.id)
        verify(usuarioRepository).findById(uid)
        verify(spotifySearchService).buscarCancionPorId("song1")
        verify(spotifySearchService).buscarArtistaPorId("artist1")
        verify(spotifySearchService).buscarAlbumPorId("album1")
        verify(spotifySearchService).buscarPlaylistPorId("playlist1")
        verify(playlistRepository).findById("playlist2")
    }

    @Test
    fun `login debe devolver UsuarioDTO si token es valido y usuario existe`() {
        val idToken = "validToken"
        val uid = "user123"
        val usuario = crearUsuarioTest(id = uid)
        val usuarioDTO = crearUsuarioDTOTest(id = uid)

        whenever(firebaseAuthService.login(idToken)).thenReturn(uid)
        whenever(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))
        whenever(mapper.toDTO(usuario)).thenReturn(usuarioDTO)

        val result = usuarioService.login(idToken)

        assertEquals(uid, result.id)
        verify(firebaseAuthService).login(idToken)
        verify(usuarioRepository).findById(uid)
        verify(mapper).toDTO(usuario)
    }

    @Test
    fun `login lanza excepcion si token invalido o usuario no existe`() {
        val idToken = "invalidToken"

        whenever(firebaseAuthService.login(idToken)).thenThrow(RuntimeException("Token inválido"))

        val exception = assertThrows(UsernameNotFoundException::class.java) {
            usuarioService.login(idToken)
        }

        assertTrue(exception.message!!.contains("No se pudo validar el token de Firebase"))
        verify(firebaseAuthService).login(idToken)
    }

    @Test
    fun `obtenerUsuarioPorId debe devolver UsuarioDTO si el usuario existe`() {
        val uid = "user123"
        val usuario = crearUsuarioTest(id = uid)

        whenever(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))

        val resultado = usuarioService.obtenerUsuarioPorId(uid)

        assertEquals(uid, resultado.id)
        verify(usuarioRepository).findById(uid)
    }

    @Test
    fun `registrarUsuario lanza ConflictException si el usuario ya existe`() {
        val usuario = crearUsuarioTest()

        whenever(usuarioRepository.existsById(usuario.id!!)).thenReturn(true)

        assertThrows(ConflictException::class.java) {
            usuarioService.registrarUsuario(usuario)
        }

        verify(usuarioRepository).existsById(usuario.id!!)
        verify(usuarioRepository, never()).save(any())
    }

    @Test
    fun `actualizarPremium actualiza el rol del usuario a PREMIUM`() {
        val uid = "user123"
        val usuario = crearUsuarioTest(id = uid).copy(role = Role.USER)

        whenever(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))

        usuarioService.actualizarPremium(uid)

        assertEquals(Role.PREMIUM, usuario.role)
        verify(usuarioRepository).save(usuario)
    }
}
