# Music Sound API

---

## Descripción breve

**Music Sound** es una API REST desarrollada en Kotlin + Spring Boot, que utiliza autenticación con Firebase y tokens JWT para asegurar el acceso a recursos. Esta API permite gestionar usuarios y su contenido musical, incluyendo canciones, playlists, artistas y álbumes, así como buscar información musical mediante integración con la Spotify Web API.

Este backend está diseñado para usarse junto a un frontend móvil y prioriza la seguridad, modularidad y claridad en su arquitectura.

---

## Descripción Detallada

Los principales documentos de esta API son almacenados en MongoDB. Una característica destacable es que el campo id en las entidades que representan a usuarios utiliza directamente el uid generado por Firebase durante el proceso de registro/autenticación. Esto permite una integración segura y directa entre el sistema de autenticación y la base de datos.

### Usuario

* id: String – UID generado por Firebase (clave primaria en MongoDB).

* nombre: String – Nombre del usuario.

* email: String – Correo electrónico único.

* playlistCount: Int – Número de playlists creadas.

* seguidores: Int – Número de seguidores.

* seguidos: Int – Número de usuarios seguidos.

* biblioteca: Objeto Biblioteca – Gustos y contenido guardado.

* role: Enum – Rol del usuario (USER o futuro ADMIN).

### Biblioteca

* playlistsCreadas: List – IDs de playlists creadas.

* likedCanciones: List – IDs de canciones marcadas como favoritas.

* likedPlaylists: List – IDs de playlists favoritas.

* likedArtistas: List – IDs de artistas favoritos.

* likedAlbums: List – Álbumes marcados como favoritos.

### Cancion

* id: String – ID único.

* nombre: String – Título.

* artista: String – Artista principal.

* album: String – Álbum de la canción.

* imagenUrl: String – Imagen del álbum.

* duracionMs: Int – Duración en milisegundos.

* previewUrl: String – URL de muestra de Spotify.

* popularidad: Int – Score del 1 al 100.

* urlSpotify: String – Enlace a Spotify.

* audioUrl: String – Enlace a recurso de audio propio (si aplica).

### Playlist

* id: String – Identificador único.

* nombre: String

* descripcion: String

* canciones: List – Canciones en la playlist.

* creadorId: String – UID del creador (vinculado a usuario).

* creadorNombre: String

* imagenUrl: String – Imagen representativa.

### Álbum

* id: String – ID único del álbum.

* nombre: String

* imagenUrl: String

* fechaLanzamiento: String

* tipo: String – Álbum, Single, etc.

* totalCanciones: Int

* popularidad: Int

* urlSpotify: String

* artistas: List – Artistas que participaron.

* canciones: List – Detalle de canciones dentro del álbum.

### Artista

* id: String – ID único.

* nombre: String

* imagenUrl: String

* popularidad: Int

* seguidores: Int

* urlSpotify: String

* generos: List – Géneros musicales del artista.

---

##  Estructura del proyecto

````markdown
src/
└── main/
├── kotlin/
│   └── com.example.spotifyapitfg/
│       ├── controller/           → Controladores REST (endpoints)
│       ├── dto/                  → Clases DTO para intercambio de datos
│       ├── error/
│       │   └── exception/        → Manejo centralizado de errores
│       ├── mapper/               → Mappers entre entidades y DTOs
│       ├── models/               → Documentos MongoDB (entidades)
│       ├── repository/           → Interfaces de acceso a base de datos
│       ├── security/             → Configuración de Spring Security + filtros JWT
│       ├── service/              → Lógica de negocio (servicios)
│       └── SpotifyapitfgApplication.kt  → Clase principal (entry point)
└── resources/
├── documentation/           → Archivos Markdown u otros docs del proyecto
├── static/, templates/      → Carpetas estándar para recursos web
├── .env.properties          → Variables sensibles (privado)
└── application.properties   → Configuración general de Spring Boot
````

---

## Casos de uso

La API REST de Music Sound está diseñada para permitir a usuarios autenticados interactuar con contenido musical de forma personalizada. A continuación, se describen los principales casos de uso:

### Registro y autenticación

* Registrarse mediante email y contraseña, usando Firebase Authentication.

* Iniciar sesión y obtener un token JWT válido.

* Toda interacción posterior requiere autenticación.

### Descubrimiento musical

* Buscar canciones, álbumes, artistas y playlists mediante la API pública de Spotify. [Link Aquí](https://developer.spotify.com/)

* Visualizar detalles completos de una canción, artista, álbum o playlist.

### Biblioteca personalizada
* Dar like a cualquier canción, álbum, artista o playlist disponible.

* Retirar like en cualquier momento.

* Toda esta información se almacena en el documento Biblioteca del usuario en MongoDB.

### Gestión de playlists
* Crear una nueva playlist personalizada con nombre, descripción e imagen.

* Agregar canciones a la playlist desde el catálogo o resultados de búsqueda.

* Editar el nombre, descripción o imagen de una playlist solo si el usuario es su creador.

* Eliminar una playlist propia.

* Eliminar una canción de una playlist propia.

* Dar like o quitar like a playlists de otros usuarios.

### Interacciones restringidas
* Los usuarios solo pueden editar o eliminar sus propias playlists.

* El rol ADMIN (asignado manualmente en base de datos) puede editar o eliminar cualquier playlist.

---

## Arquitectura del proyecto

La aplicación sigue una arquitectura por capas:

`Controller → Service → Repository → Entity/Model`

### 1. Controller

Maneja las solicitudes HTTP entrantes. No contiene lógica de negocio; únicamente delega al servicio correspondiente.

`AlbumController, ArtistaController, CancionController, PlaylistController, UsuarioController, SpotifyController`

### 2. Service

Contiene la lógica de negocio. Interactúa con los repositorios y aplica reglas antes de devolver resultados al controller.

`UsuarioService, FirebaseAuthService, SpotifySearchService, CancionService, PlaylistService, ArtistaService, AlbumService, SpotifyAuthService`

### 3. Repository

Encargada del acceso a datos. Define interfaces que extienden de MongoRepository y permiten realizar operaciones CRUD directamente sobre los documentos de MongoDB sin necesidad de implementar lógica adicional.

`UsuarioRepository, PlaylistRepository, CancionRepository`

---

## Endpoints

### Usuario - Autenticación (/usuario)

POST /register – Registra un nuevo usuario con Firebase.

POST /login – Login usando token JWT desde Firebase.

GET /perfil – Devuelve perfil del usuario autenticado.

GET /biblioteca – Devuelve contenido de la biblioteca del usuario.

### Canciones (/canciones)

GET /{id} – Obtener canción por ID.

GET /all – Obtener todas las canciones.

POST /like/{id} – Marcar como favorita.

DELETE /like/{id} – Quitar de favoritos.

### Álbumes (/albumes)

GET /{id} – Obtener álbum por ID.

POST /like/{id} – Marcar álbum como favorito.

DELETE /like/{id} – Quitar álbum de favoritos.

### Artistas (/artistas)

GET /{id} – Obtener artista por ID.

POST /like/{id} – Marcar artista como favorito.

DELETE /like/{id} – Quitar artista de favoritos.

### Playlists (/playlists)

GET /{id} – Obtener playlist por ID.

GET /todas – Obtener todas las playlists públicas.

GET /creadas – Obtener playlists creadas por el usuario autenticado.

POST /crear – Crear playlist nueva.

PUT /{id}/editar – Editar nombre/desc.

DELETE /{id} – Eliminar playlist.

POST /like/{id} – Marcar como favorita.

DELETE /like/{id} – Quitar de favoritos.

PUT /{id}/agregarCancion/{cancionId} – Añadir canción.

PUT /{id}/eliminarCancion/{cancionId} – Quitar canción.

### Spotify Search (/spotify)

GET /token – Obtener token de acceso.

GET /buscar/canciones?query=...

GET /buscar/albumes?query=...

GET /buscar/artistas?query=...

GET /buscar/playlists?query=...

---

## Excepciones

| Código | Excepción             | Descripción                                                                 |
|--------|-----------------------|-----------------------------------------------------------------------------|
| 400    | BadRequestException   | La solicitud no cumple con los requisitos esperados (datos inválidos, etc). |
| 401    | UnauthorizedException | El usuario no está autenticado (token ausente o inválido).                  |
| 403    | ForbiddenException    | El usuario autenticado no tiene permisos para esta acción.                  |
| 404    | NotFoundException     | El recurso solicitado no existe en el servidor.                             |
| 409    | ConflictException     | Conflicto en la lógica del sistema (e.g. recurso duplicado).                |

---

## Autenticación y seguridad

La API implementa un sistema de autenticación robusto basado en Firebase Authentication, integrando validación de tokens y control de roles, combinado con medidas de protección aplicadas mediante Spring Security.

### Firebase Authentication + Token JWT

El sistema de autenticación está respaldado por Firebase. Cuando un usuario se registra, se ejecutan dos acciones:

1. Se registra el usuario en Firebase Authentication con su email y contraseña.


2. Se crea automáticamente un documento Usuario en MongoDB, utilizando como id el uid generado por Firebase. Esto asegura la integridad entre autenticación y persistencia de datos.

El token de ID de Firebase se utiliza como bearer token en los encabezados de autorización para acceder a rutas protegidas. Este token es validado en cada petición mediante el servicio FirebaseAuthService.

### Validación de acceso

Todas las rutas privadas (perfil, biblioteca, likes, creación/edición de playlists, etc.) requieren el header:

`Authorization: Bearer <ID_TOKEN>`

Si el token es inválido o ha expirado, se lanza una excepción personalizada.

### Control de roles: USER vs ADMIN

El sistema implementa una lógica de roles sencilla:

Por defecto, todos los usuarios registrados obtienen el rol **USER**.

De forma manual, un usuario puede ser asignado como **ADMIN**.

| Rol   | Permisos adicionales                                              |
|-------|-------------------------------------------------------------------|
| USER  | Puede crear, modificar o eliminar **solo sus propias** playlists. |
| ADMIN | Puede modificar y eliminar **cualquier** playlist en el sistema.  |

El control se realiza validando el uid del usuario autenticado contra el creadorId del recurso, y en su defecto, comprobando si el usuario tiene rol ADMIN mediante el servicio FirebaseAuthService.

### Spring Security

La configuración de seguridad se gestiona mediante _SecurityFilterChain_ en la clase _SecurityConfig_. Esto permite interceptar peticiones, autenticar tokens y aplicar filtros globales.

No se almacena ni gestiona manualmente ninguna contraseña: el cifrado y la autenticación se delegan completamente a Firebase.

---

## Extras

[Repositorio Backend](https://github.com/sbcesar/Spotify-Back.git)

[Repositorio Frontend](https://github.com/sbcesar/Spotify-Front.git)

[Mi github](https://github.com/sbcesar)

---