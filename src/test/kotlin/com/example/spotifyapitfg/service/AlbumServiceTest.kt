package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.mapper.Mapper
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.example.spotifyapitfg.utils.crearUsuarioDTOTest
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
class AlbumServiceTest {

    @Mock
    private lateinit var usuarioRepository: UsuarioRepository

    @Mock
    private lateinit var mapper: Mapper

    @InjectMocks
    private lateinit var albumService: AlbumService

    @Test
    fun `likeAlbum agrega el album si no estaba y devuelve el usuario actualizado`() {
        val uid = "user123"
        val albumId = "album456"
        val usuario = crearUsuarioTest(id = uid)

        whenever(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))
        whenever(mapper.toDTO(usuario)).thenReturn(crearUsuarioDTOTest(id = uid))

        val result = albumService.likeAlbum(uid, albumId)

        assertTrue(usuario.biblioteca.likedAlbums.contains(albumId))
        verify(usuarioRepository).save(usuario)
        assertEquals(uid, result.id)
    }

    @Test
    fun `unlikeAlbum elimina el album si estaba y devuelve el usuario actualizado`() {
        val uid = "user123"
        val albumId = "album456"
        val usuario = crearUsuarioTest(id = uid).apply {
            biblioteca.likedAlbums.add(albumId)
        }

        whenever(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))
        whenever(mapper.toDTO(usuario)).thenReturn(crearUsuarioDTOTest(id = uid))

        val result = albumService.unlikeAlbum(uid, albumId)

        assertFalse(usuario.biblioteca.likedAlbums.contains(albumId))
        verify(usuarioRepository).save(usuario)
        assertEquals(uid, result.id)
    }
}
