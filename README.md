# API de Gestión de Eventos

API REST desarrollada con **Java 21 y Spring Boot 3.5.6** para administrar eventos, categorías y ponentes. Incluye persistencia con Spring Data JPA, autenticación mediante JWT, autorización por roles, validación de solicitudes, documentación OpenAPI y configuración para ejecución con Docker.

> **Estado del proyecto:** repositorio de desarrollo. Antes de exponerlo a Internet o usarlo en producción, revisa las advertencias de seguridad y configuración de la sección correspondiente.

## Funcionalidades

- **Eventos:** operaciones CRUD, búsqueda opcional por nombre, paginación y ordenamiento.
- **Categorías:** operaciones CRUD para clasificar eventos.
- **Ponentes:** operaciones CRUD y asociación de varios ponentes con un evento.
- **Autenticación:** registro e inicio de sesión; emisión y validación de tokens JWT.
- **Autorización:** roles `ROLE_USER` y `ROLE_ADMIN` aplicados a los endpoints de gestión.
- **Validación y errores:** validación con Jakarta Bean Validation y respuestas centralizadas para errores de validación, recursos inexistentes e integridad de datos.
- **Consultas de eventos:** endpoints separados para observar una consulta con carga de relaciones y otra con `JOIN FETCH`, útiles para estudiar el problema N+1.
- **Datos de demostración:** al iniciar, `DataLoader` crea roles, usuarios, categorías, ponentes y, si la tabla está vacía, 60 eventos de ejemplo.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje y runtime |
| Spring Boot 3.5.6 | Configuración y ejecución de la aplicación |
| Spring Web | API REST |
| Spring Data JPA / Hibernate | Persistencia y relaciones entre entidades |
| PostgreSQL | Base de datos configurada por defecto |
| H2 | Dependencia de runtime incluida; los perfiles actuales apuntan a PostgreSQL |
| Spring Security | Autenticación y autorización |
| JJWT 0.13.0 | Creación y validación de JWT |
| BCrypt | Hash de contraseñas |
| MapStruct 1.6.3 | Conversión entre entidades y DTOs |
| Lombok | Reducción de código repetitivo |
| Jakarta Validation | Validación de los datos de entrada |
| Springdoc OpenAPI 2.8.13 | Documentación interactiva de la API |
| JUnit 5, Mockito y Spring Security Test | Pruebas automatizadas |
| Maven Wrapper | Compilación y gestión de dependencias |
| Docker / Docker Compose | Empaquetado y ejecución en contenedor |

## Arquitectura del código

El código principal está en `src/main/java/com/gestion/eventos/api/`.

```text
api/
├── controller/       # Endpoints REST de eventos, categorías y ponentes
├── DTO/              # Objetos de entrada y salida de la API
├── data/             # Inicialización de datos de demostración
├── domain/           # Entidades JPA: Event, Category, Speaker, User y Role
├── exception/        # Excepciones de dominio y manejador global
├── mapper/           # Conversiones entre entidades y DTOs con MapStruct
├── repository/       # Repositorios de Spring Data JPA
├── security/
│   ├── config/       # Configuración de Spring Security y OpenAPI
│   ├── controller/   # Registro e inicio de sesión
│   ├── dto/          # DTOs de autenticación
│   ├── jwt/          # Generación, validación y filtro JWT
│   └── service/      # Carga de usuarios para Spring Security
└── ApiApplication.java

src/main/resources/
├── application.properties
├── application-dev.properties
├── application-prod.properties
└── logback-spring.xml

src/test/java/        # Pruebas de contexto, servicio y controlador de eventos
Dockerfile
docker-compose.yml
pom.xml
```

### Modelo de datos

- `Event` tiene nombre, fecha, ubicación y una categoría obligatoria.
- `Category` tiene nombre único y descripción.
- `Speaker` tiene nombre, correo único y biografía. La relación entre eventos y ponentes es muchos-a-muchos.
- `User` contiene nombre de usuario, correo, contraseña codificada, roles y una relación de eventos a los que asiste.
- `Role` representa los roles de seguridad.

Los DTOs evitan exponer la entidad `Event` en las respuestas CRUD habituales. MapStruct transforma las entidades y los DTOs, mientras que la capa de servicios coordina las operaciones y consultas a repositorios.

## Requisitos

- JDK 21.
- Una instancia de PostgreSQL accesible desde la aplicación.
- Git para clonar el repositorio.
- Docker Desktop si se desea usar Docker Compose.

## Configuración de variables de entorno

La aplicación usa estas variables en sus perfiles:

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de PostgreSQL, por ejemplo `jdbc:postgresql://localhost:5432/eventos` |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Secreto utilizado para firmar JWT con HS512; debe tener al menos 64 bytes y mantenerse privado |
| `JWT_EXPIRATION` | Tiempo de vida del token en milisegundos; `86400000` equivale a 24 horas |

No subas contraseñas ni secretos reales al repositorio. El archivo `.env` está excluido por `.gitignore`.

### Ejecutar localmente en Windows PowerShell

1. Clona el proyecto y entra en su carpeta:

   ```powershell
   git clone https://github.com/juancakun/api.git
   cd api
   ```

2. Crea una base de datos PostgreSQL llamada `eventos` (o ajusta la URL a la base de datos que tengas).

3. Define las variables de entorno en la terminal actual. Sustituye los valores de ejemplo por los de tu entorno y genera un secreto JWT aleatorio de al menos 64 bytes:

   ```powershell
   $env:DB_URL = "jdbc:postgresql://localhost:5432/eventos"
   $env:DB_USER = "tu_usuario"
   $env:DB_PASSWORD = "tu_password"
   $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
   $bytes = New-Object byte[] 64
   $rng.GetBytes($bytes)
   $env:JWT_SECRET = [Convert]::ToBase64String($bytes)
   $rng.Dispose()
   $env:JWT_EXPIRATION = "86400000"
   ```

   El comando genera una clave aleatoria en la sesión actual de PowerShell. Para volver a ejecutar la aplicación en otra terminal, define las variables de nuevo o guárdalas en un gestor de secretos; no publiques el valor generado.

4. Compila y ejecuta las pruebas:

   ```powershell
   .\mvnw.cmd clean test
   ```

5. Inicia la aplicación:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

La aplicación queda disponible, por defecto, en `http://localhost:8080`.

> **Importante:** `application.properties` activa el perfil `dev` por defecto. Las variables deben estar definidas en el entorno de la terminal; Spring Boot no carga automáticamente el archivo `.env` que usa Docker Compose.

## Ejecutar con Docker Compose

El archivo `docker-compose.yml` levanta **únicamente la aplicación**. No crea un contenedor de PostgreSQL, por lo que necesitas una base de datos externa o un PostgreSQL que ya esté ejecutándose.

1. Crea un archivo `.env` en la raíz del proyecto. Este archivo se utiliza para sustituir las variables declaradas en Compose:

   Genera primero una clave aleatoria (si tienes OpenSSL instalado, `openssl rand -hex 64`) y coloca la salida en `JWT_SECRET`. Después crea el archivo con tus propios valores:

   ```dotenv
   DB_URL=jdbc:postgresql://host.docker.internal:5432/eventos
   DB_USER=tu_usuario
   DB_PASSWORD=tu_password
   JWT_SECRET=pega_aqui_la_clave_aleatoria_generada
   JWT_EXPIRATION=86400000
   ```

   `host.docker.internal` permite normalmente que un contenedor de Docker Desktop en Windows acceda a un servicio publicado en el host. Si la base de datos está en otro servidor, usa su URL JDBC. El texto `pega_aqui_la_clave_aleatoria_generada` es solo un marcador y debe sustituirse.

2. Construye la imagen y arranca el servicio:

   ```powershell
   docker compose up --build
   ```

3. Para detenerlo:

   ```powershell
   docker compose down
   ```

La API se publica en el puerto `8080`. El `Dockerfile` utiliza una etapa de compilación con JDK 21 y una imagen de ejecución con JRE 21. Actualmente empaqueta la aplicación con `-DskipTests`, por lo que la construcción de Docker no verifica que las pruebas pasen. Además, el repositorio registra `mvnw` sin el bit de ejecución (`100644`); si Docker falla con `Permission denied` al ejecutar `./mvnw`, ajusta el `Dockerfile` para ejecutar `RUN chmod +x mvnw && ./mvnw clean package -DskipTests` o llama al wrapper mediante `sh ./mvnw`.

## Documentación interactiva

Con el perfil `dev` activo, abre:

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- Especificación OpenAPI: <http://localhost:8080/v3/api-docs>

La configuración de seguridad permite acceder a Swagger sin token en el perfil `dev`. Para llamar endpoints protegidos, inicia sesión y utiliza el token JWT en el encabezado `Authorization` con el formato `Bearer <token>`.

## Endpoints

Todas las rutas están versionadas con el prefijo `/api/v1`.

### Autenticación

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Público | Registrar un usuario |
| `POST` | `/api/v1/auth/login` | Público | Autenticar usuario y obtener JWT |

Ejemplo de registro (sin asignar roles en el cuerpo):

```json
{
  "username": "usuario_demo",
  "password": "una_clave_segura",
  "email": "usuario@example.com",
  "name": "Usuario Demo"
}
```

Ejemplo de inicio de sesión:

```json
{
  "username": "usuario_demo",
  "password": "una_clave_segura"
}
```

La respuesta actual de autenticación usa la propiedad `accesToken` (con una sola `s` después de `acce`), y `tokenType` con valor `Bearer`. Ese nombre refleja el código actual; conviene corregirlo a `accessToken` en una siguiente versión.

### Eventos

| Método | Ruta | Acceso previsto | Descripción |
|---|---|---|---|
| `GET` | `/api/v1/events` | `ADMIN`, `USER` | Listar eventos con paginación y filtro opcional `name` |
| `GET` | `/api/v1/events/{id}` | `ADMIN`, `USER` | Consultar evento por ID |
| `POST` | `/api/v1/events` | `ADMIN` | Crear evento |
| `PUT` | `/api/v1/events/{id}` | `ADMIN` | Actualizar evento |
| `DELETE` | `/api/v1/events/{id}` | `ADMIN` | Eliminar evento |
| `GET` | `/api/v1/events/problematic` | Cualquier usuario autenticado según la configuración global actual | Consultar eventos con carga de relaciones para observar el problema N+1 |
| `GET` | `/api/v1/events/optimized` | Cualquier usuario autenticado según la configuración global actual | Consultar eventos usando `JOIN FETCH` |

El listado admite parámetros de paginación de Spring Data, por ejemplo:

```text
GET /api/v1/events?name=Spring&page=0&size=10&sort=name,asc
```

Ejemplo de cuerpo para crear o actualizar un evento (ajusta los IDs para que existan en tu base de datos):

```json
{
  "name": "Conferencia de Spring Boot",
  "date": "2026-11-12",
  "location": "Ciudad de México",
  "categoryId": 1,
  "speakerIds": [1, 2]
}
```

`date` usa el formato ISO `YYYY-MM-DD`. `speakerIds` no puede ser `null` en el DTO; para crear un evento sin ponentes, envía una lista vacía: `[]`.

### Categorías

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/v1/categories` | `ADMIN`, `USER` | Listar categorías |
| `GET` | `/api/v1/categories/id?id={id}` | `ADMIN`, `USER` | Consultar categoría por ID |
| `POST` | `/api/v1/categories` | `ADMIN` | Crear categoría |
| `PUT` | `/api/v1/categories/{id}` | `ADMIN` | Actualizar categoría |
| `DELETE` | `/api/v1/categories/{id}` | `ADMIN` | Eliminar categoría |

### Ponentes

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/v1/speakers` | `ADMIN`, `USER` | Listar ponentes |
| `GET` | `/api/v1/speakers/{id}` | `ADMIN`, `USER` | Consultar ponente por ID |
| `POST` | `/api/v1/speakers` | `ADMIN` | Crear ponente |
| `PUT` | `/api/v1/speakers/{id}` | `ADMIN` | Actualizar ponente |
| `DELETE` | `/api/v1/speakers/{id}` | `ADMIN` | Eliminar ponente |

Los endpoints de consulta de eventos `problematic` y `optimized` son rutas de demostración; no están sujetos a anotaciones de autorización por rol como los endpoints CRUD habituales y deberían revisarse antes de producción.

## Datos de demostración

`DataLoader` genera los roles `ROLE_ADMIN` y `ROLE_USER`, dos cuentas de demostración, tres categorías (`Conferencia`, `Taller`, `Webinar`), dos ponentes y hasta 60 eventos cuando no hay eventos existentes.

Las credenciales actuales de demostración están escritas en el código (`admin` / `admin1234` y `user` / `123456`). **Son conocidas y no deben utilizarse en un despliegue público.** El cargador las crea si esas cuentas no existen.

## Pruebas

Las pruebas existentes cubren principalmente el servicio y el controlador de eventos:

- `EventServiceTest`: consulta por ID, error cuando no existe, creación con y sin ponentes, categoría inexistente y listado paginado con y sin filtro.
- `EventControllerTest`: consulta por ID, 404, listado filtrado/paginado, creación, actualización y eliminación.
- `ApiApplicationTests`: prueba básica de carga del contexto.

Para ejecutar toda la suite:

```powershell
.\mvnw.cmd clean test
```

La prueba de contexto usa la configuración de aplicación y, por tanto, necesita las variables de entorno y una base de datos que pueda alcanzar. No hay pruebas equivalentes dedicadas a categorías, ponentes, registro/login ni a las reglas de autorización.

## Aspectos que conviene corregir antes de producción

1. **Asignación de roles al registrarse:** el registro público recibe un campo `roles` y `UserMapper` acepta los nombres enviados si existen en la base de datos. Como `ROLE_ADMIN` existe, un cliente podría intentar registrarse como administrador. La asignación de roles debe controlarse exclusivamente en el servidor y el registro público debe asignar solo `ROLE_USER`.
2. **Cuentas de demostración:** `DataLoader` crea usuarios con contraseñas conocidas. Deshabilita o reemplaza esa lógica en despliegues no locales y exige credenciales seguras.
3. **Configuración del secreto JWT:** ambos archivos de perfil contienen `jwt.secret=${JWT_SECRET}}`, con una llave de cierre sobrante. La propiedad debería ser `jwt.secret=${JWT_SECRET}`.
4. **Perfil de producción:** `application-prod.properties` mantiene `ddl-auto=update`, `show-sql=true` y logging `DEBUG` para el paquete de la aplicación, igual que el perfil de desarrollo. Define ajustes de producción explícitos y revisa las migraciones de esquema.
5. **Validación del registro:** `RegisterDto` declara restricciones, pero el controlador de autenticación no aplica `@Valid` al cuerpo recibido. Añade validación y pruebas que cubran los casos inválidos.
6. **Pruebas y CI:** el `Dockerfile` omite las pruebas al empaquetar; agrega una etapa de CI que ejecute la suite y pruebas específicas para seguridad, autenticación, categorías y ponentes.
7. **Perfil de Compose y CORS:** `docker-compose.yml` fija el perfil `dev`; además, CORS solo permite actualmente `http://localhost:4200`. Ajusta ambos valores al entorno real y evita exponer Swagger en producción sin una decisión explícita.
8. **Restricciones de usuarios:** `username` y `email` se comprueban en el controlador, pero no tienen restricciones de unicidad declaradas en la entidad `User`. Añade restricciones únicas a nivel de base de datos y maneja el conflicto para evitar duplicados por solicitudes simultáneas.

## Historial reciente del repositorio

El historial muestra la evolución desde la estructura inicial hacia la configuración Maven, variables de entorno y Docker, autenticación JWT y Spring Security, documentación OpenAPI, entidades y módulos CRUD, carga de datos de demostración, pruebas para eventos y exclusión de logs generados.

## Licencia

El repositorio no contiene actualmente un archivo `LICENSE`. Añade una licencia explícita si deseas definir los términos en los que otras personas pueden usar, modificar o redistribuir el proyecto.
