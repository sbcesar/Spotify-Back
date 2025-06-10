package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.example.spotifyapitfg.service.StripeService
import com.stripe.net.Webhook

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/stripe")
class StripeController {

    @Autowired
    private lateinit var stripeService: StripeService

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Value("\${stripe.webhook.secret}")
    private lateinit var webhookSecret: String

    /**
     * Inicia el proceso de pago y devuelve la URL del checkout
     */
    @PostMapping("/checkout")
    fun iniciarCheckout(
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val uid = authentication.name
        val checkoutUrl = stripeService.crearSesionPago(uid)

        return ResponseEntity.ok(mapOf("url" to checkoutUrl))
    }

    /**
     * Webhook de Stripe que convierte al usuario en PREMIUM cuando se completa el pago
     */
    @PostMapping("/webhook")
    fun manejarWebhook(
        @RequestBody playload: String,
        @RequestHeader("Stripe-Signature") sigHeader: String
    ): ResponseEntity<String> {
        val event = try {
            Webhook.constructEvent(playload, sigHeader, webhookSecret)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Webhook no verificado ${e.message}")
        }

        if (event.type == "checkout.session.completed") {
            val session = event.data?.`object` as Map<String, Any>

            val metadata = session["metadata"] as? Map<String, Any>

            val usuarioId = metadata?.get("usuarioId") as? String

            if (!usuarioId.isNullOrEmpty()) {
                // Buscar al usuario en la base de datos usando el usuarioId
                val usuario = usuarioRepository.findById(usuarioId.toString()).orElse(null)
                if (usuario != null) {
                    // Si el usuario existe, actualizar su rol a PREMIUM
                    usuario.role = Role.PREMIUM
                    usuarioRepository.save(usuario)
                    println("Usuario $usuarioId actualizado a PREMIUM")
                } else {
                    println("Usuario no encontrado para ID: $usuarioId")
                }
            }
        }

        return ResponseEntity.ok("Webhook recibido")
    }

}