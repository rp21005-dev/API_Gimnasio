# Despliegue en Kubernetes local con act

El pipeline `.github/workflows/ci-cd.yml` se ejecuta en local con [act](https://github.com/nektos/act) y despliega la API en un clúster de Kubernetes local creado con [kind](https://kind.sigs.k8s.io/):

```
act ──► test (JUnit, 84 pruebas) ──► build (docker build api-gimnasio:1.0) ──► deploy (kubectl → kind)
                                                                               │
                         .kube-local/config ──(montado en el runner)───────────┘
```

| Pieza | Dónde |
|---|---|
| Pipeline | `.github/workflows/ci-cd.yml` (el mismo que corre en GitHub Actions) |
| Configuración del clúster | `kubernetes/kind-config.yaml` (clúster `gimnasio`, publica el NodePort `30080`) |
| Kubeconfig del runner | `.kube-local/config` (lo genera `scripts/k8s-cluster.sh`, no se sube al repo) |
| Secretos del pipeline | `.secrets` (lo genera `scripts/pipeline-local.sh` con valores aleatorios, no se sube al repo) |
| Manifiestos | `kubernetes/configmap.yaml`, `postgres.yaml`, `deployment.yaml` (`imagePullPolicy: Never`), `service.yaml` |

## Requisitos

- Docker en ejecución (el usuario debe poder usar `docker` sin `sudo`).
- `kind`, `kubectl` y `act`. En Linux se pueden instalar en `~/.local/bin`:

```bash
mkdir -p ~/.local/bin
curl -fsSLo ~/.local/bin/kind https://github.com/kubernetes-sigs/kind/releases/download/v0.33.0/kind-linux-amd64
curl -fsSLo ~/.local/bin/kubectl https://dl.k8s.io/release/v1.37.2/bin/linux/amd64/kubectl
curl -fsSL https://github.com/nektos/act/releases/download/v0.2.97/act_Linux_x86_64.tar.gz | tar -xz -C ~/.local/bin act
chmod +x ~/.local/bin/kind ~/.local/bin/kubectl
```

En macOS: `brew install kind kubectl act`. Verificar con `kind version`, `kubectl version --client` y `act --version`.

## Pasos

### 1. Crear el clúster local

```bash
./scripts/k8s-cluster.sh
```

Crea el clúster `gimnasio` con `kubernetes/kind-config.yaml` y guarda su kubeconfig en `.kube-local/config`. Si el clúster ya existe, solo vuelve a exportar el kubeconfig.

### 2. Ejecutar el pipeline con act

```bash
./scripts/pipeline-local.sh
```

El script:

1. Genera `.secrets` la primera vez (`DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `POSTGRES_DB`). Son el equivalente local de los GitHub Secrets.
2. Ejecuta `act workflow_dispatch` con la imagen `catthehacker/ubuntu:act-latest` y monta `.kube-local/` en el runner (`-v .kube-local:/kube-local:ro`).
3. No pasa el `.env` local al runner (`--env-file /dev/null`).

Trabajos del pipeline:

| Trabajo | Qué hace en local (act) |
|---|---|
| `test` | Instala Java 21 y ejecuta `./gradlew test` (H2 en memoria). Muestra el total de pruebas. |
| `build` | `docker build -t api-gimnasio:1.0 .` en el Docker del equipo. |
| `deploy` | Usa `KUBECONFIG=/kube-local/config`, carga la imagen con `kind load docker-image`, crea el Secret, aplica los manifiestos, reinicia la API para usar la imagen nueva, espera los `rollout status` y prueba el login. |

El runner de act usa la red del equipo (`--network host`, el valor por defecto), por eso alcanza el API server de kind en `127.0.0.1`.

### 3. Usar la API desplegada

La API queda publicada en **http://localhost:30080/gimnasioAPI** (NodePort `30080`):

```bash
curl -s -X POST http://localhost:30080/gimnasioAPI/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@gimnasio.com","contrasena":"clave1234"}'
```

Comandos útiles:

```bash
export KUBECONFIG=.kube-local/config
kubectl get pods,svc
kubectl logs -f deployment/api-gimnasio
kubectl rollout restart deployment/api-gimnasio
```

Para volver a desplegar después de un cambio en el código, ejecutar otra vez `./scripts/pipeline-local.sh`.

### 4. Limpiar

```bash
./scripts/k8s-cluster.sh --borrar
```

## Notas

- **`imagePullPolicy: Never`**: la imagen `api-gimnasio:1.0` nunca se descarga de un registro; el pipeline la carga en el nodo con `kind load docker-image`. Como la etiqueta no cambia, el paso de despliegue hace `kubectl rollout restart` para que los pods usen la imagen recién cargada.
- **No se escriben credenciales en los manifiestos.** El Secret `gimnasio-secret` se crea en el pipeline a partir de `.secrets` (local) o de los GitHub Secrets. `kubernetes/secret.example.yaml` es solo una plantilla y no debe aplicarse.
- **En GitHub Actions** el mismo workflow publica la imagen en GHCR y despliega en un clúster kind efímero creado con `helm/kind-action`; los pasos solo para act usan la condición `env.ACT`.
- PostgreSQL guarda los datos en el PVC `postgres-pvc` (StorageClass por defecto de kind), así que se conservan entre despliegues hasta borrar el clúster.
