package com.example.spotifyapitfg.repository

import com.example.spotifyapitfg.models.Cancion
import org.springframework.data.mongodb.repository.MongoRepository

interface CancionRepository : MongoRepository<Cancion, String> {

}