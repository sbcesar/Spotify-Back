package com.example.spotifyapitfg.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseInitializer {
    init {
        val path = System.getenv("FIREBASE_CONFIG_PATH")
            ?: throw IllegalStateException("FIREBASE_CONFIG_PATH environment variable not set")

        val serviceAccount = FileInputStream(path)


        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
            println("Initialized Firebase App")
        }

    }
}