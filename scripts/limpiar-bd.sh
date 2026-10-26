#!/usr/bin/env bash
# Borra y vuelve a crear la base de datos local, y elimina los logs.
# Al iniciar la API de nuevo se crean las tablas y los datos de demostración.
# Uso (desde la raíz del proyecto): ./scripts/limpiar-bd.sh
# En Windows ejecutarlo desde Git Bash.
set -euo pipefail

cd "$(dirname "$0")/.."
source scripts/leer-env.sh

echo "Se borrarán TODOS los datos de la base '$base' en $host:$puerto (usuario $DB_USER)."
read -r -p "Escribe el nombre de la base de datos para confirmar: " respuesta
if [ "$respuesta" != "$base" ]; then
    echo "Cancelado: el nombre no coincide."
    exit 1
fi

dropdb --if-exists -h "$host" -p "$puerto" -U "$DB_USER" "$base"
createdb -h "$host" -p "$puerto" -U "$DB_USER" "$base"
rm -rf logs

echo "Listo: la base '$base' está vacía y los logs se borraron."
echo "Ejecuta ./scripts/iniciar.sh para crear las tablas y los datos de demostración."
