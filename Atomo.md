
# Documentation Atomo App

## 1. Visión General

Atomo es una aplicación Android nativa desarrollada en Kotlin que sigue los principios de **Clean Architecture**. Está diseñada para ser modular, escalable y mantenible, con un fuerte enfoque en la calidad del código y las buenas prácticas.

**Principios Clave:**

- **Clean Architecture**: Separación clara de las capas de la aplicación (dominio, datos y presentación).
- **Offline-First**: La aplicación es funcional sin conexión a internet, utilizando una base de datos local como caché.
- **Inyección de Dependencias**: Utiliza **Koin** para gestionar las dependencias de la aplicación.
- **UI Declarativa**: La interfaz de usuario está construida con **Jetpack Compose**.
- **Reactiva**: Utiliza **Kotlin Flows** para manejar flujos de datos asíncronos.

## 2. Arquitectura de la Aplicación

La aplicación se divide en tres capas principales:

- **Capa de Dominio**: Contiene la lógica de negocio de la aplicación.
- **Capa de Datos**: Se encarga de la gestión de los datos, tanto locales como remotos.
- **Capa de Presentación**: Es responsable de la interfaz de usuario.

```
┌─────────────┐     ┌──────────────┐     ┌────────────────┐
│  ViewModel  │ ──▶ │  Caso de Uso │ ──▶ │  Repositorio   │
└─────────────┘     └──────────────┘     └────────────────┘
                                                  │
                          ┌───────────────────────┼───────────────────────┐
                          ▼                       ▼                       ▼
                    ┌──────────┐           ┌──────────┐           ┌──────────┐
                    │   DAO    │           │  Mapper  │           │ Supabase │
                    │  (Room)  │           │          │           │ (Remoto) │
                    └──────────┘           └──────────┘           └──────────┘
```

### 2.1. Capa de Dominio

La capa de dominio es el núcleo de la aplicación. Contiene:

- **Modelos de Dominio**: Clases de datos que representan las entidades de la aplicación.
- **Interfaces de Repositorio**: Contratos que definen cómo se accede a los datos.
- **Casos de Uso**: Clases que encapsulan la lógica de negocio de la aplicación.

### 2.2. Capa de Datos

La capa de datos se encarga de proporcionar los datos a la aplicación. Está compuesta por:

- **Repositorios**: Implementan las interfaces de repositorio de la capa de dominio.
- **Fuentes de Datos**: Pueden ser locales (base de datos Room) o remotas (API de Supabase).
- **Mappers**: Convierten los modelos de datos entre las diferentes capas.

### 2.3. Capa de Presentación

La capa de presentación es responsable de la interfaz de usuario. Utiliza:

- **Jetpack Compose**: Para construir la interfaz de usuario de forma declarativa.
- **ViewModels**: Para exponer los datos de la aplicación a la interfaz de usuario.
- **Navegación**: Utiliza la librería de **Navigation Compose** para gestionar la navegación entre pantallas.

## 3. Inyección de Dependencias

La aplicación utiliza **Koin** para la inyección de dependencias. Los módulos de Koin se encuentran en el paquete `di`.

**Módulos Principales:**

- **`DatabaseModule`**: Proporciona la instancia de la base de datos Room y los DAOs.
- **`RepositoryModule`**: Enlaza las interfaces de los repositorios con sus implementaciones.
- **`UseCaseModule`**: Proporciona los casos de uso de la aplicación.
- **`FeaturesModule`**: Proporciona los ViewModels y la navegación.

## 4. Base de Datos

La aplicación utiliza **Room** como base de datos local. Las entidades de Room se encuentran en el paquete `data.local.entity`.

**Estrategia Offline-First:**

1.  **Lectura**: Siempre se leen los datos de la base de datos local.
2.  **Sincronización**: Se obtienen los datos del servidor y se guardan en la base de datos local.
3.  **Escritura**: Se escriben los datos en el servidor y, si la operación es exitosa, se actualiza la base de datos local.

## 5. Backend

La aplicación utiliza **Supabase** como backend. La comunicación con la API de Supabase se realiza a través de la librería de Supabase para Kotlin.

## 6. UI

La interfaz de usuario está construida con **Jetpack Compose**. Los temas de la aplicación se encuentran en el paquete `ui.theme`.

**Componentes Reutilizables:**

La aplicación cuenta con una serie de componentes de Jetpack Compose reutilizables que se encuentran en el paquete `ui.components`.

## 7. Navegación

La navegación en la aplicación se gestiona con **Navigation Compose**. El grafo de navegación se define en el paquete `ui.navigation`.

## 8. Estructura del Proyecto

```
app/src/main/java/org/override/atomo/
├── data/
│   ├── local/
│   │   ├── AtomoDatabase.kt
│   │   ├── entity/
│   │   └── dao/
│   ├── remote/
│   │   └── dto/
│   ├── mapper/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── ui/
│   ├── components/
│   ├── navigation/
│   ├── theme/
│   └── view/
├── di/
└── MainActivity.kt
```
