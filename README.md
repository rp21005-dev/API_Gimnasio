<<<<<<< Updated upstream
# API_Gimnasio - Proyecto API para la gestion de gimnasio
---
<<<<<<< HEAD
=======
# API_Gimnasio - Proyecto API para la gestion de gimnasio
---
>**Universidad de El Salvador**
>
>**Facultad Multidisciplinaria de Occidente**
>
>**Asignatura:** Programación Orientada a Objetos (POO) - Ciclo II/2026
>
>**Carrera:** Ingeniería en Desarrollo de Software / Educación en Línea
>
>---
>---
>## Integrantes del Equipo
>| Nombre Completo | Carnét |
>| :--- | :---: |
>| Jehosua Abdiel Cañas Tijerino | CT24001 |
>| Alexis Jonathan Mazariego Mazariego| MM24002 |
>| Joseline Rosibel Aldana Aldana | AA13081 |
>| Jorge Mario Meléndez | MC25066 |
>| Jason Isaac Rogríguez Pérez | RP21005 |

>## 📝 Descripción del Proyecto
>
>Este proyecto consiste en el desarrollo de una API para la gestión integral de un gimnasio. El sistema automatiza el control de clases recurrentes impartidas por entrenadores, el autoregistro e inscripción de miembros, el control automático de cupo, la prevención de traslapes de horario y la gestión detallada de asistencia por sesión.
>
> ---
>
>## 🛠️ Stack Tecnológico y Herramientas
>* **Lenguaje de Programación:** Java
>* **Gestor de Dependencias:** Gradle


# API_Gimnasio
Proyecto API para la gestion de gimnasio

API de Gestión de Clases de Gimnasio

API REST para administrar las clases de un gimnasio: los entrenadores imparten clases, los miembros se registran en ellas, se controla el cupo disponible y se lleva el registro de la asistencia.

Descripción

El sistema resuelve la operación diaria de las clases de un gimnasio a través de una API REST. Permite mantener el catálogo de entrenadores y de miembros, programar las clases que cada entrenador imparte, inscribir a los miembros en esas clases y registrar quién asistió a cada una.

La regla central del sistema es el control del cupo: cada clase tiene un número máximo de participantes y la API garantiza que las inscripciones nunca lo superen. Cuando una clase alcanza su cupo máximo, el sistema rechaza nuevas inscripciones; cuando un miembro cancela, el lugar vuelve a quedar disponible.

Qué hace el sistema
Gestiona entrenadores. Registra a los entrenadores del gimnasio, permite consultarlos, actualizar sus datos y eliminarlos. Cada clase se asigna a un entrenador.
Gestiona miembros. Registra a los miembros, permite consultarlos, actualizar su información y darlos de baja. Solo un miembro registrado puede inscribirse en una clase.
Gestiona clases. Programa las clases indicando entrenador, horario y cupo máximo, y permite consultarlas, modificarlas y eliminarlas.
Gestiona inscripciones. Permite que un miembro se registre en una clase, consulte sus inscripciones y las cancele. Al inscribirse se reserva un lugar y al cancelar se libera.
Registra la asistencia. Permite marcar si cada miembro inscrito asistió o no a la clase, corregir ese dato y consultar el historial.
Controla el cupo. Verifica el cupo disponible antes de cada inscripción, impide superar el máximo de la clase y mantiene actualizado el número de lugares libres.
Entidades principales
Entidad	Descripción
Entrenador	Persona que imparte las clases.
Miembro	Cliente del gimnasio que se inscribe en las clases.
Clase	Sesión impartida por un entrenador, con horario y cupo máximo.
Inscripción	Registro de un miembro en una clase; ocupa un lugar del cupo.
Asistencia	Registro de si un miembro inscrito asistió a la clase.
Operaciones

Cada entidad principal expone las cuatro operaciones del CRUD:

Método	Uso
GET	Consultar el listado o el detalle de un registro.
POST	Crear un nuevo registro.
PUT	Actualizar un registro existente.
DELETE	Eliminar un registro.
Endpoints
Recurso	Endpoints
Entrenadores	GET /api/entrenadores · GET /api/entrenadores/{id} · POST /api/entrenadores · PUT /api/entrenadores/{id} · DELETE /api/entrenadores/{id}
Miembros	GET /api/miembros · GET /api/miembros/{id} · POST /api/miembros · PUT /api/miembros/{id} · DELETE /api/miembros/{id}
Clases	GET /api/clases · GET /api/clases/{id} · POST /api/clases · PUT /api/clases/{id} · DELETE /api/clases/{id}
Inscripciones	GET /api/inscripciones · GET /api/inscripciones/{id} · POST /api/inscripciones · PUT /api/inscripciones/{id} · DELETE /api/inscripciones/{id}
Asistencias	GET /api/asistencias · GET /api/asistencias/{id} · POST /api/asistencias · PUT /api/asistencias/{id} · DELETE /api/asistencias/{id}
Reglas de negocio
Toda clase debe tener un entrenador asignado.
Toda clase tiene un cupo máximo de participantes.
Un miembro solo puede inscribirse en una clase si hay cupo disponible.
Un miembro no puede inscribirse dos veces en la misma clase.
Al cancelar una inscripción, el lugar vuelve a estar disponible.
Solo se registra la asistencia de miembros inscritos en la clase.
Cada miembro tiene un único registro de asistencia por clase.
## 📨 Respuestas de la API

| Código | Nombre | Significado |
| :---: | :--- | :--- |
| `200` | OK | Consulta o actualización correcta. |
| `201` | Created | Registro creado. |
| `204` | No Content | Registro eliminado. |
| `400` | Bad Request | Datos incompletos o con formato inválido. |
| `404` | Not Found | El registro solicitado no existe. |
| `409` | Conflict | La operación viola una regla de negocio, por ejemplo inscribirse en una clase sin cupo disponible. |
=======
>>>>>>> Stashed changes
