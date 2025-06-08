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

@Component
class FirebaseAuthenticationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            try {
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
                val uid = decodedToken.uid

                // Creamos el objeto de autenticación
                val authentication = UsernamePasswordAuthenticationToken(uid, null, null)
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