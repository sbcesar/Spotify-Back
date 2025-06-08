package com.example.spotifyapitfg.models

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Album(
    @BsonId
    val id: String?,
    val nombre: String,
    val imagenUrl: String?,
    val fechaLanzamiento: String,
    val tipo: String,
    val totalCanciones: Int,
    val popularidad: Int,
    val urlSpotify: String,
    val artistas: List<String>,
    val canciones: List<Cancion>
)
