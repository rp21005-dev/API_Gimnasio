# 🏋️ API_Gimnasio
### API REST para la gestión de usuarios de un gimnasio

API REST desarrollada en Java con Spring Boot para administrar a los **administradores**, **entrenadores**, **miembros** y **clases** de un gimnasio. La información se guarda en una base de datos PostgreSQL y el acceso está protegido con inicio de sesión mediante tokens JWT.

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

---

## 🧩 Estructura del proyecto

El código está organizado en capas dentro de `src/main/java/gimnasio`:

| Paquete | Contenido |
|---|---|
| `model` | Entidades. `Usuario` es la clase padre (correo, contraseña y estado) y de ella heredan `Administrador`, `Entrenador` y `Miembro` (herencia `JOINED`: los datos comunes van en la tabla `usuario` y los propios de cada tipo en su tabla). `Clase` guarda el horario, los días y el cupo de cada clase. |
| `dto` | Objetos para recibir y devolver datos. Las respuestas nunca incluyen la contraseña. |
| `repository` | Repositorios JPA para acceder a la base de datos. |
| `service` | Lógica del negocio: validaciones, caché y logs. |
| `controller` | Endpoints REST. |
| `security` | Generación y validación de tokens JWT. |
| `config` | Configuración de Spring Security y carga de usuarios de demostración. |
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
  "tipoClase": "Spinning",
  "diasSemana": ["Lunes", "Miércoles", "Viernes"],
  "horaInicio": "08:00:00",
  "horaFin": "09:00:00",
  "fechaInicio": "2026-11-01",
  "cupoMaximo": 20,
  "descripcion": "Clase de cardio de alta intensidad en bicicleta fija."
}
```

### Probar con Postman

En `postman/collections/API_Gimnasio` hay una colección lista para usar:

1. Abrir la carpeta `postman/` en la **aplicación de escritorio** de Postman (Windows, macOS o Linux). La versión web no puede llamar a `localhost` sin instalar el Postman Desktop Agent.
2. Ejecutar **Iniciar sesión** (usa el administrador de demostración). El token se guarda solo en la variable `token` de la colección.
3. Las demás peticiones heredan ese token, así que no hay que copiarlo a mano. Si pasan 24 horas, volver a ejecutar **Iniciar sesión**.

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
  -d '{"tipoClase": "Yoga", "diasSemana": ["Martes", "Jueves"], "horaInicio": "18:00:00", "horaFin": "19:00:00", "fechaInicio": "2026-11-03", "cupoMaximo": 15}'

# Registrar un miembro (público, no necesita token)
curl -s -X POST $API/miembros \
  -H "Content-Type: application/json" \
  -d '{"nombres": "Ana", "apellidos": "López", "correo": "ana@ejemplo.com", "telefono": "7000-1234", "documentoIdentidad": "12345678-0", "fechaNacimiento": "2001-03-10", "contrasenaHash": "clave1234"}'
```

Los demás endpoints funcionan igual: cambiar la ruta y el método (`-X PUT`, `-X DELETE`).

---

## 📏 Reglas de negocio

1. El correo no se puede repetir entre ningún usuario (administrador, entrenador o miembro), así cada correo inicia sesión con un solo rol.
2. Las contraseñas se guardan encriptadas con BCrypt.
3. Las respuestas de la API nunca muestran la contraseña.
4. Los entrenadores y miembros nuevos se registran con estado `ACTIVO`, y las clases nuevas con estado `ACTIVA` (luego puede cambiar a `CERRADA`).
5. Solo los usuarios con estado `ACTIVO` pueden iniciar sesión.
6. Todos los endpoints, excepto el inicio de sesión y el registro de miembros, necesitan un token válido.

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
| `409` | Conflict | El correo ya está registrado. |

---

## 📚 Temas de la Unidad 6 aplicados

### Caché (`@Cacheable`)
La consulta por ID (`GET /{id}`) se guarda en memoria. La primera vez se consulta la base de datos y las siguientes veces se responde desde el caché. Al actualizar o eliminar un registro se usa `@CacheEvict` para borrar el dato del caché y no devolver información desactualizada.

**Cómo comprobarlo:** consultar dos veces el mismo ID y revisar la consola. La consulta SQL y el mensaje `Consultando ... en la base de datos` solo aparecen la primera vez.

### Logs con SLF4J
Los servicios usan `@Slf4j` para registrar eventos con distintos niveles:
- `INFO`: registros creados, actualizados o eliminados.
- `WARN`: correo repetido o intento de eliminar un registro que no existe.
- `DEBUG`: consultas a la base de datos.

Los logs se muestran en consola y también se guardan en `logs/gimnasio.log`.

### Spring Security y JWT
El usuario inicia sesión con su correo y contraseña, y la API devuelve un token firmado que dura 24 horas. Un filtro revisa el token en cada petición; si no es válido, la API responde `401`.

### Pruebas y cobertura
- **Prueba unitaria** (`EntrenadorServiceTest`): prueba el servicio con JUnit 5 y Mockito, sin base de datos.
- **Prueba de integración** (`EntrenadorControllerIntegrationTest`): levanta toda la aplicación con `@SpringBootTest` y prueba los endpoints con MockMvc.
- **JaCoCo**: genera el reporte de cobertura en `build/reports/jacoco/test/html/index.html`.

---

## 🚀 Instalación y ejecución

### Requisitos

- Java 17 o superior.
- PostgreSQL instalado y en ejecución.
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

Al iniciar, si las tablas están vacías, se crean estos usuarios de demostración (todos con la contraseña `clave1234`) y una clase de ejemplo (Spinning):

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

> Las pruebas de integración usan la base de datos local, así que PostgreSQL debe estar en ejecución. Los datos de prueba se eliminan automáticamente al terminar.

### 5. Limpiar la base de datos (opcional)

Para empezar de cero (por ejemplo, después de un cambio en el modelo):

```bash
./scripts/limpiar-bd.sh
```

El script lee la conexión del `.env`, pide escribir el nombre de la base de datos para confirmar, la borra y la vuelve a crear vacía, y elimina la carpeta `logs/`. Al ejecutar de nuevo `./scripts/iniciar.sh` se crean las tablas y los datos de demostración. En Windows se ejecuta desde Git Bash.

---

## 📄 Licencia

Proyecto desarrollado con fines académicos para la asignatura de Programación Orientada a Objetos de la Universidad de El Salvador.
