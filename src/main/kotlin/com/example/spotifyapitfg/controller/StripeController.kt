package com.example.spotifyapitfg.controller

import com.example.spotifyapitfg.service.StripeService
import com.example.spotifyapitfg.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * Controlador REST para gestionar operaciones relacionadas con Stripe,
 * como el inicio del proceso de checkout y el manejo de webhooks.
 *
 * @property stripeService Servicio encargado de interactuar con la API de Stripe.
 * @property usuarioService Servicio para actualizar el estado del usuario en la base de datos.
 * @property webhookSecret Clave secreta utilizada para validar los webhooks de Stripe.
 */
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
     * Inicia una sesión de checkout en Stripe para que el usuario realice el pago de la suscripción.
     *
     * @param authentication Información de autenticación del usuario actual.
     * @return Un objeto [ResponseEntity] que contiene la URL de la sesión de checkout.
     */
    @PostMapping("/checkout")
    fun iniciarCheckout(
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val uid = authentication.name
        val checkoutUrl = stripeService.crearSesionSuscripcion(uid)

        return ResponseEntity.ok(mapOf("url" to checkoutUrl))
    }

    /**
     * Endpoint para manejar los webhooks enviados por Stripe.
     * Verifica el evento y si el pago fue exitoso, actualiza el estado del usuario a PREMIUM.
     *
     * @param payload Cuerpo del webhook recibido.
     * @param sigHeader Encabezado de firma proporcionado por Stripe para validar el evento.
     * @return Un [ResponseEntity] indicando el resultado del procesamiento del webhook.
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