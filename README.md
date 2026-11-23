# 🏋️ API_Gimnasio
### API REST para la gestión de usuarios de un gimnasio

API REST desarrollada en Java con Spring Boot para administrar a los **administradores**, **entrenadores**, **miembros**, **clases**, **categorías**, **salas**, **inscripciones** y **asistencias** de un gimnasio. La información se guarda en una base de datos PostgreSQL y el acceso está protegido con inicio de sesión mediante tokens JWT.

El proyecto aplica los temas vistos en la Unidad 6 de la materia: caché con `@Cacheable`, registro de eventos con SLF4J, seguridad con Spring Security y JWT, y pruebas con JUnit 5, Mockito y JaCoCo.

---

## 🎓 Información académica

**Universidad de El Salvador**
**Facultad Multidisciplinaria de Occidente**

- **Asignatura:** Programación Orientada a Objetos (POO)
- **Ciclo:** II/2026
- **Carrera:** Ingeniería en Desarrollo de Software
- **Modalidad:** Educación en Línea

## 👥 Integrantes del equipo

| Nombre completo | Carnet |
|---|:---:|
| Jehosua Abdiel Cañas Tijerino | CT24001 |
| Alexis Jonathan Mazariego Mazariego | MM24002 |
| Joseline Rosibel Aldana Aldana | AA13081 |
| Jorge Mario Meléndez | MC25066 |
| Jason Isaac Rodríguez Pérez | RP21005 |

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| **Java + Spring Boot** | Lenguaje y framework principal de la API. |
| **Spring Data JPA** | Guardar y consultar los datos en la base de datos. |
| **PostgreSQL** | Base de datos. |
| **Spring Security + JWT** | Inicio de sesión y protección de los endpoints. |
| **Spring Cache** | Guardar en memoria las consultas por ID. |
| **SLF4J (Lombok `@Slf4j`)** | Registro de eventos (logs). |
| **JUnit 5, Mockito y JaCoCo** | Pruebas y reporte de cobertura. |
| **Lombok** | Generar getters, setters y constructores. |
| **Gradle** | Compilar y ejecutar el proyecto. |
| **Docker** | Contenedor de la API y de PostgreSQL para despliegue local. |
| **GitHub Actions + act** | Pipeline CI/CD (pruebas, imagen y despliegue), también ejecutable en local con act. |
| **Kubernetes (kind) + kubectl** | Despliegue local de la API y PostgreSQL (`kubernetes/`). |

---

## 🧩 Estructura del proyecto

El código está organizado en capas dentro de `src/main/java/gimnasio`:

| Paquete | Contenido |
|---|---|
| `model` | Entidades. `Usuario` es la clase padre (correo, contraseña y estado) y de ella heredan `Administrador`, `Entrenador` y `Miembro` (herencia `JOINED`: los datos comunes van en la tabla `usuario` y los propios de cada tipo en su tabla). `Clase` guarda el tipo, el horario, los días, el cupo, el estado y las relaciones con `Categoria`, `Sala` y `Entrenador`. Además existen `Categoria`, `Sala`, `Inscripcion` y `Asistencia`. |
| `dto` | Objetos para recibir y devolver datos. Las respuestas nunca incluyen la contraseña. |
| `repository` | Repositorios JPA para acceder a la base de datos. |
| `service` | Lógica del negocio: validaciones, caché y logs. |
| `controller` | Endpoints REST. |
| `security` | Generación y validación de tokens JWT. |
| `config` | Configuración de Spring Security, carga de usuarios de demostración y catálogo inicial. |
| `exception` | Manejo de errores y códigos HTTP. |

---

## 🌐 Endpoints

### Inicio de sesión (público)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/gimnasioAPI/auth/login` | Inicia sesión y devuelve un token JWT. Funciona para administradores, entrenadores y miembros. |

Ejemplo:

```json
{ "correo": "ana@ejemplo.com", "contrasena": "clave1234" }
```

Respuesta:

```json
{ "token": "eyJhbGciOi...", "rol": "ENTRENADOR" }
```

Para los demás endpoints se debe enviar el token en la cabecera:

```
Authorization: Bearer <token>
```

### Administradores

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/gimnasioAPI/administrador` | Obtener todos los administradores. |
| `GET` | `/gimnasioAPI/administrador/{id}` | Obtener un administrador por ID. |
| `POST` | `/gimnasioAPI/administrador` | Registrar un administrador. |
| `PUT` | `/gimnasioAPI/administrador/{id}` | Actualizar un administrador. |
| `DELETE` | `/gimnasioAPI/administrador/{id}` | Eliminar un administrador. |

### Entrenadores

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/gimnasioAPI/entrenador` | Obtener todos los entrenadores. |
| `GET` | `/gimnasioAPI/entrenador/{id}` | Obtener un entrenador por ID. |
| `POST` | `/gimnasioAPI/entrenador` | Registrar un entrenador. |
| `PUT` | `/gimnasioAPI/entrenador/{id}` | Actualizar un entrenador. |
| `DELETE` | `/gimnasioAPI/entrenador/{id}` | Eliminar un entrenador. |

### Miembros

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/gimnasioAPI/miembros` | Obtener todos los miembros. |
| `GET` | `/gimnasioAPI/miembros/{id}` | Obtener un miembro por ID. |
| `POST` | `/gimnasioAPI/miembros` | Registrar un miembro (**público**, no necesita token). |
| `PUT` | `/gimnasioAPI/miembros/{id}` | Actualizar un miembro. |
| `DELETE` | `/gimnasioAPI/miembros/{id}` | Eliminar un miembro. |

### Clases

`POST` y `PUT` requieren `categoriaId`, `salaId` y `entrenadorId`. En `POST`, la
`fechaInicio` no puede ser anterior a hoy. El `tipoClase` debe coincidir con uno
de los `tiposClase` de la categoría, el `cupoMaximo` no puede superar la
capacidad de la sala y la clase no puede traslaparse con otra del mismo
entrenador o sala en el mismo día y horario.

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/gimnasioAPI/clases` | Obtener todas las clases. |
| `GET` | `/gimnasioAPI/clases/{id}` | Obtener una clase por ID. |
| `POST` | `/gimnasioAPI/clases` | Registrar una clase. |
| `PUT` | `/gimnasioAPI/clases/{id}` | Actualizar una clase. |
| `DELETE` | `/gimnasioAPI/clases/{id}` | Eliminar una clase. |

Ejemplo:

```json
{
  "tipoClase": "Zumba",
  "diasSemana": ["Martes", "Jueves"],
  "horaInicio": "18:00:00",
  "horaFin": "19:00:00",
  "fechaInicio": "2026-12-01",
  "cupoMaximo": 15,
  "descripcion": "Clase de zumba.",
  "categoriaId": 1,
  "salaId": 1,
  "entrenadorId": 2
}
```

### Categorías

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/gimnasioAPI/categorias` | Obtener todas las categorías. |
| `GET` | `/gimnasioAPI/categorias/{id}` | Obtener una categoría por ID. |
| `POST` | `/gimnasioAPI/categorias` | Registrar una categoría. |
| `PUT` | `/gimnasioAPI/categorias/{id}` | Actualizar una categoría. |
| `DELETE` | `/gimnasioAPI/categorias/{id}` | Eliminar una categoría. |

Ejemplo:

```json
{
  "nombre": "Funcional",
  "objetivoPrincipal": "Mejorar la condición física general.",
  "tiposClase": ["HIIT", "Circuitos", "Core"]
}
```

### Salas

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/gimnasioAPI/salas` | Obtener todas las salas. |
| `GET` | `/gimnasioAPI/salas/{id}` | Obtener una sala por ID. |
| `POST` | `/gimnasioAPI/salas` | Registrar una sala. |
| `PUT` | `/gimnasioAPI/salas/{id}` | Actualizar una sala. |
| `DELETE` | `/gimnasioAPI/salas/{id}` | Eliminar una sala. |

Ejemplo:

```json
{
  "codigo": "S-04",
  "nombre": "Sala de Pesas",
  "ubicacion": "Planta baja",
  "capacidadMaxima": 40,
  "descripcion": "Zona de pesas libres y máquinas."
}
```

### Inscripciones

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/gimnasioAPI/inscripciones` | Inscribir un miembro a una clase. |
| `GET` | `/gimnasioAPI/inscripciones` | Listar inscripciones (opcional: `?miembroId=` o `?claseId=`). |
| `GET` | `/gimnasioAPI/inscripciones/{id}` | Obtener una inscripción por ID. |
| `PUT` | `/gimnasioAPI/inscripciones/{id}` | Cambiar de clase. |
| `DELETE` | `/gimnasioAPI/inscripciones/{id}` | Cancelar una inscripción. |

Ejemplo:

```json
{ "claseId": 1, "miembroId": 3 }
```

### Asistencias

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/gimnasioAPI/asistencias` | Registrar la asistencia de una sesión. |
| `GET` | `/gimnasioAPI/asistencias` | Listar asistencias (opcional: `?miembroId=` o `?claseId=`). |
| `GET` | `/gimnasioAPI/asistencias/{id}` | Obtener una asistencia por ID. |
| `GET` | `/gimnasioAPI/asistencias/clases/{claseId}/sesiones/{fecha}/asistencia` | Hoja de asistencia de una sesión. |
| `PUT` | `/gimnasioAPI/asistencias/{id}` | Corregir una asistencia. |
| `DELETE` | `/gimnasioAPI/asistencias/{id}` | Eliminar un registro de asistencia. |

Ejemplo:

```json
{
  "claseId": 1,
  "fechaSesion": "2026-11-17",
  "registros": [
    { "miembroId": 3, "estado": "PRESENTE", "observacion": "Llegó a tiempo" },
    { "miembroId": 4, "estado": "AUSENTE", "observacion": "Sin aviso" }
  ]
}
```

### Probar con Postman

`API_Gimnasio.postman_collection.json` tiene todas las peticiones, organizadas en 5 carpetas (seguridad, clases, miembros, entrenadores, inscripciones y asistencias).

1. En la aplicación de escritorio de Postman, con sesión iniciada, ir a **My Workspace** → **Import** → **files** y elegir el archivo.
2. En la pestaña **Variables** de la colección, poner en `baseUrl`:
   - `http://localhost:8080/gimnasioAPI` para la API local (Docker o Gradle).
   - `http://localhost:30080/gimnasioAPI` para Kubernetes local (kind).
   - `https://api-production-359d25fa.up.railway.app/gimnasioAPI` para la API publicada en Railway.
3. Ejecutar **1. Seguridad (JWT) → Iniciar sesión como administrador**. El token se guarda solo en la variable `token` y las demás peticiones lo usan automáticamente. Si pasan 24 horas, volver a iniciar sesión.
4. Para probar todo de una vez: **⋯** de la colección → **Run collection** → **Run**.

### Probar con curl

`scripts/obtener-token.sh` inicia sesión y muestra solo el token, para guardarlo en una variable (en Windows, desde Git Bash):

```bash
TOKEN=$(./scripts/obtener-token.sh)                                  # administrador de demostración
TOKEN=$(./scripts/obtener-token.sh miembro@gimnasio.com clave1234)   # otro usuario
```

Luego se envía en la cabecera `Authorization`:

```bash
API=http://localhost:8080/gimnasioAPI

# Listar clases
curl -s $API/clases -H "Authorization: Bearer $TOKEN"

# Crear una clase
curl -s -X POST $API/clases \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"tipoClase": "Zumba", "diasSemana": ["Martes", "Jueves"], "horaInicio": "18:00:00", "horaFin": "19:00:00", "fechaInicio": "2026-12-01", "cupoMaximo": 15, "categoriaId": 1, "salaId": 1, "entrenadorId": 2}'

# Registrar un miembro (público, no necesita token)
curl -s -X POST $API/miembros \
  -H "Content-Type: application/json" \
  -d '{"nombres": "Ana", "apellidos": "López", "correo": "ana@ejemplo.com", "telefono": "7000-1234", "documentoIdentidad": "12345678-0", "fechaNacimiento": "2001-03-10", "contrasenaHash": "clave1234"}'
```

Los demás endpoints funcionan igual: cambiar la ruta y el método (`-X PUT`, `-X DELETE`). Hay ejemplos completos de todos los recursos en **`EJEMPLOS_API.md`**, en la raíz del proyecto.

---

## 📏 Reglas de negocio

1. El correo no se puede repetir entre ningún usuario (administrador, entrenador o miembro), así cada correo inicia sesión con un solo rol.
2. Las contraseñas se guardan encriptadas con BCrypt.
3. Las respuestas de la API nunca muestran la contraseña.
4. Los entrenadores y miembros nuevos se registran con estado `ACTIVO`, y las clases nuevas con estado `ACTIVA` (luego puede cambiar a `CERRADA`).
5. Solo los usuarios con estado `ACTIVO` pueden iniciar sesión.
6. Todos los endpoints, excepto el inicio de sesión y el registro de miembros, necesitan un token válido.
7. El `tipoClase` debe coincidir con uno de los tipos definidos en la categoría.
8. El `cupoMaximo` no puede superar la capacidad de la sala.
9. Una clase no puede traslaparse con otra del mismo entrenador o de la misma sala en el mismo día y horario.
10. En `POST /clases`, la `fechaInicio` no puede ser anterior a hoy.

---

## 📨 Códigos de respuesta HTTP

| Código | Nombre | Cuándo se devuelve |
|:---:|---|---|
| `200` | OK | Consulta o actualización correcta. |
| `201` | Created | Registro creado correctamente. |
| `204` | No Content | Registro eliminado. |
| `400` | Bad Request | Datos inválidos o incompletos. |
| `401` | Unauthorized | No se envió token, el token no es válido o las credenciales son incorrectas. |
| `404` | Not Found | El registro no existe. |
| `409` | Conflict | El correo ya está registrado o hay un traslape de horario. |

---

## 📚 Temas de la Unidad 6 aplicados

### Caché (`@Cacheable`)
La consulta por ID (`GET /{id}`) se guarda en memoria. La primera vez se consulta la base de datos y las siguientes veces se responde desde el caché. Al actualizar o eliminar un registro se usa `@CacheEvict` para borrar el dato del caché y no devolver información desactualizada.

**Cómo comprobarlo:** consultar dos veces el mismo ID y revisar la consola. La consulta SQL y el mensaje `Consultando ... en la base de datos` solo aparecen la primera vez.

### Logs con SLF4J
Los servicios usan `@Slf4j` para registrar eventos con distintos niveles:
- `INFO`: registros creados, actualizados o eliminados.
- `WARN`: correo repetido, intento de eliminar un registro que no existe o traslapes de horario.
- `DEBUG`: consultas a la base de datos.

Los logs se muestran en consola y también se guardan en `logs/gimnasio.log`.

### Spring Security y JWT
El usuario inicia sesión con su correo y contraseña, y la API devuelve un token firmado que dura 24 horas. Un filtro revisa el token en cada petición; si no es válido, la API responde `401`.

### Pruebas y cobertura
76 pruebas con JUnit 5, todas en verde. Se ejecutan sobre una base de datos **H2 en memoria** (configurada en `src/test/resources/application.properties`), así que no necesitan PostgreSQL ni el archivo `.env` y pasan igual en cualquier equipo y en el pipeline.

| Tipo | Clases | Qué prueban |
|---|---|---|
| **Unitarias** (JUnit 5 + Mockito, sin base de datos) | `EntrenadorServiceTest`, `CategoriaServiceTest`, `SalaServiceTest`, `ClaseServiceTest`, `InscripcionServiceTest`, `AsistenciaServiceTest` | Reglas de negocio de cada servicio: duplicados, días y horarios, cupo vs. capacidad de la sala, traslapes, estado del miembro/clase, historial de solo lectura. |
| **Integración** (`@SpringBootTest` + MockMvc) | `EntrenadorControllerIntegrationTest`, `AuthControllerIntegrationTest`, `FlujoGimnasioIntegrationTest` | Login real con JWT por rol, `401` sin token, y el flujo completo categoría → sala → clase → inscripción → asistencia con los códigos `201`, `400`, `404` y `409`. |

- **JaCoCo**: genera el reporte de cobertura en `build/reports/jacoco/test/html/index.html` (≈79 % de instrucciones).

---

## 🚀 Instalación y ejecución

### Requisitos

- Java 17 o superior.
- PostgreSQL instalado y en ejecución (o usar Docker, ver la sección al final).
- Git (en Windows incluye Git Bash, necesario para los scripts).
- Para probar la API: la aplicación de escritorio de Postman, o `curl`.

### 1. Clonar el repositorio

```bash
git clone https://github.com/rp21005-dev/API_Gimnasio.git
cd API_Gimnasio
```

### 2. Iniciar la API (opción rápida)

```bash
./scripts/iniciar.sh
```

El script:

1. Si no existe el `.env`, lo crea a partir de `.env.example`, pide el usuario y la contraseña de PostgreSQL y genera `JWT_SECRET`.
2. Revisa que PostgreSQL esté en ejecución.
3. Crea la base de datos si no existe.
4. Ejecuta `./gradlew bootRun`.

> Los scripts usan `psql`, `pg_isready` y `createdb`, así que es necesario tener las herramientas de PostgreSQL instaladas y en el `PATH`.

### 3. Iniciar la API (opción manual)

Los mismos pasos que hace el script, uno por uno:

#### a. Crear la base de datos

```bash
createdb gimnasio
```

Las tablas se crean automáticamente al ejecutar la aplicación.

> Si ya tenías la base de datos de una versión anterior, límpiala con `./scripts/limpiar-bd.sh` (ver paso 5): el modelo de usuarios cambió y las tablas viejas no son compatibles.

#### b. Crear el archivo `.env`

Copiar el archivo de ejemplo y completar los datos:

```bash
cp .env.example .env
```

| Variable | Descripción |
|---|---|
| `DB_URL` | Dirección de la base de datos. |
| `DB_USER` | Usuario de PostgreSQL. |
| `DB_PASSWORD` | Contraseña de PostgreSQL. |
| `JWT_SECRET` | Clave para firmar los tokens (mínimo 32 caracteres). Se puede generar con `openssl rand -base64 48`. |

> El archivo `.env` no se sube al repositorio porque contiene contraseñas.

#### c. Ejecutar la aplicación

Linux o macOS:

```bash
./gradlew bootRun
```

Windows:

```bat
gradlew.bat bootRun
```

La API queda disponible en `http://localhost:8080`.

Al iniciar, si las tablas están vacías, se crean estos usuarios de demostración (todos con la contraseña `clave1234`), un catálogo de 6 categorías, 3 salas y una clase de ejemplo (Spinning):

| Rol | Correo |
|---|---|
| Administrador | `admin@gimnasio.com` |
| Entrenador | `entrenador@gimnasio.com` |
| Miembro | `miembro@gimnasio.com` |

> Son solo para pruebas locales. Cambiar o eliminar estas cuentas si la API se usa fuera del entorno local.

### 4. Ejecutar las pruebas

```bash
./gradlew test
```

> Las pruebas usan una base de datos H2 en memoria: no hace falta tener PostgreSQL en ejecución ni el archivo `.env`. El resultado queda en `build/reports/tests/test/index.html` y la cobertura en `build/reports/jacoco/test/html/index.html`.

### 5. Limpiar la base de datos (opcional)

Para empezar de cero (por ejemplo, después de un cambio en el modelo):

```bash
./scripts/limpiar-bd.sh
```

El script lee la conexión del `.env`, pide escribir el nombre de la base de datos para confirmar, la borra y la vuelve a crear vacía, y elimina la carpeta `logs/`. Al ejecutar de nuevo `./scripts/iniciar.sh` se crean las tablas y los datos de demostración. En Windows se ejecuta desde Git Bash.

---

## 📄 Licencia

Proyecto desarrollado con fines académicos para la asignatura de Programación Orientada a Objetos de la Universidad de El Salvador.

---

## 🐳 Implementación con Docker

El proyecto incluye un `Dockerfile` de dos etapas (compilación con Gradle y ejecución con JRE 17) y un `docker-compose.yml` que levanta la API junto con PostgreSQL 16.

### Requisitos

- Docker Desktop (o Docker Engine + Compose) instalado y en ejecución.

### 1. Configurar el `.env`

```bash
cp .env.example .env
```

Complete al menos `DB_PASSWORD` y `JWT_SECRET`. No es necesario cambiar `DB_URL`: Compose lo sobrescribe internamente con `jdbc:postgresql://postgres:5432/gimnasio`, porque dentro de la red de Docker la base de datos se llama `postgres`, no `localhost`.

> El archivo `.dockerignore` excluye el `.env` y la carpeta `build/`, así que las credenciales no se copian a la imagen.

### 2. Levantar los contenedores

```bash
docker compose up --build -d
```

Esto construye la imagen `api-gimnasio:1.0`, espera a que PostgreSQL esté sano (healthcheck) y arranca la API.

### 3. Verificar y probar

```bash
docker compose ps                 # estado de los contenedores
docker compose logs -f api        # logs de la API
```

La API queda en `http://localhost:8080`. Por ejemplo:

```bash
curl -s -X POST http://localhost:8080/gimnasioAPI/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@gimnasio.com","contrasena":"clave1234"}'
```

### 4. Detener

```bash
docker compose down        # detiene y conserva la base de datos
docker compose down -v     # detiene y elimina el volumen de datos
```

### Puertos

| Servicio | Contenedor | Host |
|---|---|---|
| API | `8080` | `8080` |
| PostgreSQL | `5432` | `5433` |

El puerto del host para PostgreSQL es `5433` porque el `5432` del equipo puede estar ocupado por otra instancia. La API se conecta igualmente al `5432` del contenedor `postgres` dentro de la red de Compose.

### Variables de entorno de la API

| Variable | Descripción |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexión a PostgreSQL (`DB_URL` en formato JDBC: `jdbc:postgresql://...`). |
| `JWT_SECRET` | Clave para firmar los tokens (mínimo 32 caracteres). |
| `PORT` | Puerto del servidor (por defecto `8080`). Plataformas como Railway o Render lo asignan solas. |
| `DATOS_DEMO` | `true` (por defecto) crea los usuarios de demostración con la contraseña `clave1234`; usar `false` en producción. |

---

## 🔄 CI/CD y Kubernetes

El pipeline está en `.github/workflows/ci-cd.yml` y tiene tres trabajos encadenados:

- `test`: ejecuta las 76 pruebas JUnit (H2 en memoria) y publica los reportes de pruebas y de cobertura.
- `build`: construye la imagen con el `Dockerfile` y, en GitHub, la publica en **GitHub Container Registry (GHCR)**.
- `deploy`: carga la imagen en un clúster **kind**, crea el Secret, despliega PostgreSQL y la API, y prueba el inicio de sesión.

### Ejecutarlo en local con act (Kubernetes local)

```bash
./scripts/k8s-cluster.sh      # crea el clúster kind "gimnasio" y .kube-local/config
./scripts/pipeline-local.sh   # corre test -> build -> deploy con act
```

La API queda en **http://localhost:30080/gimnasioAPI**. El runner de act recibe la configuración del clúster montando `.kube-local/config`, y el `deployment.yaml` usa `imagePullPolicy: Never` porque la imagen se carga directamente en el nodo. Requisitos, detalles y limpieza: ver [`KUBERNETES.md`](KUBERNETES.md).

### Cuándo se ejecuta en GitHub

- Push a `main` o a etiquetas `v*`: prueba, construye, publica y despliega en un kind efímero.
- Pull request a `main`: prueba y construye (no publica ni despliega).
- Ejecución manual desde la pestaña **Actions** (`workflow_dispatch`).

### Imagen generada

```
ghcr.io/<dueño-del-repositorio>/api-gimnasio:<etiqueta>
```

Las etiquetas son el nombre de la rama, la versión (si el push es una etiqueta `v*`) y el SHA corto del commit.

### Secretos de GitHub

| Secreto | Para qué |
|---|---|
| `DB_USER` | Usuario de PostgreSQL y de la API. |
| `DB_PASSWORD` | Contraseña de PostgreSQL y de la API. |
| `JWT_SECRET` | Clave para firmar los tokens (mínimo 32 caracteres). |
| `POSTGRES_DB` | Nombre de la base de datos que crea la imagen de Postgres. |

Se crean en **Settings → Secrets and variables → Actions**. El job `deploy` los inyecta como variables de entorno y crea el `gimnasio-secret` en el clúster con `kubectl create secret ... --from-literal`. GitHub oculta sus valores en los logs.

Para construir y publicar en GHCR no hace falta ningún secreto adicional: se usa el `GITHUB_TOKEN` automático. Como el clúster es efímero (kind), **no** se necesita `KUBE_CONFIG` ni `imagePullSecret`.

### Despliegue educativo (kind)

En GitHub, el job `deploy`:

1. Crea un clúster temporal con `helm/kind-action` (con `kubernetes/kind-config.yaml`).
2. Construye la imagen `api-gimnasio:1.0` y la carga con `kind load docker-image`.
3. Crea el Secret `gimnasio-secret` a partir de los GitHub Secrets.
4. Aplica `kubernetes/configmap.yaml` y `kubernetes/postgres.yaml`, y espera a que PostgreSQL esté listo.
5. Aplica `kubernetes/deployment.yaml` y `kubernetes/service.yaml`, y espera a que la API esté lista.
6. Hace un `port-forward` y prueba `POST /gimnasioAPI/auth/login`.

Los manifiestos están en `kubernetes/`:

| Archivo | Contenido |
|---|---|
| `kind-config.yaml` | Clúster kind `gimnasio`; publica el NodePort `30080` en el equipo. |
| `configmap.yaml` | `DB_URL` apuntando al Service `postgres` y `DATOS_DEMO`. |
| `postgres.yaml` | PVC, Deployment y Service de PostgreSQL 16. |
| `deployment.yaml` | Deployment de la API (imagen `api-gimnasio:1.0`, `imagePullPolicy: Never`, con sondas de salud). |
| `service.yaml` | Service `NodePort` (`8080` → `30080`). |
| `secret.example.yaml` | Plantilla del Secret (no se aplica; el real se crea desde GitHub Secrets). |

### Despliegue en un clúster real

Para apuntar a un clúster real (EKS, GKE, AKS, k3s, etc.):

1. Guardar el kubeconfig en base64 como el secreto `KUBE_CONFIG`.
2. Cambiar la imagen del `deployment.yaml` a la de GHCR, por ejemplo `ghcr.io/rp21005-dev/api-gimnasio:main`.
3. Crear un `imagePullSecret` si el paquete de GHCR es privado y referenciarlo en el `Deployment`.
4. Sustituir el job de kind por un `kubectl apply -f kubernetes/` autenticado con el `KUBE_CONFIG`.

---

## ☁️ Despliegue en Railway

La API y su base de datos PostgreSQL están publicadas en [Railway](https://railway.com):

**URL base:** https://api-production-359d25fa.up.railway.app/gimnasioAPI

```bash
curl -s -X POST https://api-production-359d25fa.up.railway.app/gimnasioAPI/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@gimnasio.com","contrasena":"clave1234"}'
```

**Página web:** https://api-production-359d25fa.up.railway.app/ muestra en el navegador las clases, las categorías y las salas después de iniciar sesión con un usuario de demostración. Es el archivo `src/main/resources/static/index.html` y la sirve la misma API (también en local: http://localhost:8080/).

Para usar la colección de Postman contra Railway, cambiar la variable `baseUrl` de la colección a esa URL.

### Cómo está armado

El proyecto de Railway `api-gimnasio` tiene dos servicios:

| Servicio | Qué es |
|---|---|
| `Postgres` | Base de datos PostgreSQL administrada por Railway. Railway genera el usuario y la contraseña. |
| `api` | La API, construida con el `Dockerfile` del repositorio. |

Variables del servicio `api` (la API se conecta a la base por la red privada de Railway):

| Variable | Valor |
|---|---|
| `DB_URL` | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `DB_USER` | `${{Postgres.PGUSER}}` |
| `DB_PASSWORD` | `${{Postgres.PGPASSWORD}}` |
| `JWT_SECRET` | Clave aleatoria de 96 caracteres (solo está en Railway) |
| `DATOS_DEMO` | `true`: crea los usuarios de demostración. Cambiar a `false` si la API deja de ser solo para presentar. |

`PORT` lo asigna Railway. El contenedor usa la zona horaria `America/El_Salvador` (definida en el `Dockerfile`) para que "hoy" sea el mismo día que en El Salvador aunque el servidor esté en UTC.

### Volver a desplegar

Con el [CLI de Railway](https://docs.railway.com/guides/cli) y acceso al proyecto:

```bash
railway login
railway link --project api-gimnasio
railway up --service api
```
