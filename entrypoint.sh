#!/bin/sh

# Escribimos el JSON en un archivo a partir de la variable de entorno
echo "$FIREBASE_CONFIG" > $FIREBASE_CONFIG_PATH

# Luego ejecutamos la aplicación
exec java -jar /app/app.war
