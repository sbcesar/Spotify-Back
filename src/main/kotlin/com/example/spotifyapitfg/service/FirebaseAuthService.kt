package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Servicio encargado de gestionar la autenticación de usuarios a través de Firebase.
 * Proporciona funciones para registrar usuarios, verificar tokens de inicio de sesión
 * y determinar si un usuario tiene rol de administrador.
 *
 * @property usuarioRepository Repositorio que permite acceder a la información de los usuarios en la base de datos.
 */
@Service
class FirebaseAuthService(
    private var usuarioRepository: UsuarioRepository
) {

//    @Autowired
//    private lateinit var usuarioRepository: UsuarioRepository

    /**
     * Registra un nuevo usuario en Firebase Authentication con el correo y la contraseña especificados.
     *
     * @param email Correo electrónico del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     * @return UID del usuario creado en Firebase.
     */
    fun registrarUsuario(email: String, password: String): String {
        val request = UserRecord.CreateRequest()
            .setEmail(email)
            .setPassword(password)

        val createdUser = FirebaseAuth.getInstance().createUser(request)
        return createdUser.uid
    }

    /**
     * Verifica un token JWT proporcionado por Firebase y devuelve el UID del usuario autenticado.
     *
     * @param idToken Token JWT recibido del cliente.
     * @return UID del usuario autenticado si el token es válido.
     * @throws UsernameNotFoundException si el token no es válido o no se puede verificar.
     */
    fun login(idToken: String): String {
        try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
            return decodedToken.uid
        } catch (e: Exception) {
            throw UsernameNotFoundException("No se pudo validar el token de Firebase: ${e.message}")
        }
    }

    /**
     * Verifica si un usuario tiene el rol de administrador.
     *
     * @param uid UID del usuario a verificar.
     * @return `true` si el usuario tiene el rol ADMIN, `false` en caso contrario.
     * @throws NotFoundException si el usuario no existe en la base de datos.
     */
    fun usuarioEsAdmin(uid: String): Boolean {
        val usuario = usuarioRepository.findById(uid)
            .orElseThrow { NotFoundException("Usuario no encontrado") }

        return usuario.role == Role.ADMIN
    }
}