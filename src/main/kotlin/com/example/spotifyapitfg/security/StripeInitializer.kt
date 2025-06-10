package com.example.spotifyapitfg.security

import com.stripe.Stripe
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class StripeInitializer(
    @Value("\${stripe.secret.key}") private val secretKey: String,
) {

    @PostConstruct
    fun init() {
        println("Stripe Secret Key: $secretKey")
        Stripe.apiKey = secretKey
    }

}