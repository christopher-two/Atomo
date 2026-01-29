# Atomo App

![Logo](app/src/main/main/logo_app-playstore.png)

Atomo es una aplicación Android nativa de vanguardia diseñada para la gestión de servicios digitales y presencia profesional. Construida con **Kotlin** y las últimas tecnologías de Android, sigue los principios de **Clean Architecture** y una organización modular basada en características (feature-based).

## 🚀 Tecnologías Principales (Tech Stack)

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) con Material 3 y Adaptive Navigation Suite.
- **Lenguaje**: [Kotlin](https://kotlinlang.org/) con Corrutinas y Flows para programación reactiva.
- **Inyección de Dependencias**: [Koin](https://insert-koin.io/) (BOM).
- **Base de Datos Local**: [Room](https://developer.android.com/training/data-storage/room) con soporte offline-first.
- **Backend**: [Supabase](https://supabase.com/) (Auth, Postgrest, Realtime, Storage).
- **Navegación**: [Navigation 3](https://developer.android.com/jetpack/compose/navigation) (experimental) y Navigation Compose.
- **Almacenamiento Liviano**: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) para sesiones y configuración.
- **Imágenes**: [Coil](https://coil-kt.github.io/coil/) con motor Ktor 3.
- **Autenticación**: Google ID y Credentials API.
- **Utilidades UI**: Shimmer, Graphics Shapes, Morph Polygon, Material Kolor.
- **Pruebas**: JUnit 4, MockK, Turbine, Koin Test.

## 🏗️ Arquitectura

La aplicación implementa **Clean Architecture** dividida en capas para asegurar la escalabilidad y mantenibilidad. Para más detalles técnicos, consulta la [Documentación de Arquitectura](docs/ARCHITECTURE.md).

- **`core`**: Componentes UI comunes, temas, utilidades de navegación y gestores globales (SnackbarManager).
- **`data`**: Implementaciones de repositorios, DAOs de Room, entidades locales, DTOs de Supabase y mappers.
- **`domain`**: Modelos de dominio, interfaces de repositorios y casos de uso globales.
- **`feature`**: Módulos independientes por funcionalidad, cada uno con su propia lógica de presentación (MVI/MVVM).
- **`libs`**: Bibliotecas internas para abstracción de servicios como autenticación, biometría y gestión de sesiones.
- **`di`**: Configuración centralizada de módulos Koin.

## 📦 Estructura del Proyecto

```text
org.override.atomo/
├── core/             # Lógica y componentes compartidos
│   ├── common/       # Rutas y gestores comunes
│   └── ui/           # Temas y componentes base (AtomoButton, etc.)
├── data/             # Capa de datos (Local y Remoto)
├── di/               # Inyección de dependencias centralizada
├── domain/           # Entidades y casos de uso transversales
├── feature/          # Módulos por funcionalidad
│   ├── auth/         # Gestión de autenticación
│   ├── dashboard/    # Panel principal de control
│   ├── digital_menu/ # Gestión de menús digitales
│   ├── profile/      # Perfil de usuario y personalización
│   └── ...           # Otros módulos (cv, portfolio, shop, etc.)
├── libs/             # Librerías internas (Auth, Biometric, Session)
├── MainActivity.kt   # Punto de entrada de la UI
└── MainApp.kt        # Clase de aplicación y arranque de Koin
```

## ✨ Funcionalidades

- **Dashboard Inteligente**: Vista rápida de estadísticas y accesos directos.
- **Gestión de Servicios**: Creación y edición de CVs, Portafolios, Invitaciones, Menús Digitales y Tiendas.
- **Sincronización en Tiempo Real**: Uso de Supabase Realtime para mantener los datos actualizados.
- **Offline-First**: Funcionalidad completa sin conexión con sincronización posterior.
- **Autenticación Segura**: Integración con Google y manejo de sesiones persistentes.
- **Personalización**: Temas dinámicos y generadores de URLs personalizadas.
- **Biometría**: Seguridad adicional mediante huella digital o reconocimiento facial.

## 🛠️ Configuración

### Requisitos
- Android SDK 33+ (Min SDK 33, Target SDK 36)
- Android Studio Ladybug o superior
- Java 11

### Variables de Entorno
El proyecto requiere un archivo `local.properties` y `keystore.properties` para la configuración de Supabase y firmas de la aplicación.

## 🧪 Pruebas
Ejecuta las pruebas unitarias con:
```bash
./gradlew test
```

---
© 2026 Christopher Alejandro Maldonado Chávez. **Override**. Todos los derechos reservados.
Uruapan, Michoacán, México. | [atomo.click](https://atomo.click)
