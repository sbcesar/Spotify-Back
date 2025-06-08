package com.example.spotifyapitfg.error.exception

class ForbiddenException(message: String) : Exception("You dont have permission to access this resource (403): $message") {
}