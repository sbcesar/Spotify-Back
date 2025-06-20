package com.example.spotifyapitfg.service

import com.example.spotifyapitfg.error.exception.NotFoundException
import com.example.spotifyapitfg.models.Album
import com.example.spotifyapitfg.models.Artista
import com.example.spotifyapitfg.models.Cancion
import com.example.spotifyapitfg.models.Playlist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

/**
 * Servicio encargado de interactuar con la API pública de Spotify para realizar búsquedas de
 * canciones, álbumes, artistas y playlists, así como obtener detalles de recursos por ID.
 *
 * Utiliza el flujo de autenticación de cliente y los endpoints REST de Spotify.
 *
 * @property authService Servicio que proporciona tokens de acceso para autenticar peticiones a Spotify.
 * @property searchUrl URL base de búsqueda de Spotify, definida en las propiedades del proyecto.
 */
@Service
class SpotifySearchService {

    @Autowired
    private lateinit var authService: SpotifyAuthService

    @Value("\${spotify.api.searchUrl}")
    lateinit var searchUrl: String

    private val restTemplate = RestTemplate()

    /**
     * Busca una canción en Spotify por su ID.
     *
     * @param id ID de la canción en Spotify.
     * @return [Cancion] con los datos recuperados de la API.
     */
    fun buscarCancionPorId(id: String): Cancion {
        val token = authService.obtenerTokenDeAcceso()
        val url = "https://api.spotify.com/v1/tracks/$id"

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java)
        val track = response.body ?: throw RuntimeException("No se encontró la canción")

        return parseCancion(track as Map<*, *>)
    }

    /**
     * Recupera una playlist desde Spotify y la adapta al modelo local.
     * Este metodo se utiliza cuando la playlist no existe localmente, pero ha sido marcada como "like".
     *
     * @param playlistId ID de la playlist en Spotify.
     * @param headers Encabezados HTTP con token de autenticación.
     * @return [Playlist] con estructura local.
     */
    fun buscarPlaylistSpotifyComoLocal(playlistId: String, headers: HttpHeaders): Playlist {
        val playlistUrl = "https://api.spotify.com/v1/playlists/$playlistId"
        val playlistEntity = HttpEntity<String>(headers)

        val playlistResponse = restTemplate.exchange(
            playlistUrl,
            HttpMethod.GET,
            playlistEntity,
            Map::class.java
        )

        val playlistBody = playlistResponse.body ?: throw NotFoundException("No se encontró la playlist")

        val nombre = playlistBody["name"] as? String ?: "Playlist externa"
        val descripcion = playlistBody["description"] as? String ?: "Playlist obtenida desde Spotify"

        val canciones = obtenerCancionesDePlaylist(playlistId, headers)

        return Playlist(
            id = playlistId,
            nombre = nombre,
            descripcion = descripcion,
            canciones = canciones,
            creadorId = "spotify",
            creadorNombre = "Spotify",
            imagenUrl = canciones.firstOrNull()?.imagenUrl ?: ""
        )
    }

    /**
     * Busca canciones que coincidan con el término dado.
     *
     * @param query Término de búsqueda.
     * @return Lista de [Cancion] que coinciden con la consulta.
     */
    fun buscarCanciones(query: String): List<Cancion> {

        // Obtiene un token para poder usar el endpoint
        val token = authService.obtenerTokenDeAcceso()

        // Creo la uri limitando el resultado a 10 canciones
        val uri = UriComponentsBuilder
            .fromHttpUrl(searchUrl)
            .queryParam("q", query)
            .queryParam("type", "track")
            .queryParam("limit", 10)
            .build().toUri()

        // Configuro el header para que me autorice la api
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)

        // Lanza la peticion mapeando el body de la respuesta (Map)
        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map::class.java)
        // Extrae la lista
        val tracks = ((response.body?.get("tracks") as? Map<*, *>)?.get("items") as? List<*>) ?: return emptyList()

        return tracks
            .filterIsInstance<Map<*, *>>()
            .filter { it["type"] == "track" }
            .mapNotNull { parseCancion(it) }
    }

    private fun parseCancion(data: Map<*, *>): Cancion {
        return try {
            val id = data["id"] as String
            val nombre = data["name"] as String
            val duracionMs = data["duration_ms"] as Int
            val popularidad = data["popularity"] as? Int ?: 0
            val previewUrl = data["preview_url"] as? String
            val urlSpotify = (data["external_urls"] as Map<*, *>)["spotify"] as String

            val artista = ((data["artists"] as? List<*>)?.firstOrNull() as? Map<*, *>)?.get("name") as? String ?: "Desconocido"

            val albumMap = data["album"] as Map<*, *>
            val albumNombre = albumMap["name"] as String
            val imagenUrl = (albumMap["images"] as List<*>?)?.firstOrNull()?.let {
                (it as Map<*, *>)["url"] as? String
            }

            val audioUrl: String? = null

            Cancion(
                id = id,
                nombre = nombre,
                artista = artista,
                album = albumNombre,
                imagenUrl = imagenUrl,
                duracionMs = duracionMs,
                previewUrl = previewUrl,
                popularidad = popularidad,
                urlSpotify = urlSpotify,
                audioUrl = audioUrl
            )
        } catch (e: Exception) {
            println("Error al parsear canción: ${e.message}")
            println("➡️ Data recibida: $data")
            throw RuntimeException("Error al parsear canción")
        }
    }

    /**
     * Busca un álbum por su ID en Spotify.
     *
     * @param id ID del álbum.
     * @return [Album] con información básica.
     */
    fun buscarAlbumPorId(id: String): Album {
        val token = authService.obtenerTokenDeAcceso()
        val url = "https://api.spotify.com/v1/albums/$id"

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java)
        val data = response.body ?: throw RuntimeException("No se encontró el álbum")

        return parseAlbum(data as Map<*, *>)
    }

    /**
     * Busca álbumes en Spotify que coincidan con el término dado.
     *
     * @param query Término de búsqueda.
     * @return Lista de [Album] encontrados.
     */
    fun buscarAlbumes(query: String): List<Album> {
        val token = authService.obtenerTokenDeAcceso()

        val uri = UriComponentsBuilder.fromHttpUrl(searchUrl)
            .queryParam("q", query)
            .queryParam("type", "album")
            .queryParam("limit", 10)
            .build().toUri()

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map::class.java)

        val albums = (response.body?.get("albums") as? Map<*, *>)?.get("items") as? List<Map<*, *>>
            ?: return emptyList()

        return albums.map { parseAlbum(it) }
    }

    private fun parseAlbum(data: Map<*, *>): Album {
        val id = data["id"] as String
        val nombre = data["name"] as String
        val urlSpotify = (data["external_urls"] as Map<*, *>)["spotify"] as String
        val imagenUrl = (data["images"] as List<*>).firstOrNull()?.let {
            (it as Map<*, *>)["url"] as? String
        }
        val artistas = (data["artists"] as List<*>).mapNotNull {
            (it as? Map<*, *>)?.get("name") as? String
        }

        return Album(
            id = id,
            nombre = nombre,
            imagenUrl = imagenUrl,
            fechaLanzamiento = data["release_date"] as? String ?: "Desconocida",
            tipo = data["album_type"] as? String ?: "Desconocido",
            totalCanciones = (data["total_tracks"] as? Int) ?: 0,
            popularidad = 0, // No viene en el endpoint de álbumes
            urlSpotify = urlSpotify,
            artistas = artistas,
            canciones = emptyList() // No las cargamos aquí
        )
    }

    /**
     * Busca un artista en Spotify por su ID.
     *
     * @param id ID del artista.
     * @return [Artista] con los datos obtenidos.
     */
    fun buscarArtistaPorId(id: String): Artista {
        val token = authService.obtenerTokenDeAcceso()
        val url = "https://api.spotify.com/v1/artists/$id"

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java)
        val data = response.body ?: throw RuntimeException("No se encontró el artista")

        return parseArtista(data as Map<*, *>)
    }

    /**
     * Busca artistas en Spotify que coincidan con el término dado.
     *
     * @param query Término de búsqueda.
     * @return Lista de [Artista] encontrados.
     */
    fun buscarArtistas(query: String): List<Artista> {
        val token = authService.obtenerTokenDeAcceso()

        val uri = UriComponentsBuilder.fromHttpUrl(searchUrl)
            .queryParam("q", query)
            .queryParam("type", "artist")
            .queryParam("limit", 10)
            .build().toUri()

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map::class.java)

        val artists = (response.body?.get("artists") as? Map<*, *>)?.get("items") as? List<Map<*, *>>
            ?: return emptyList()

        return artists.map { parseArtista(it) }
    }

    private fun parseArtista(data: Map<*, *>): Artista {
        val id = data["id"] as String
        val nombre = data["name"] as String
        val urlSpotify = (data["external_urls"] as Map<*, *>)["spotify"] as String
        val popularidad = (data["popularity"] as? Int) ?: 0
        val seguidores = ((data["followers"] as? Map<*, *>)?.get("total") as? Int) ?: 0
        val imagenUrl = (data["images"] as List<*>).firstOrNull()?.let {
            (it as Map<*, *>)["url"] as? String
        }
        val generos = (data["genres"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()

        return Artista(
            id = id,
            nombre = nombre,
            imagenUrl = imagenUrl,
            popularidad = popularidad,
            seguidores = seguidores,
            urlSpotify = urlSpotify,
            generos = generos
        )
    }

    /**
     * Busca una playlist en Spotify por su ID.
     *
     * @param id ID de la playlist.
     * @return [Playlist] con los datos obtenidos.
     */
    fun buscarPlaylistPorId(id: String): Playlist {
        val token = authService.obtenerTokenDeAcceso()
        val url = "https://api.spotify.com/v1/playlists/$id"

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)

        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java)
        val data = response.body ?: throw RuntimeException("No se encontró la playlist")

        return parsePlaylist(data as Map<*, *>, headers)
    }

    /**
     * Busca playlists públicas en Spotify que coincidan con el término dado.
     *
     * @param query Término de búsqueda.
     * @return Lista de [Playlist] encontradas.
     */
    fun buscarPlaylists(query: String): List<Playlist> {
        val token = authService.obtenerTokenDeAcceso()

        val uri = UriComponentsBuilder.fromHttpUrl("https://api.spotify.com/v1/search")
            .queryParam("q", query)
            .queryParam("type", "playlist")
            .queryParam("limit", 10)
            .build().toUri()

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map::class.java)

        val playlists = ((response.body?.get("playlists") as? Map<*, *>)?.get("items") as? List<*>)
            ?.filterIsInstance<Map<*, *>>()
            ?: return emptyList()

        return playlists.map { parsePlaylist(it, headers) }
    }

    private fun parsePlaylist(data: Map<*, *>, headers: HttpHeaders): Playlist {
        val id = data["id"] as String
        val nombre = data["name"] as String
        val descripcion = data["description"] as? String ?: ""
        val imagenUrl = (data["images"] as? List<*>)?.firstOrNull()?.let {
            (it as? Map<*, *>)?.get("url") as? String
        } ?: ""

        val canciones = obtenerCancionesDePlaylist(id, headers)
        val creadorId = ((data["owner"] as? Map<*, *>)?.get("id") as? String) ?: "Desconocido"
        val creadorNombre = (data["owner"] as Map<*, *>)["display_name"] as? String ?: "Desconocido"

        return Playlist(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            canciones = canciones,
            creadorId = creadorId,
            creadorNombre = creadorNombre,
            imagenUrl = imagenUrl
        )
    }

    /**
     * Obtiene las canciones incluidas en una playlist específica de Spotify.
     *
     * @param playlistId ID de la playlist.
     * @param headers Encabezados con autorización.
     * @return Lista de [Cancion] que pertenecen a la playlist.
     */
    fun obtenerCancionesDePlaylist(playlistId: String, headers: HttpHeaders): List<Cancion> {
        val uri = "https://api.spotify.com/v1/playlists/$playlistId/tracks"
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map::class.java)

        val items = response.body?.get("items") as? List<Map<*, *>> ?: return emptyList()

        return items.mapNotNull { item ->
            val track = item["track"] as? Map<*, *> ?: return@mapNotNull null
            parseCancion(track)
        }
    }


}