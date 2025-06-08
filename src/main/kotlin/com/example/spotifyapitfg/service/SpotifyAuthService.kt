package com.example.spotifyapitfg.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class SpotifyAuthService {

    @Value("\${spotify.api.clientId}")
    lateinit var clientId: String

    @Value("\${spotify.api.clientSecret}")
    lateinit var clientSecret: String

    @Value("\${spotify.api.tokenUrl}")
    lateinit var tokenUrl: String

    fun obtenerTokenDeAcceso(): String {

        println("clientId: $clientId")
        println("clientSecret: $clientSecret")
        println("tokenUrl: $tokenUrl")

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