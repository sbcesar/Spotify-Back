package com.example.spotifyapitfg.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*

/**
 * Servicio encargado de autenticar la aplicación ante la API de Spotify utilizando el flujo Client Credentials.
 * Solicita un token de acceso que permite realizar llamadas a endpoints públicos de Spotify.
 *
 * @property clientId ID de cliente registrado en Spotify, inyectado desde la configuración.
 * @property clientSecret Clave secreta del cliente de Spotify.
 * @property tokenUrl URL del endpoint de autenticación de Spotify.
 */
@Service
class SpotifyAuthService {

    @Value("\${spotify.api.clientId}")
    lateinit var clientId: String

    @Value("\${spotify.api.clientSecret}")
    lateinit var clientSecret: String

    @Value("\${spotify.api.tokenUrl}")
    lateinit var tokenUrl: String

    /**
     * Solicita un token de acceso a Spotify usando el flujo de credenciales del cliente.
     *
     * @return Token de acceso (access_token) como [String], necesario para autenticar peticiones a la API de Spotify.
     */
    fun obtenerTokenDeAcceso(): String {

        // println("clientId: $clientId")
        // println("clientSecret: $clientSecret")
        // println("tokenUrl: $tokenUrl")

        val restTemplate = RestTemplate()

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            val auth = Base64.getEncoder()
                .encodeToString("$clientId:$clientSecret".toByteArray())
            set("Authorization", "Basic $auth")

            println("Authorization header: Basic $auth")
        }

        val body = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "client_credentials")
        }

        val request = HttpEntity<MultiValueMap<String, String>>(body, headers)

        val response = restTemplate.postForEntity(tokenUrl, request, Map::class.java)

        return response.body?.get("access_token") as String
    }
}