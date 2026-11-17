Pasos para reproducir el job localmente. Es básicamente el mismo flujo del workflow, pero ejecutado en tu máquina.
Requisitos
- Docker corriendo.
- kind: brew install kind
- kubectl: ya trae uno Docker Desktop en ~/.docker/bin/kubectl. Agrégalo al PATH:
export PATH="$HOME/.docker/bin:$PATH"
Verifica: kind version y kubectl version --client.

1. Crear el clúster efímero
kind create cluster --name gimnasio
kind deja el contexto de kubectl apuntando a kind-gimnasio.

2. Construir y cargar la imagen
cd /Users/jm/Documents/ues/API_Gimnasio
docker build -t api-gimnasio:1.0 .
kind load docker-image api-gimnasio:1.0 --name gimnasio

3. Crear el Secret con los valores de GitHub Secrets
Usa los mismos valores que guardaste en GitHub (no los escribas en los manifiestos):
export DB_USER=gimnasio
export DB_PASSWORD=d6a9209f2fae573c1f2f9ec1fc31b667
export JWT_SECRET=5MODQf3RJXLtmU2Oj2bUxA3bsI1WTwcIsViJgH7Fm3pgLwjnjVsqUhmTPrTUVLNM
export POSTGRES_DB=gimnasio

kubectl create secret generic gimnasio-secret \
  --from-literal=DB_USER="$DB_USER" \
  --from-literal=DB_PASSWORD="$DB_PASSWORD" \
  --from-literal=JWT_SECRET="$JWT_SECRET" \
  --from-literal=POSTGRES_DB="$POSTGRES_DB" \
  --dry-run=client -o yaml | kubectl apply -f -

4. Desplegar PostgreSQL y la API
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/postgres.yaml
kubectl rollout status deployment/postgres --timeout=180s

kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl rollout status deployment/api-gimnasio --timeout=180s
No apliques kubernetes/secret.example.yaml: el Secret real se crea en el paso 3.

5. Probar el login
kubectl port-forward svc/gimnasio-service 8080:8080 &
sleep 5
curl -s -X POST http://localhost:8080/gimnasioAPI/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@gimnasio.com","contrasena":"clave1234"}'
Debe devolver el token y el rol. Otros comandos útiles:
kubectl get pods
kubectl logs -f deployment/api-gimnasio
kubectl get svc

6. Limpiar
kind delete cluster --name gimnasio
Notas
- No necesitas el .env para este despliegue: los valores los toma el Secret y el ConfigMap.
- kind crea un nodo con Docker; el PVC usa el StorageClass por defecto de kind, así que se binding automático.
- Si la API tarda en arrancar, es porque espera a PostgreSQL; las sondas y los rollout status lo controlan.