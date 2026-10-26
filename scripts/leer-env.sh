# Lee la conexión a PostgreSQL del .env (sin ejecutarlo) y deja listas
# las variables host, puerto, base, DB_USER y PGPASSWORD.
# Las variables de entorno tienen prioridad, igual que en Spring Boot.
# Se usa con: source scripts/leer-env.sh

leer_variable() {
    [ -f .env ] || return 0
    grep -E "^$1=" .env | tail -n 1 | cut -d= -f2- | tr -d '\r"' || true
}

DB_URL="${DB_URL:-$(leer_variable DB_URL)}"
DB_URL="${DB_URL:-jdbc:postgresql://localhost:5432/gimnasio}"
DB_USER="${DB_USER:-$(leer_variable DB_USER)}"
DB_USER="${DB_USER:-postgres}"
export PGPASSWORD="${DB_PASSWORD:-$(leer_variable DB_PASSWORD)}"

# jdbc:postgresql://host:puerto/base?parametros -> host, puerto y base
resto="${DB_URL#jdbc:postgresql://}"
servidor="${resto%%/*}"
base="${resto#*/}"
base="${base%%\?*}"
host="${servidor%%:*}"
puerto="5432"
[[ "$servidor" == *:* ]] && puerto="${servidor##*:}"
