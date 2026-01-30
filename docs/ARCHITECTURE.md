# Arquitectura de Atomo App

Este documento detalla las decisiones arquitectónicas y la estructura técnica de la aplicación.

## 1. Clean Architecture + Feature-Based

Atomo utiliza una combinación de **Clean Architecture** para la separación de preocupaciones y una organización **basada en características (Feature-Based)** para mejorar la modularidad.

### Capas (Layers)

1.  **Domain Layer (`domain/`)**:
    - Contiene la lógica de negocio pura.
    - **Models**: Clases de datos que representan el negocio.
    - **Repository Interfaces**: Contratos para el acceso a datos.
    - **Use Cases**: Lógica específica de una acción de negocio (ej. `SyncAllServicesUseCase`).

2.  **Data Layer (`data/`)**:
    - Implementa los repositorios definidos en el dominio.
    - **Local**: Gestión de la base de datos **Room**.
    - **Remote**: Implementación de la comunicación con **Supabase**.
    - **Mappers**: Conversión entre DTOs, Entidades y Modelos de Dominio.

3.  **Presentation Layer (`feature/`)**:
    - Organizada por módulos (ej. `auth`, `dashboard`, `cv`).
    - Cada módulo sigue el patrón **MVI (Model-View-Intent)** o **MVVM**:
      - **Action/Intent**: Representa las acciones del usuario.
      - **State**: Un único estado inmutable para la UI.
      - **ViewModel**: Gestiona el estado y los casos de uso.
      - **Screen/Root**: Componibles de Jetpack Compose.

## 2. Inyección de Dependencias (Koin)

Se utiliza **Koin** por su simplicidad y naturaleza idiomática en Kotlin.

- **`DataModule`**: Proporciona instancias de DataStore y librerías de sesión.
- **`SupabaseModule`**: Configura el cliente de Supabase (Auth, DB, Storage).
- **`DatabaseModule`**: Configura Room y proporciona los DAOs.
- **`RepositoryModule`**: Enlaza las interfaces del dominio con las implementaciones de datos.
- **`UseCaseModule`**: Instancia los casos de uso transversales.
- **`FeaturesModule`**: Agrupa todos los módulos de ViewModels y lógica de presentación.

## 3. Estrategia de Datos: Offline-First

Atomo está diseñada para funcionar sin conexión de forma transparente:

1.  **Lectura**: La UI observa flujos (Flows) provenientes de la base de datos local (Room).
2.  **Escritura**: Las actualizaciones se realizan primero en la base de datos local y se encolan o sincronizan con Supabase.
3.  **Sincronización**: Al iniciar la aplicación o bajo demanda, se sincronizan los deltas entre el servidor y el cliente.

## 4. Navegación (Navigation 3)

La aplicación utiliza la nueva API de **Navigation 3** de Android, la cual permite una navegación más declarativa y basada en estado, integrándose perfectamente con el ciclo de vida de los ViewModels.

## 5. Librerías de Soporte (`libs/`)

Para evitar el acoplamiento excesivo, se han extraído librerías internas especializadas:

- **`auth`**: Abstracción para proveedores de autenticación (Google Auth) y gestión de credenciales.
- **`biometric`**: Utilidades para integración con la API de Biometría de Android.
- **`image`**: Sistema de compresión y gestión de URIs de imágenes antes de la subida.
- **`session`**: Gestión segura y persistente de la sesión del usuario.
- **`settings`**: Gestión de preferencias globales mediante Jetpack DataStore.
- **`supabase`**: Configuración centralizada del cliente Supabase y sus módulos (Auth, Postgrest).
- **`validation`**: Framework de validación desacoplado para inputs de usuario.

## 6. Estrategia de Testing

Atomo prioriza la estabilidad mediante diferentes niveles de prueba:

- **Unit Tests (`test/`)**:
  - Pruebas de lógica de negocio en `ViewModels` usando **MockK** y **Turbine** (para flujos).
  - Pruebas de `Use Cases` y lógica pura de Kotlin.
- **Instrumented Tests (`androidTest/`)**:
  - Pruebas de persistencia para los **DAOs** de Room usando una base de datos in-memory.
  - Pruebas de integración para componentes de librería críticos.
- **UI Tests**: Implementación progresiva usando Compose Test Rule para verificar componentes críticos del Design System.
