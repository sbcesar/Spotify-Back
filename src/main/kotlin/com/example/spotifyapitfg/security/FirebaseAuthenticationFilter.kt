package com.example.spotifyapitfg.security

import com.google.firebase.auth.FirebaseAuth
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filtro personalizado de Spring Security que intercepta cada petición HTTP una sola vez,
 * y valida el token JWT proporcionado en la cabecera "Authorization" usando Firebase.
 *
 * Si el token es válido, se establece la autenticación en el contexto de seguridad de Spring.
 *
 * Este filtro permite integrar autenticación con Firebase en una aplicación Spring Boot.
 */
@Component
class FirebaseAuthenticationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Obtenemos el encabezado Authorization de la solicitud HTTP
        val authHeader = request.getHeader("Authorization")

        // Verificamos si el encabezado contiene un token JWT válido
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            try {
                // Validamos y decodificamos el token con Firebase
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)

                val uid = decodedToken.uid

                // Creamos un objeto de autenticación de Spring Security con el UID como nombre de usuario (no incluye roles ni credenciales)
                val authentication = UsernamePasswordAuthenticationToken(uid, null, null)

                // Asociamos los detalles de la solicitud (IP, sesión...) al objeto de autenticación
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // Establecemos la autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().authentication = authentication

            } catch (e: Exception) {
                println("Error al validar token de Firebase: ${e.message}")
            }
        }
        filterChain.doFilter(request, response)
    }
}