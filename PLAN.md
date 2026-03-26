# Plan: Sistema de Login + Aislamiento por Institucion

## Resumen
Implementar autenticacion JWT completa con pantalla de login/registro y filtrado automatico de datos por institucion del admin logueado.

---

## FASE 1: Backend - JWT Infrastructure

### 1.1 Dependencia JWT en build.gradle
Agregar `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` (version 0.12.6)

### 1.2 Propiedades JWT en application.properties
```
jwt.secret=<clave-secreta-base64-256bits>
jwt.expiration=86400000  (24 horas)
```

### 1.3 Nuevo: JwtService.java
`com.appasistencia.services.JwtService`
- `generateToken(Usuario usuario)` → String token con claims: sub=email, idUsuario, idInstitucion, rol, nombre
- `extractEmail(String token)` → String
- `extractIdInstitucion(String token)` → Long
- `isTokenValid(String token)` → boolean
- Usa la secret key de properties, expiracion configurable

### 1.4 Nuevo: JwtAuthenticationFilter.java
`com.appasistencia.configurations.JwtAuthenticationFilter` (extends OncePerRequestFilter)
- Lee header `Authorization: Bearer <token>`
- Valida token con JwtService
- Carga el usuario, crea UsernamePasswordAuthenticationToken
- Setea en SecurityContextHolder
- Guarda `idInstitucion` como atributo del request para que los controllers lo lean

### 1.5 Actualizar SecurityConfig.java
- Inyectar JwtAuthenticationFilter
- Agregar filtro antes de UsernamePasswordAuthenticationFilter
- Cambiar `/api/**` de permitAll a **authenticated**
- Mantener `/api/auth/**` como permitAll
- Agregar handler para 401 que retorne JSON (ApiError format)

### 1.6 Nuevo: AuthController.java
`com.appasistencia.controllers.AuthController`

Endpoints:
- `POST /api/auth/login` - Body: `{email, contrasena}` → `{token, usuario: {...}}`
- `POST /api/auth/register` - Body: UsuarioDTO (con rol=ADMIN fijo) → `{token, usuario: {...}}`
- `POST /api/auth/register-institucion` - Body: InstitucionDTO → InstitucionResponseDTO (endpoint publico para crear institucion durante registro)
- `GET /api/auth/me` - Header: Bearer token → usuario actual (para validar sesion)

### 1.7 Nuevo: AuthService.java
`com.appasistencia.services.AuthService`
- `login(String email, String contrasena)` → valida credenciales con BCrypt, genera token
- `register(UsuarioDTO dto)` → crea usuario ADMIN, genera token
- Reutiliza UsuarioService.crear() para el registro

### 1.8 Nuevos DTOs
- `LoginDTO.java`: email (NotBlank), contrasena (NotBlank)
- `AuthResponseDTO.java`: token (String), usuario (UsuarioResponseDTO)

---

## FASE 2: Backend - Filtrado por Institucion

### Estrategia: Nuevos metodos de repositorio con @Query JPQL

Para cada entidad, agregar queries que filtren por idInstitucion a traves de la cadena de relaciones. Esto es mas eficiente que filtrar en Java.

### 2.1 Nuevos metodos en Repositorios

**CarreraRepository** (directo):
```java
List<Carrera> findByInstitucionIdInstitucion(Long idInstitucion);
```

**MateriaRepository** (via Carrera):
```java
@Query("SELECT m FROM Materia m WHERE m.carrera.institucion.idInstitucion = :idInst")
List<Materia> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**CursoRepository** (via Carrera):
```java
@Query("SELECT c FROM Curso c WHERE c.carrera.institucion.idInstitucion = :idInst")
List<Curso> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**UsuarioRepository** (directo):
```java
List<Usuario> findByInstitucionIdInstitucion(Long idInstitucion);
```

**UsuarioProfesorRepository** (via Usuario):
```java
@Query("SELECT p FROM UsuarioProfesor p WHERE p.usuario.institucion.idInstitucion = :idInst")
List<UsuarioProfesor> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**UsuarioAlumnoRepository** (via Usuario):
```java
@Query("SELECT a FROM UsuarioAlumno a WHERE a.usuario.institucion.idInstitucion = :idInst")
List<UsuarioAlumno> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**CursoMateriaRepository** (via Curso→Carrera):
```java
@Query("SELECT cm FROM CursoMateria cm WHERE cm.curso.carrera.institucion.idInstitucion = :idInst")
List<CursoMateria> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**AsignacionRepository** (via CursoMateria→Curso→Carrera):
```java
@Query("SELECT a FROM Asignacion a WHERE a.cursoMateria.curso.carrera.institucion.idInstitucion = :idInst")
List<Asignacion> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**HorarioRepository** (via Asignacion→CursoMateria→Curso→Carrera):
```java
@Query("SELECT h FROM Horario h WHERE h.asignacion.cursoMateria.curso.carrera.institucion.idInstitucion = :idInst")
List<Horario> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**InscripcionRepository** (via Curso→Carrera):
```java
@Query("SELECT i FROM Inscripcion i WHERE i.curso.carrera.institucion.idInstitucion = :idInst")
List<Inscripcion> findByInstitucion(@Param("idInst") Long idInstitucion);
```

**AsistenciaRepository** (via Profesor→Usuario):
```java
@Query("SELECT a FROM Asistencia a WHERE a.profesor.usuario.institucion.idInstitucion = :idInst")
List<Asistencia> findByInstitucion(@Param("idInst") Long idInstitucion);
```

### 2.2 Actualizar Services - listarTodos con sobrecarga

Cada servicio recibe un nuevo metodo `listarTodos(Long idInstitucion)` que usa el query filtrado:

```java
// Ejemplo en CarreraService:
public List<CarreraResponseDTO> listarTodos(Long idInstitucion) {
    return carreraRepository.findByInstitucionIdInstitucion(idInstitucion).stream()
            .map(CarreraResponseDTO::fromEntity)
            .collect(Collectors.toList());
}
```

El `listarTodos()` sin parametro se mantiene pero NO se usa desde controllers (queda para uso interno).

### 2.3 Actualizar Controllers - Leer idInstitucion del request

Cada controller extrae idInstitucion del request attribute (seteado por JwtAuthenticationFilter):

```java
@GetMapping
public List<CarreraResponseDTO> listarTodas(HttpServletRequest request) {
    Long idInst = (Long) request.getAttribute("idInstitucion");
    return carreraService.listarTodos(idInst);
}
```

**InstitucionController** es especial: el admin solo ve SU institucion.
```java
@GetMapping
public List<InstitucionResponseDTO> listar(HttpServletRequest request) {
    Long idInst = (Long) request.getAttribute("idInstitucion");
    return List.of(institucionService.obtenerPorId(idInst));
}
```

### 2.4 Validacion de ownership en crear/actualizar/eliminar

En los metodos crear/actualizar de cada service, validar que la entidad pertenezca a la institucion del admin. Por ejemplo en CarreraService.crear():
- La idInstitucion del DTO debe ser igual a la del admin logueado
- O mejor: ignorar la idInstitucion del DTO y usar la del admin automaticamente

**Enfoque**: Los controllers pasan idInstitucion al crear, y los services la asignan automaticamente.

---

## FASE 3: Frontend - Pantalla de Login

### 3.1 Nuevo: auth.js
`static/js/auth.js` - Modulo de autenticacion

```javascript
const Auth = {
    getToken()     // lee de localStorage
    getUser()      // lee usuario de localStorage
    isLoggedIn()   // verifica si hay token
    login(email, contrasena)  // POST /api/auth/login
    register(dto)  // POST /api/auth/register
    logout()       // limpia localStorage, muestra login
    validate()     // GET /api/auth/me, valida token vigente
}
```

### 3.2 Modificar index.html

Agregar ANTES del contenido principal un contenedor de login:

```html
<!-- Login Screen -->
<div id="auth-screen" style="display:none;">
    <!-- Se renderiza dinámicamente desde auth.js -->
</div>

<!-- App (sidebar + content) - se oculta hasta login -->
<div id="app-wrapper" style="display:none;">
    <!-- sidebar, topbar, content existentes -->
</div>
```

- Mover sidebar + topbar + content dentro de `#app-wrapper`
- Agregar `auth.js` al load order (despues de ui.js, antes de crud.js)

### 3.3 Pantalla de Login (diseño)

Pantalla fullscreen centrada con el tema oscuro/claro existente:
- Card centrado con logo/titulo "Sistema de Asistencia"
- Dos tabs: "Iniciar Sesion" | "Registrarse"
- Tab login: email + contrasena + boton "Ingresar"
- Tab registro: formulario multi-step
  - Step 1: Datos personales (nombre, apellido, email, contrasena, confirmar contrasena, documento, genero)
  - Step 2: Seleccionar institucion (dropdown con existentes + boton "Crear nueva institucion")
  - Sub-formulario institucion: nombre, direccion, telefono, email (aparece inline)
- Validaciones frontend identicas a las existentes (UI.validatePersonForm)
- Mensajes de error con toast

### 3.4 Modificar api.js

Agregar header Authorization automaticamente:
```javascript
const token = localStorage.getItem('auth_token');
if (token) {
    options.headers['Authorization'] = 'Bearer ' + token;
}
```

Manejar respuestas 401: limpiar token y redirigir a login.

### 3.5 Modificar app.js

- En `init()`: verificar Auth.isLoggedIn() antes de navegar
- Si no logueado: mostrar #auth-screen, ocultar #app-wrapper
- Si logueado: validar token con /api/auth/me, luego mostrar app
- Agregar boton logout en topbar
- Mostrar nombre del usuario en topbar

### 3.6 Ocultar dropdown de Institucion en formularios

Como el admin solo opera en SU institucion:
- En forms de carreras: no mostrar selector de institucion (se asigna automaticamente)
- En forms de usuarios: no mostrar selector de institucion (se asigna automaticamente)
- Simplifica la UI considerablemente

### 3.7 Ocultar pagina de Instituciones del sidebar

El admin no necesita gestionar otras instituciones. La pagina de instituciones se oculta o se convierte en "Mi Institucion" para ver/editar solo la propia.

---

## FASE 4: Frontend - Ajustes de datos por institucion

### 4.1 Los datos ya vienen filtrados del backend
Como los endpoints ahora filtran por idInstitucion automaticamente (via JWT), el frontend NO necesita cambios en las paginas CRUD. Los listados, dropdowns y busquedas ya reciben solo datos de la institucion correcta.

### 4.2 Asignar idInstitucion al crear registros
En los formularios de creacion, el idInstitucion se envia automaticamente desde el backend (leido del token), no del formulario.

---

## Archivos a crear (nuevos):
1. `src/.../services/JwtService.java`
2. `src/.../services/AuthService.java`
3. `src/.../controllers/AuthController.java`
4. `src/.../configurations/JwtAuthenticationFilter.java`
5. `src/.../dtos/LoginDTO.java`
6. `src/.../dtos/AuthResponseDTO.java`
7. `static/js/auth.js`

## Archivos a modificar:
1. `build.gradle` (dependencia jjwt)
2. `application.properties` (jwt.secret, jwt.expiration)
3. `SecurityConfig.java` (filtro JWT, proteger endpoints)
4. `index.html` (login screen, app-wrapper, cargar auth.js, boton logout en topbar)
5. `api.js` (header Authorization, manejar 401)
6. `app.js` (auth check en init, logout)
7. `styles.css` (estilos de login screen)
8. 12 Repositorios (nuevos metodos findByInstitucion)
9. 12 Services (nuevos metodos listarTodos(idInstitucion))
10. 12 Controllers (leer idInstitucion del request)
11. Formularios que tenian selector de institucion (carreras, profesores, alumnos, administradores)

## Orden de implementacion:
1. build.gradle + properties (dependencias)
2. JwtService (infraestructura JWT)
3. JwtAuthenticationFilter
4. SecurityConfig (conectar filtro)
5. DTOs de auth (LoginDTO, AuthResponseDTO)
6. AuthService + AuthController (login/register)
7. Repositorios (queries por institucion)
8. Services (metodos filtrados)
9. Controllers (leer idInstitucion)
10. Frontend: auth.js + login screen
11. Frontend: api.js + app.js (integracion)
12. Frontend: ajustes UI (ocultar selector institucion, etc.)
13. Compilar y probar
