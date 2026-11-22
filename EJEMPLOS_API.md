# Ejemplos de uso de la API

Ejemplos con `curl` para probar la API. Casi todos los endpoints requieren el
encabezado `Authorization: Bearer <token>`; solo el inicio de sesión y el
registro de miembros son públicos.

## Requisitos

- La API corriendo en `http://localhost:8080`
  (con Docker: `docker compose up --build -d`).
- `curl` y `python3` (para extraer el token), o usar `scripts/obtener-token.sh`.

## Configuración rápida

```bash
API=http://localhost:8080

# Login del administrador de demostración
TOKEN=$(curl -s -X POST "$API/gimnasioAPI/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@gimnasio.com","contrasena":"clave1234"}' \
  | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

AUTH="Authorization: Bearer $TOKEN"

# Alternativa con el script del proyecto:
# TOKEN=$(./scripts/obtener-token.sh)
# AUTH="Authorization: Bearer $TOKEN"
```

Usuarios de demostración (contraseña `clave1234`):
`admin@gimnasio.com`, `entrenador@gimnasio.com`, `miembro@gimnasio.com`.

---

## Autenticación

### Iniciar sesión

```bash
curl -s -X POST "$API/gimnasioAPI/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@gimnasio.com","contrasena":"clave1234"}'
```

Respuesta `200`:

```json
{ "token": "eyJhbGciOi...", "rol": "ADMINISTRADOR" }
```

---

## Administradores

```bash
# Listar
curl -s "$API/gimnasioAPI/administrador" -H "$AUTH"

# Obtener uno
curl -s "$API/gimnasioAPI/administrador/1" -H "$AUTH"

# Crear (201)
curl -s -X POST "$API/gimnasioAPI/administrador" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"correo":"nuevo.admin@gimnasio.com","contrasenaHash":"clave1234","estado":"ACTIVO"}'

# Actualizar (200) - se envía el recurso completo
curl -s -X PUT "$API/gimnasioAPI/administrador/1" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"correo":"admin@gimnasio.com","estado":"ACTIVO"}'

# Eliminar (204)
curl -s -X DELETE "$API/gimnasioAPI/administrador/1" -H "$AUTH"
```

---

## Entrenadores

```bash
# Listar
curl -s "$API/gimnasioAPI/entrenador" -H "$AUTH"

# Obtener uno
curl -s "$API/gimnasioAPI/entrenador/2" -H "$AUTH"

# Crear (201)
curl -s -X POST "$API/gimnasioAPI/entrenador" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "nombres": "Laura",
    "apellidos": "Gómez",
    "correo": "laura.gomez@gimnasio.com",
    "telefono": "7000-3333",
    "especialidad": "Yoga",
    "contrasenaHash": "clave1234"
  }'

# Actualizar (200)
curl -s -X PUT "$API/gimnasioAPI/entrenador/2" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "nombres": "Carlos",
    "apellidos": "Martínez",
    "correo": "entrenador@gimnasio.com",
    "telefono": "7000-1111",
    "especialidad": "Musculación",
    "estado": "ACTIVO"
  }'

# Eliminar (204)
curl -s -X DELETE "$API/gimnasioAPI/entrenador/2" -H "$AUTH"
```

---

## Miembros

`POST /gimnasioAPI/miembros` es **público** (no requiere token). El campo
`contrasenaHash` recibe la contraseña en texto plano; el servidor la encripta.

```bash
# Crear (201) - público
curl -s -X POST "$API/gimnasioAPI/miembros" \
  -H "Content-Type: application/json" \
  -d '{
    "nombres": "Ana",
    "apellidos": "López",
    "documentoIdentidad": "12345678-0",
    "correo": "ana@ejemplo.com",
    "telefono": "7000-1234",
    "fechaNacimiento": "2001-03-10",
    "contrasenaHash": "clave1234"
  }'

# Listar
curl -s "$API/gimnasioAPI/miembros" -H "$AUTH"

# Obtener uno
curl -s "$API/gimnasioAPI/miembros/3" -H "$AUTH"

# Actualizar (200)
curl -s -X PUT "$API/gimnasioAPI/miembros/3" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "nombres": "Juan",
    "apellidos": "Pérez",
    "documentoIdentidad": "12345678-9",
    "correo": "miembro@gimnasio.com",
    "telefono": "7000-2222",
    "fechaNacimiento": "2000-05-15",
    "estado": "ACTIVO"
  }'

# Eliminar (204)
curl -s -X DELETE "$API/gimnasioAPI/miembros/3" -H "$AUTH"
```

---

## Clases

`POST` y `PUT` requieren `categoriaId`, `salaId` y `entrenadorId`. En `POST`,
`fechaInicio` no puede ser anterior a hoy. Además, el `tipoClase` debe coincidir
(sin distinguir mayúsculas) con uno de los `tiposClase` de la categoría, el
`cupoMaximo` no puede superar la capacidad de la sala, y la clase no puede
traslaparse con otra del mismo entrenador o sala en el mismo día y horario.

```bash
# Listar
curl -s "$API/gimnasioAPI/clases" -H "$AUTH"

# Obtener una
curl -s "$API/gimnasioAPI/clases/1" -H "$AUTH"

# Crear (201)
curl -s -X POST "$API/gimnasioAPI/clases" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
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
  }'

# Actualizar (200) - se envía el recurso completo, incluido el estado
curl -s -X PUT "$API/gimnasioAPI/clases/1" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "tipoClase": "Spinning / Indoor cycling",
    "diasSemana": ["Lunes", "Miércoles", "Viernes"],
    "horaInicio": "08:00:00",
    "horaFin": "09:00:00",
    "fechaInicio": "2026-11-02",
    "cupoMaximo": 20,
    "descripcion": "Clase de cardio en bicicleta fija.",
    "estado": "ACTIVA",
    "categoriaId": 1,
    "salaId": 1,
    "entrenadorId": 2
  }'

# Eliminar (204)
curl -s -X DELETE "$API/gimnasioAPI/clases/1" -H "$AUTH"
```

---

## Categorías

```bash
# Listar
curl -s "$API/gimnasioAPI/categorias" -H "$AUTH"

# Obtener una
curl -s "$API/gimnasioAPI/categorias/1" -H "$AUTH"

# Crear (201)
curl -s -X POST "$API/gimnasioAPI/categorias" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "nombre": "Funcional",
    "objetivoPrincipal": "Mejorar la condición física general.",
    "tiposClase": ["HIIT", "Circuitos", "Core"]
  }'

# Actualizar (200)
curl -s -X PUT "$API/gimnasioAPI/categorias/1" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "nombre": "Cardiovasculares (Cardio)",
    "objetivoPrincipal": "Resistencia aeróbica y quema calórica.",
    "tiposClase": ["Spinning / Indoor cycling", "Zumba", "Step"]
  }'

# Eliminar (204)
curl -s -X DELETE "$API/gimnasioAPI/categorias/1" -H "$AUTH"
```

---

## Salas

```bash
# Listar
curl -s "$API/gimnasioAPI/salas" -H "$AUTH"

# Obtener una
curl -s "$API/gimnasioAPI/salas/1" -H "$AUTH"

# Crear (201)
curl -s -X POST "$API/gimnasioAPI/salas" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "codigo": "S-04",
    "nombre": "Sala de Pesas",
    "ubicacion": "Planta baja",
    "capacidadMaxima": 40,
    "descripcion": "Zona de pesas libres y máquinas."
  }'

# Actualizar (200)
curl -s -X PUT "$API/gimnasioAPI/salas/1" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "codigo": "S-01",
    "nombre": "Sala de Ciclismo",
    "ubicacion": "Primer piso",
    "capacidadMaxima": 25,
    "descripcion": "Bicicletas fijas para spinning."
  }'

# Eliminar (204)
curl -s -X DELETE "$API/gimnasioAPI/salas/1" -H "$AUTH"
```

---

## Inscripciones

```bash
# Inscribir un miembro a una clase (201)
curl -s -X POST "$API/gimnasioAPI/inscripciones" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"claseId": 1, "miembroId": 3}'

# Listar (opcional: ?miembroId=3 o ?claseId=1)
curl -s "$API/gimnasioAPI/inscripciones" -H "$AUTH"

# Obtener una
curl -s "$API/gimnasioAPI/inscripciones/1" -H "$AUTH"

# Cambiar de clase (200)
curl -s -X PUT "$API/gimnasioAPI/inscripciones/1" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"nuevaClaseId": 2}'

# Cancelar inscripción (204)
curl -s -X DELETE "$API/gimnasioAPI/inscripciones/1" -H "$AUTH"
```

---

## Asistencias

```bash
# Registrar la asistencia de una sesión (201)
curl -s -X POST "$API/gimnasioAPI/asistencias" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{
    "claseId": 1,
    "fechaSesion": "2026-11-17",
    "registros": [
      { "miembroId": 3, "estado": "PRESENTE", "observacion": "Llegó a tiempo" },
      { "miembroId": 4, "estado": "AUSENTE", "observacion": "Sin aviso" }
    ]
  }'

# Listar (opcional: ?miembroId=3 o ?claseId=1)
curl -s "$API/gimnasioAPI/asistencias" -H "$AUTH"

# Obtener una
curl -s "$API/gimnasioAPI/asistencias/1" -H "$AUTH"

# Hoja de asistencia de una sesión
curl -s "$API/gimnasioAPI/asistencias/clases/1/sesiones/2026-11-17/asistencia" -H "$AUTH"

# Corregir una asistencia (200)
curl -s -X PUT "$API/gimnasioAPI/asistencias/1" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"estado": "AUSENTE", "observacion": "Se retiró temprano"}'

# Eliminar un registro de asistencia (204)
curl -s -X DELETE "$API/gimnasioAPI/asistencias/1" -H "$AUTH"
```

---

## Códigos de respuesta

| Código | Significado |
|:---:|---|
| `200` | Consulta o actualización correcta. |
| `201` | Registro creado. |
| `204` | Registro eliminado (sin cuerpo de respuesta). |
| `400` | Datos inválidos o incompletos. |
| `401` | No se envió token, el token no es válido o las credenciales son incorrectas. |
| `404` | El registro no existe. |
| `409` | El correo ya está registrado. |

## Notas

- La contraseña debe tener al menos 8 caracteres, con al menos una letra y un número.
- El correo es único entre administradores, entrenadores y miembros.
- El token dura 24 horas.
- `PUT` recibe el recurso completo (no es un parche parcial).
- El registro de miembros es público; el resto de endpoints requiere token.
