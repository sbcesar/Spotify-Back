package com.example.spotifyapitfg.error.exception

class ConflictException(message : String) : Exception("There was a conflict between the client's request and the resource's current state on the server (409): $message") {
}