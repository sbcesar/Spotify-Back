package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.models.Role
import com.example.spotifyapitfg.repository.UsuarioRepository
import com.example.spotifyapitfg.service.StripeService
import com.example.spotifyapitfg.service.UsuarioService
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
    private lateinit var usuarioService: UsuarioService

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
        @RequestBody payload: String,
        @RequestHeader("Stripe-Signature") sigHeader: String
    ): ResponseEntity<String> {
        val event = try {
            stripeService.verificarEvento(payload, sigHeader, webhookSecret)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Webhook inválido: ${e.message}")
        }

        if (event.type == "checkout.session.completed") {
            val session = event.data?.`object` as Map<*, *>
            val metadata = session["metadata"] as? Map<*, *>
            val usuarioId = metadata?.get("usuarioId") as? String
            usuarioId?.let {
                usuarioService.actualizarPremium(it)
            }
        }

        return ResponseEntity.ok("Webhook recibido")
    }

}