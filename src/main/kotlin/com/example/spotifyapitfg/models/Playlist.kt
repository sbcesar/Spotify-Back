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
    val creadorId: String   // Uso el id del creador en vez del Creador en si (Usuario) porque se duplica la informacion, si hay cambios en usuario tengo que actualizar todas las playlist, crecen los documentos de la db muchisimo
)