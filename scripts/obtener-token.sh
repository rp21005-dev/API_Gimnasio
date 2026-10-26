#!/usr/bin/env bash
# Inicia sesión en la API y muestra solo el token JWT.
# Uso: TOKEN=$(./scripts/obtener-token.sh)                      (administrador de demostración)
#      TOKEN=$(./scripts/obtener-token.sh correo@ejemplo.com clave1234)
set -euo pipefail

correo="${1:-admin@gimnasio.com}"
contrasena="${2:-clave1234}"
api="${API_URL:-http://localhost:8080/gimnasioAPI}"

respuesta=$(curl -s -X POST "$api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"correo\": \"$correo\", \"contrasena\": \"$contrasena\"}")

token=$(echo "$respuesta" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
if [ -z "$token" ]; then
    echo "No se pudo iniciar sesión: $respuesta" >&2
    exit 1
fi
echo "$token"
