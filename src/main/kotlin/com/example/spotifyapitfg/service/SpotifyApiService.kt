package com.example.spotifyapitfg.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SpotifyApiService {

    @Value("\${spotify.api.clientId}")
    private lateinit var clientId: String

    @Value("\${spotify.api.clientSecret}")
    private lateinit var clientSecret: String

    @Value("\${spotify.api.redirectUri}")
    private lateinit var redirectUri: String

    fun mostrarCredenciales() {
        println("Client ID: $clientId")
        println("Client Secret: $clientSecret")
        println("Redirect URI: $redirectUri")
    }
}