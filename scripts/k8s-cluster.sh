#!/usr/bin/env bash
# Crea (si no existe) el clúster local de Kubernetes con kind y guarda su configuración
# en .kube-local/config, que es la que se monta en el runner de act.
# Uso (desde la raíz del proyecto):
#   ./scripts/k8s-cluster.sh            crear el clúster y exportar el kubeconfig
#   ./scripts/k8s-cluster.sh --borrar   eliminar el clúster y el kubeconfig
# Requisitos: Docker, kind y kubectl.
set -euo pipefail

cd "$(dirname "$0")/.."
CLUSTER=gimnasio

for herramienta in docker kind kubectl; do
    command -v "$herramienta" >/dev/null || { echo "Falta '$herramienta'. Ver KUBERNETES.md (Requisitos)."; exit 1; }
done

if [ "${1:-}" = "--borrar" ]; then
    kind delete cluster --name "$CLUSTER"
    rm -rf .kube-local
    echo "Clúster '$CLUSTER' eliminado."
    exit 0
fi

if kind get clusters 2>/dev/null | grep -qx "$CLUSTER"; then
    echo "El clúster '$CLUSTER' ya existe."
else
    kind create cluster --config kubernetes/kind-config.yaml --wait 120s
fi

# kubeconfig solo de este clúster; apunta a 127.0.0.1, que el runner de act alcanza
# porque act usa la red del equipo (--network host)
mkdir -p .kube-local
kind get kubeconfig --name "$CLUSTER" > .kube-local/config
chmod 600 .kube-local/config

KUBECONFIG=.kube-local/config kubectl get nodes
echo
echo "Kubeconfig guardado en .kube-local/config"
echo "Siguiente paso: ./scripts/pipeline-local.sh"
