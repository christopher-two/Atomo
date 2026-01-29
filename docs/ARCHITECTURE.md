# Arquitectura de Atomo App

Este documento detalla las decisiones arquitectónicas y la estructura técnica de la aplicación.

## 1. Clean Architecture + Feature-Based

Atomo utiliza una combinación de **Clean Architecture** para la separación de preocupaciones y una organización **basada en características (Feature-Based)** para mejorar la modularidad.

### Capas (Layers)

1.  **Domain Layer (`domain/`)**:
    *   Contiene la lógica de negocio pura.
    *   **Models**: Clases de datos que representan el negocio.
    *   **Repository Interfaces**: Contratos para el acceso a datos.
    *   **Use Cases**: Lógica específica de una acción de negocio (ej. `SyncAllServicesUseCase`).

2.  **Data Layer (`data/`)**:
    *   Implementa los repositorios definidos en el dominio.
    *   **Local**: Gestión de la base de datos **Room**.
    *   **Remote**: Implementación de la comunicación con **Supabase**.
    *   **Mappers**: Conversión entre DTOs, Entidades y Modelos de Dominio.

3.  **Presentation Layer (`feature/`)**:
    *   Organizada por módulos (ej. `auth`, `dashboard`, `cv`).
    *   Cada módulo sigue el patrón **MVI (Model-View-Intent)** o **MVVM**:
        *   **Action/Intent**: Representa las acciones del usuario.
        *   **State**: Un único estado inmutable para la UI.
        *   **ViewModel**: Gestiona el estado y los casos de uso.
        *   **Screen/Root**: Componibles de Jetpack Compose.

## 2. Inyección de Dependencias (Koin)

Se utiliza **Koin** por su simplicidad y naturaleza idiomática en Kotlin.

*   **`DataModule`**: Proporciona instancias de DataStore y librerías de sesión.
*   **`SupabaseModule`**: Configura el cliente de Supabase (Auth, DB, Storage).
*   **`DatabaseModule`**: Configura Room y proporciona los DAOs.
*   **`RepositoryModule`**: Enlaza las interfaces del dominio con las implementaciones de datos.
*   **`UseCaseModule`**: Instancia los casos de uso transversales.
*   **`FeaturesModule`**: Agrupa todos los módulos de ViewModels y lógica de presentación.

## 3. Estrategia de Datos: Offline-First

Atomo está diseñada para funcionar sin conexión de forma transparente:

1.  **Lectura**: La UI observa flujos (Flows) provenientes de la base de datos local (Room).
2.  **Escritura**: Las actualizaciones se realizan primero en la base de datos local y se encolan o sincronizan con Supabase.
3.  **Sincronización**: Al iniciar la aplicación o bajo demanda, se sincronizan los deltas entre el servidor y el cliente.

## 4. Navegación (Navigation 3)

La aplicación utiliza la nueva API de **Navigation 3** de Android, la cual permite una navegación más declarativa y basada en estado, integrándose perfectamente con el ciclo de vida de los ViewModels.

## 5. Librerías de Soporte (`libs/`)

Para evitar el acoplamiento excesivo, se han extraído librerías internas:

*   **`auth`**: Abstracción para Google Auth y gestión de credenciales.
*   **`biometric`**: Utilidades para autenticación biométrica de Android.
*   **`session`**: Gestión de la sesión de usuario persistente.
*   **`settings`**: Gestión de preferencias de la aplicación mediante DataStore.
