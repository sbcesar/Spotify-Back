package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.dto.PlaylistCreateDTO
import com.example.spotifyapitfg.dto.PlaylistDTO
import com.example.spotifyapitfg.error.exception.ForbiddenException
import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.models.Playlist
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.repository.PlaylistRepository
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.example.spotifyapitfg.utils.crearPlaylistCreateDTOTest
import com.example.spotifyapitfg.utils.crearPlaylistDTOTest
import com.example.spotifyapitfg.utils.crearUsuarioTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
class PlaylistServiceTest {

    @Mock
    lateinit var usuarioRepository: UsuarioRepository

    @Mock
    lateinit var playlistRepository: PlaylistRepository

    @Mock
    lateinit var firebaseAuthService: FirebaseAuthService

    @Mock
    lateinit var mapper: Mapper

    @InjectMocks
    lateinit var playlistService: PlaylistService

    @Test
    fun `crearPlaylist debe crear y guardar una nueva playlist`() {
        val uid = "user123"
        val usuario = crearUsuarioTest(id = uid)
        val dto = crearPlaylistCreateDTOTest(nombre = "Mi Playlist", descripcion = "Descripción")
        val playlist = Playlist(
            id = "playlist123",
            nombre = dto.nombre,
            descripcion = dto.descripcion,
            canciones = emptyList(),
            creadorId = uid,
            creadorNombre = usuario.nombre,
            imagenUrl = dto.imagenUrl
        )

        whenever(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))
        whenever(playlistRepository.save(any())).thenReturn(playlist)
        whenever(usuarioRepository.save(any())).thenReturn(usuario)
        whenever(mapper.toDTO(any<Playlist>())).thenReturn(crearPlaylistDTOTest(id = "playlist123"))

        val result = playlistService.crearPlaylist(uid, dto)

        assertEquals("playlist123", result.id)
        verify(playlistRepository).save(any())
        verify(usuarioRepository).save(usuario)
    }

    @Test
    fun `modificarPlaylist debe actualizar una playlist si es del usuario`() {
        val uid = "user123"
        val playlistId = "playlist123"
        val original = Playlist(
            id = playlistId,
            nombre = "Antigua",
            descripcion = "Vieja",
            canciones = emptyList(),
            creadorId = uid,
            creadorNombre = "Nombre",
            imagenUrl = ""
        )
        val dto = crearPlaylistCreateDTOTest(nombre = "Nueva", descripcion = "Actualizada")
        val actualizada = original.copy(nombre = dto.nombre, descripcion = dto.descripcion)

        whenever(playlistRepository.findById(playlistId)).thenReturn(Optional.of(original))
        whenever(firebaseAuthService.usuarioEsAdmin(uid)).thenReturn(false)
        whenever(playlistRepository.save(any())).thenReturn(actualizada)
        whenever(mapper.toDTO(any<Playlist>())).thenReturn(crearPlaylistDTOTest(id = playlistId, nombre = "Nueva"))

        val result = playlistService.modificarPlaylist(uid, playlistId, dto)

        assertEquals("Nueva", result.nombre)
        verify(playlistRepository).save(any())
    }

    @Test
    fun `eliminarPlaylist lanza ForbiddenException si el usuario no es el creador ni admin`() {
        val uid = "user456"
        val playlistId = "playlist123"
        val playlist = Playlist(
            id = playlistId,
            nombre = "Mi Playlist",
            descripcion = "desc",
            canciones = emptyList(),
            creadorId = "otroUser",
            creadorNombre = "Otro",
            imagenUrl = ""
        )

        whenever(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlist))
        whenever(firebaseAuthService.usuarioEsAdmin(uid)).thenReturn(false)

        assertThrows(ForbiddenException::class.java) {
            playlistService.eliminarPlaylist(uid, playlistId)
        }
    }
}
