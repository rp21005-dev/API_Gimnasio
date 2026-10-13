# 🏋️ API_Gimnasio
### API REST para la gestión de clases de gimnasio

API REST desarrollada en Java para administrar las operaciones principales de un gimnasio, incluyendo la gestión de entrenadores, miembros, clases, inscripciones y asistencias.

El sistema aplica reglas de negocio para controlar los cupos disponibles, evitar inscripciones duplicadas y garantizar que los registros de asistencia correspondan a miembros inscritos.

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

## 📋 Descripción del proyecto

El proyecto consiste en el desarrollo de una API REST orientada a la gestión integral de las clases de un gimnasio. Su objetivo es facilitar la administración de los recursos y participantes, permitiendo organizar las clases impartidas por los entrenadores y gestionar las inscripciones y la asistencia de los miembros.

La API centraliza la información y aplica reglas de negocio que ayudan a mantener la integridad de los datos. Entre sus funcionalidades principales se encuentra el control automático de cupos, la prevención de inscripciones duplicadas y la validación de los participantes antes de registrar su asistencia.

## 🎯 Objetivos

- Implementar una API REST para administrar las operaciones de un gimnasio.
- Aplicar los principios de la Programación Orientada a Objetos.
- Facilitar la gestión de entrenadores, miembros y clases.
- Controlar las inscripciones según la disponibilidad de cupos.
- Mantener un registro organizado de la asistencia a las clases.
- Aplicar validaciones para garantizar la consistencia de la información.

---

## 🛠️ Tecnologías y herramientas

| Tecnología | Uso |
|---|---|
| **Java** | Lenguaje de programación principal. |
| **Gradle** | Gestión de dependencias y automatización de la compilación. |
| **API REST** | Interfaz para gestionar los recursos del sistema mediante solicitudes HTTP. |
| **Git** | Control de versiones del proyecto. |
| **GitHub** | Alojamiento y colaboración en el repositorio. |

---

## ⚙️ Funcionalidades principales

### 1. Gestión de entrenadores

Permite registrar, consultar, actualizar y eliminar entrenadores. Cada clase debe estar asociada a un entrenador responsable.

### 2. Gestión de miembros

Permite administrar la información de los miembros del gimnasio. Los miembros registrados pueden inscribirse en las clases que tengan cupos disponibles.

### 3. Gestión de clases

Permite crear, consultar, actualizar y eliminar clases, definiendo el entrenador responsable, el horario y la capacidad máxima de participantes.

### 4. Gestión de inscripciones

Permite registrar a los miembros en las clases, consultar las inscripciones existentes y cancelar una inscripción. Al cancelar, el cupo ocupado vuelve a estar disponible.

### 5. Control de asistencia

Permite registrar y consultar la asistencia de los miembros inscritos en una clase, así como actualizar el registro cuando sea necesario.

### 6. Control de cupos

Verifica la disponibilidad antes de aceptar una inscripción, impide superar la capacidad máxima de una clase y actualiza los lugares disponibles cuando se realizan inscripciones o cancelaciones.

---

## 🧩 Entidades principales

| Entidad | Descripción |
|---|---|
| **Entrenador** | Persona responsable de impartir una o varias clases. |
| **Miembro** | Cliente del gimnasio que puede inscribirse en las clases. |
| **Clase** | Actividad programada con un entrenador, un horario y un cupo máximo. |
| **Inscripción** | Registro que relaciona a un miembro con una clase y reserva un cupo. |
| **Asistencia** | Registro que indica si un miembro inscrito asistió a una clase. |

---

## 🌐 Endpoints de la API

La API organiza sus recursos mediante endpoints REST. Las rutas siguientes representan las operaciones CRUD previstas para cada recurso.

### Entrenadores

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/entrenadores` | Obtener todos los entrenadores. |
| `GET` | `/api/entrenadores/{id}` | Obtener un entrenador por su ID. |
| `POST` | `/api/entrenadores` | Registrar un entrenador. |
| `PUT` | `/api/entrenadores/{id}` | Actualizar un entrenador. |
| `DELETE` | `/api/entrenadores/{id}` | Eliminar un entrenador. |

### Miembros

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/miembros` | Obtener todos los miembros. |
| `GET` | `/api/miembros/{id}` | Obtener un miembro por su ID. |
| `POST` | `/api/miembros` | Registrar un miembro. |
| `PUT` | `/api/miembros/{id}` | Actualizar un miembro. |
| `DELETE` | `/api/miembros/{id}` | Eliminar un miembro. |

### Clases

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/clases` | Obtener todas las clases. |
| `GET` | `/api/clases/{id}` | Obtener una clase por su ID. |
| `POST` | `/api/clases` | Crear una clase. |
| `PUT` | `/api/clases/{id}` | Actualizar una clase. |
| `DELETE` | `/api/clases/{id}` | Eliminar una clase. |

### Inscripciones

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/inscripciones` | Obtener todas las inscripciones. |
| `GET` | `/api/inscripciones/{id}` | Obtener una inscripción por su ID. |
| `POST` | `/api/inscripciones` | Registrar una inscripción. |
| `PUT` | `/api/inscripciones/{id}` | Actualizar una inscripción. |
| `DELETE` | `/api/inscripciones/{id}` | Cancelar o eliminar una inscripción. |

### Asistencias

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/asistencias` | Obtener todos los registros de asistencia. |
| `GET` | `/api/asistencias/{id}` | Obtener una asistencia por su ID. |
| `POST` | `/api/asistencias` | Registrar una asistencia. |
| `PUT` | `/api/asistencias/{id}` | Actualizar un registro de asistencia. |
| `DELETE` | `/api/asistencias/{id}` | Eliminar un registro de asistencia. |

---

## 📏 Reglas de negocio

1. **Asignación de entrenador:** toda clase debe tener un entrenador válido asignado.
2. **Capacidad máxima:** cada clase debe tener definido un cupo máximo de participantes.
3. **Disponibilidad de cupos:** no se permiten inscripciones cuando la clase ha alcanzado su capacidad máxima.
4. **Inscripciones únicas:** un miembro no puede inscribirse más de una vez en la misma clase.
5. **Liberación de cupos:** al cancelar una inscripción, el lugar reservado vuelve a estar disponible.
6. **Miembros registrados:** únicamente los miembros existentes pueden inscribirse en las clases.
7. **Asistencia válida:** solo se puede registrar la asistencia de un miembro inscrito en la clase correspondiente.
8. **Registro único de asistencia:** cada miembro debe tener como máximo un registro de asistencia por clase.
9. **Integridad de referencias:** las operaciones deben validar que las entidades relacionadas existan.
10. **Validación de horarios:** los traslapes deben validarse según las reglas definidas por el proyecto.

---

## 📨 Códigos de respuesta HTTP

| Código | Nombre | Descripción |
|:---:|---|---|
| `200` | OK | Consulta o actualización realizada correctamente. |
| `201` | Created | Recurso creado correctamente. |
| `204` | No Content | Operación completada sin contenido de respuesta. |
| `400` | Bad Request | Solicitud con datos inválidos o incompletos. |
| `404` | Not Found | El recurso solicitado no existe. |
| `409` | Conflict | La operación entra en conflicto con una regla de negocio. |
| `500` | Internal Server Error | Error inesperado durante el procesamiento de la solicitud. |

---

## 🚀 Instalación y ejecución

### Requisitos previos

- Java en una versión compatible con el proyecto.
- Git.
- Gradle o el Gradle Wrapper incluido en el repositorio.

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd API_Gimnasio
````

### 2. Compilar el proyecto

Linux o macOS:

bash
```
./gradlew build
```

Windows:

bat
```
gradlew.bat build
```

### 3. Ejecutar las pruebas

Linux o macOS:

bash
```
./gradlew test
```

Windows:

bat
```
gradlew.bat test
```

### 4. Ejecutar la aplicación

Si el proyecto utiliza Spring Boot:

Linux o macOS:

bash
```
./gradlew bootRun
```

Windows:

bat
```
gradlew.bat bootRun
```

> Nota: si el proyecto no utiliza Spring Boot, ejecuta la tarea configurada en Gradle para iniciar la aplicación.

## 🧪 Pruebas y validación

- [ ]Crear y consultar entrenadores, miembros y clases.
- [ ]Inscribir a un miembro en una clase con cupos disponibles.
- [ ]Rechazar una inscripción cuando la clase está llena.
- [ ]Impedir inscripciones duplicadas.
- [ ]Liberar un cupo después de cancelar una inscripción.
- [ ]Registrar asistencia únicamente para miembros inscritos.
- [ ]Impedir registros de asistencia duplicados.
- [ ]Validar los códigos HTTP de las solicitudes exitosas y fallidas.

## 📌 Consideraciones

- Mantener los endpoints documentados sincronizados con los controladores.
- Realizar las validaciones de negocio en la API.
- Proteger las relaciones entre entidades al actualizar o eliminar registros.
- Actualizar esta documentación cuando se incorporen nuevas funcionalidades.

## 📄 Licencia

Proyecto desarrollado con fines académicos para la asignatura de Programación Orientada a Objetos de la Universidad de El Salvador.
