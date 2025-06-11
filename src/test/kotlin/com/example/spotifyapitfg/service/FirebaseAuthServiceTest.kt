package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.models.Usuario
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.MockedStatic
import java.util.*

@ExtendWith(MockitoExtension::class)
class FirebaseAuthServiceTest {

    @Mock
    lateinit var usuarioRepository: UsuarioRepository

    @Test
    fun `usuarioEsAdmin devuelve true si el usuario tiene rol ADMIN`() {
        val uid = "adminUser"
        val usuario = Usuario(id = uid, nombre = "Admin", email = "admin@example.com", role = Role.ADMIN)

        `when`(usuarioRepository.findById(uid)).thenReturn(Optional.of(usuario))

        val service = FirebaseAuthService(usuarioRepository)

        val result = service.usuarioEsAdmin(uid)

        assertTrue(result)
        verify(usuarioRepository).findById(uid)
    }

    @Test
    fun `usuarioEsAdmin lanza NotFoundException si el usuario no existe`() {
        val uid = "notExists"

        `when`(usuarioRepository.findById(uid)).thenReturn(Optional.empty())

        val service = FirebaseAuthService(usuarioRepository)

        assertThrows(NotFoundException::class.java) {
            service.usuarioEsAdmin(uid)
        }
    }

    @Test
    fun `login devuelve UID si el token es valido`() {
        val uid = "user123"
        val token = "validToken"
        val firebaseToken = mock(FirebaseToken::class.java)
        `when`(firebaseToken.uid).thenReturn(uid)

        val firebaseAuth = mock(FirebaseAuth::class.java)
        `when`(firebaseAuth.verifyIdToken(token)).thenReturn(firebaseToken)

        val staticMock: MockedStatic<FirebaseAuth> = mockStatic(FirebaseAuth::class.java)
        staticMock.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(firebaseAuth)

        val service = FirebaseAuthService(usuarioRepository)

        val result = service.login(token)

        assertEquals(uid, result)
        staticMock.close()
    }
}
