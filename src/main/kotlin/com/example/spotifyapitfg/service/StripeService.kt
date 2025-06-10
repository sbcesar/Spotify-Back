package com.example.spotifyapitfg.service

import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StripeService {

    @Value("\${stripe.success.url}")
    private lateinit var successUrl: String

    @Value("\${stripe.cancel.url}")
    private lateinit var cancelUrl: String

    fun crearSesionPago(usuarioId: String): String {
        val sessionParams = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("$successUrl?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(cancelUrl)
            .setCustomerEmail(null)
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setPrice("price_1234567890")
                    .setQuantity(1L)
                    .build()
            )
            .putMetadata("usuarioId", usuarioId)
            .build()

        val session = Session.create(sessionParams)
        return session.url
    }
}