# Sistema de Asistencia para Profesores

Este proyecto es una aplicación **Spring Boot** que permite gestionar profesores, sus horarios y registrar la asistencia de cada uno. Fue diseñado como un ejemplo completo de CRUD (alta, modificación, baja lógica y consulta) con control de accesos y cálculo automático de llegadas a horario o tarde.

## Estructura del proyecto

El código se encuentra en el paquete `com.example.asistencia` y se organiza en los siguientes módulos:

- **Entidad `Profesor`** – representa a cada profesor. Posee un campo `deleted` para realizar **baja lógica** (eliminación suave). La anotación `@SQLDelete` redefine la operación de borrado para que solo actualice este campo y `@Where` filtra los profesores eliminados en las consultas.
- **Entidad `Horario`** – define una franja horaria con `horaInicio` y `horaFin`. Cada profesor tiene asignado un horario para comparar su hora de ingreso.
- **Entidad `Asistencia`** – registra la fecha y hora de entrada de un profesor. El servicio calcula si llegó a tiempo (`EstadoAsistencia.A_TIEMPO`) o tarde (`EstadoAsistencia.TARDE`) comparando `fechaHoraIngreso.toLocalTime()` con el horario asignado.
- **Entidad `Usuario`** – usuarios del sistema con credenciales y rol (`ADMIN` o `PROFESOR`). La clase `DataInitializer` crea un usuario administrador por defecto (`admin` / `admin123`).
- **Servicios (`service`)** – contienen la lógica de negocio para profesores, horarios, asistencias y usuarios. Por ejemplo, `AsistenciaService` determina el estado de la asistencia en función del horario.
- **Controladores (`controller`)** – exponen las API REST:
    - `ProfesorController` en la ruta `/api/profesores` con operaciones de alta, consulta, modificación y baja lógica.
    - `HorarioController` en `/api/horarios` para gestionar los horarios.
    - `AsistenciaController` en `/api/asistencias` para registrar y consultar asistencias.
- **Seguridad (`config/SecurityConfig.java`)** – configura Spring Security con autenticación básica. Solo los usuarios con rol `ADMIN` pueden crear, modificar o eliminar recursos; las operaciones de lectura requieren estar autenticado.

La baja lógica se implementa gracias a las anotaciones JPA `@SQLDelete` y `@Where`. Cuando se elimina un profesor mediante `DELETE`, se ejecuta una actualización que marca el campo `deleted` en `true` en lugar de borrar la fila. Las consultas automáticas excluyen los registros con `deleted=true`, de modo que siguen en la base pero ya no aparecen【116801425690994†screenshot】.

## Requisitos

* Java 17 o superior.
* Gradle wrapper o instalación local de Gradle. El proyecto incluye un archivo `build.gradle` con las dependencias necesarias (SpringBoot, Spring Data JPA, Spring Security, H2, Lombok). Al combinar este proyecto con tu repositorio original, asegúrate de conservar los archivos `gradlew`, `gradlew.bat` y `gradle/wrapper` para utilizar el wrapper.

## Cómo ejecutar la aplicación

1. **Descomprimir el proyecto.** Copia el contenido del directorio `asistenciaAutomatica` en tu repositorio de GitHub o en tu entorno de desarrollo. Si tu repositorio ya tiene los archivos del **Gradle wrapper** (`gradlew`, `gradlew.bat` y `gradle/wrapper/gradle-wrapper.jar`), no los sobrescribas.
2. **Abrir en tu IDE favorito** (IntelliJ IDEA, Eclipse, etc.). El proyecto utiliza Gradle; la mayoría de IDEs detectan el archivo `build.gradle` automáticamente.
3. **Ejecutar la aplicación** usando el wrapper:

   ```bash
   ./gradlew bootRun
   ```

   En Windows usa `gradlew.bat`. Esto compilará el proyecto y arrancará el servidor en `http://localhost:8080`.

4. **Acceder a la consola de H2.** Puedes ver las tablas y datos en `http://localhost:8080/h2-console`. Las credenciales de la base se encuentran en `src/main/resources/application.properties` y, por defecto, se usa una base en memoria (`jdbc:h2:mem:db`) con usuario `sa` y contraseña vacía.

5. **Autenticación**. La aplicación usa autenticación básica. Un usuario administrador inicial (`admin` / `admin123`) se crea automáticamente al iniciar. Puedes crear nuevos usuarios mediante el `UsuarioService` (añadiendo métodos o endpoints según tus necesidades) o directamente en la base de datos.

## Endpoints principales

Todos los endpoints están prefijados por `/api`. Las operaciones de escritura (POST, PUT, DELETE) requieren el rol `ADMIN`. Ejemplos de solicitudes usando `curl`:

### Profesores

* **Listar profesores** (incluye solo los no eliminados):

  ```bash
  curl -u admin:admin123 http://localhost:8080/api/profesores
  ```

* **Crear profesor**:

  ```bash
  curl -u admin:admin123 -X POST -H "Content-Type: application/json" \
       -d '{"nombre":"Juan","apellido":"Pérez","email":"juan.perez@example.com","horarioId":1}' \
       http://localhost:8080/api/profesores
  ```

* **Modificar profesor** (por id):

  ```bash
  curl -u admin:admin123 -X PUT -H "Content-Type: application/json" \
       -d '{"nombre":"Juan","apellido":"Pérez","email":"jperez@example.com","horarioId":1}' \
       http://localhost:8080/api/profesores/1
  ```

* **Eliminar profesor** (baja lógica):

  ```bash
  curl -u admin:admin123 -X DELETE http://localhost:8080/api/profesores/1
  ```

### Horarios

* **Crear horario**:

  ```bash
  curl -u admin:admin123 -X POST -H "Content-Type: application/json" \
       -d '{"descripcion":"Turno mañana","horaInicio":"08:00","horaFin":"12:00"}' \
       http://localhost:8080/api/horarios
  ```

* **Listar horarios**:

  ```bash
  curl -u admin:admin123 http://localhost:8080/api/horarios
  ```

### Asistencias

* **Registrar asistencia** (calcula automáticamente si llegó a tiempo o tarde):

  ```bash
  curl -u admin:admin123 -X POST -H "Content-Type: application/json" \
       -d '{"profesorId":1,"fechaHoraIngreso":"2026-01-25T08:05:00"}' \
       http://localhost:8080/api/asistencias
  ```

  La respuesta incluirá el estado de la asistencia (`A_TIEMPO` o `TARDE`) según el horario del profesor.

* **Listar asistencias**:

  ```bash
  curl -u admin:admin123 http://localhost:8080/api/asistencias
  ```
## Desarrollado por:

- [Quiroga Francisco](https://github.com/fran-q)