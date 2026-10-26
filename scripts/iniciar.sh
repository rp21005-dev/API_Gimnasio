#!/usr/bin/env bash
# Prepara todo y levanta la API:
#   1. Si no existe el .env, lo crea a partir de .env.example y genera JWT_SECRET.
#   2. Revisa que PostgreSQL esté en ejecución.
#   3. Si la base de datos no existe, la crea.
#   4. Ejecuta ./gradlew bootRun.
# Uso (desde la raíz del proyecto): ./scripts/iniciar.sh
# En Windows ejecutarlo desde Git Bash.
set -euo pipefail

cd "$(dirname "$0")/.."

# Cambia (o agrega) una variable en el .env
guardar_variable() {
    grep -v -E "^$1=" .env > .env.tmp || true
    echo "$1=$2" >> .env.tmp
    mv .env.tmp .env
}

if [ ! -f .env ]; then
    echo "No existe el archivo .env; se creará a partir de .env.example."
    cp .env.example .env
    source scripts/leer-env.sh
    read -r -p "Usuario de PostgreSQL [$DB_USER]: " usuario
    read -r -s -p "Contraseña de PostgreSQL (Enter si no tiene): " contrasena
    echo
    DB_USER="${usuario:-$DB_USER}"
    DB_PASSWORD="$contrasena"
    guardar_variable DB_USER "$DB_USER"
    guardar_variable DB_PASSWORD "$DB_PASSWORD"
    guardar_variable JWT_SECRET "$(openssl rand -base64 48 | tr -d '\n')"
    echo "Archivo .env creado. Si la base de datos no está en $DB_URL, edita DB_URL en .env."
fi

source scripts/leer-env.sh

if ! pg_isready -q -h "$host" -p "$puerto"; then
    echo "PostgreSQL no responde en $host:$puerto. Inícialo y vuelve a ejecutar este script."
    exit 1
fi

if psql -h "$host" -p "$puerto" -U "$DB_USER" -d "$base" -c "SELECT 1" > /dev/null 2>&1; then
    echo "La base de datos '$base' ya existe."
else
    echo "Creando la base de datos '$base'..."
    createdb -h "$host" -p "$puerto" -U "$DB_USER" "$base"
fi

echo "Iniciando la API en http://localhost:8080 (Ctrl+C para detenerla)..."
exec ./gradlew bootRun
