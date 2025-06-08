package com.example.spotifyapitfg.models

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Playlist(
    @BsonId
    val id: String?,
    val nombre: String,
    val descripcion: String?,
    val canciones: List<Cancion>,
    val creadorId: String,
    val creadorNombre: String,
    val imagenUrl: String?
)