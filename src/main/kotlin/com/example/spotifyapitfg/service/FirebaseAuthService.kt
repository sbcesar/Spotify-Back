package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class FirebaseAuthService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    fun registrarUsuario(email: String, password: String): String {
        val request = UserRecord.CreateRequest()
            .setEmail(email)
            .setPassword(password)

        val createdUser = FirebaseAuth.getInstance().createUser(request)
        return createdUser.uid
    }

    fun login(idToken: String): String {
        try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
            return decodedToken.uid
        } catch (e: Exception) {
            throw UsernameNotFoundException("No se pudo validar el token de Firebase: ${e.message}")
        }
    }

    fun usuarioEsAdmin(uid: String): Boolean {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        return usuario.role == Role.ADMIN
    }
}