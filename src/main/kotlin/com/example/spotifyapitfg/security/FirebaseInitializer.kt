package com.example.spotifyapitfg.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseInitializer {
    init {
        val serviceAccount = this::class.java.classLoader.getResourceAsStream("spotifyapitfg-firebase-adminsdk-fbsvc-af89212f87.json")
            ?: throw IllegalStateException("Firebase credentials file not found in classpath")


        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
            println("Initialized Firebase App")
        }

    }
}