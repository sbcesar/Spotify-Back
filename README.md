**notas de artista**

val id: String, // mismo que Spotify

val nombre: String, // name

val imagenUrl: String?, // images[0].url

val popularidad: Int, // popularity

val seguidores: Int, // followers.total

val urlSpotify: String, // external_urls.spotify

val generos: List<String> // genres

atributo de interés -> val uriSpotify: String // Por ejemplo: "spotify:artist:0TnOYISbd1XYRBk9myaseg"
es un atributo que aplica deep linking, esto significa que me es util para que me abra el link no solo en la web, si no en la misma app de spotify
su uso seria el siguiente -> launchUrl(Uri.parse(uriSpotify));
pero en la interfaz, es decir, usando dart

**notas de cancion**

val id: String, // ️ ID del track (cancion.id)

val nombre: String,// ️ Nombre de la canción (cancion.name)

val artista: String,// ️ Nombre del primer artista (cancion.artists[0].name)

val album: String, // ️ Nombre del álbum (cancion.album.name)

val imagenUrl: String?,// ️ Portada del álbum (cancion.album.images[0].url)

val duracionMs: Int, // Duración (cancion.duration_ms)

val previewUrl: String?, // URL de previsualización (cancion.preview_url)

val popularidad: Int,// Popularidad (cancion.popularity)

val urlSpotify: String // Enlace a la canción en Spotify (cancion.external_urls.spotify)

**flujo del funcionamiento**

[FRONTEND] Usuario se registra/loguea con Firebase

↓

[BACKEND] Recibe token → lo valida con Firebase → obtiene UID

↓

Busca (o crea) al Usuario en MongoDB

↓

Usuario busca canción → backend consulta a Spotify Web API

↓

Usuario da like → backend guarda el objeto Cancion (si no existe) y lo agrega a `likedCanciones` del Usuario




