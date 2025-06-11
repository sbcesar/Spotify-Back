package com.example.spotifyapitfg.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

/**
 * Clase de configuración de Spring que inicializa la aplicación de Firebase al arrancar el servidor.
 *
 * Esta clase se ejecuta automáticamente cuando se crea el contexto de Spring,
 * y configura la conexión con Firebase utilizando la ruta del archivo de credenciales
 * proporcionada por la variable de entorno `FIREBASE_CONFIG_PATH`.
 *
 * Lanza una excepción si la variable de entorno no está configurada correctamente.
 *
 * Se asegura de que la inicialización solo ocurra una vez si aún no hay una instancia de FirebaseApp.
 */
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