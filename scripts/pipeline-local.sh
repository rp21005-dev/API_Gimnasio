#!/usr/bin/env bash
# Ejecuta en local, con act, el pipeline completo de .github/workflows/ci-cd.yml:
# pruebas -> imagen de Docker -> despliegue en el clúster kind local.
# Uso (desde la raíz del proyecto): ./scripts/pipeline-local.sh
# Requisitos: Docker, kind, kubectl, act y el clúster creado con ./scripts/k8s-cluster.sh
set -euo pipefail

cd "$(dirname "$0")/.."
RAIZ="$(pwd)"

for herramienta in docker kind kubectl act; do
    command -v "$herramienta" >/dev/null || { echo "Falta '$herramienta'. Ver KUBERNETES.md (Requisitos)."; exit 1; }
done

if [ ! -f .kube-local/config ]; then
    echo "No existe .kube-local/config. Ejecutando ./scripts/k8s-cluster.sh..."
    ./scripts/k8s-cluster.sh
fi

# Secretos del pipeline (equivalen a los GitHub Secrets). Se generan una sola vez
# con valores aleatorios y no se suben al repositorio (.secrets está en .gitignore).
if [ ! -f .secrets ]; then
    umask 077
    cat > .secrets <<SECRETOS
DB_USER=gimnasio
DB_PASSWORD=$(openssl rand -hex 16)
JWT_SECRET=$(openssl rand -base64 48 | tr -d '\n')
POSTGRES_DB=gimnasio
SECRETOS
    echo "Secretos generados en .secrets"
fi

# --env-file /dev/null: no pasar el .env local (credenciales de desarrollo) al runner
# -v .kube-local:/kube-local: enlaza la configuración del clúster con el runner
act workflow_dispatch \
    -W .github/workflows/ci-cd.yml \
    -P ubuntu-latest=catthehacker/ubuntu:act-latest \
    --secret-file .secrets \
    --env-file /dev/null \
    --container-options "-v ${RAIZ}/.kube-local:/kube-local:ro" \
    "$@"

echo
echo "Despliegue listo. La API está en http://localhost:30080/gimnasioAPI"
echo "  KUBECONFIG=.kube-local/config kubectl get pods"
