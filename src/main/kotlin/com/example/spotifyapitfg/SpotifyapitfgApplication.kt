package com.example.spotifyapitfg

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream

@SpringBootApplication
class SpotifyapitfgApplication

fun main(args: Array<String>) {

	val serviceAccount = FileInputStream("src/main/resources/spotifyapitfg-firebase-adminsdk-fbsvc-a00762cadd.json")

	val options = FirebaseOptions.builder()
		.setCredentials(GoogleCredentials.fromStream(serviceAccount))
		.build()

	if (FirebaseApp.getApps().isEmpty()) {
		FirebaseApp.initializeApp(options)
		println("Initialized Firebase App")
	}

	runApplication<SpotifyapitfgApplication>(*args)
}
