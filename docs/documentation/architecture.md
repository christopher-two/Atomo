# Arquitectura de la Capa de Datos

## Descripción General

La capa de datos de Atomo sigue los principios de **Clean Architecture** con:

- Estrategia **offline-first** usando Room como caché local
- **Patrón Repository** que abstrae las fuentes de datos
- **Casos de uso** que encapsulan la lógica de negocio
- **Koin DI** para inyección de dependencias

## Capas

### 1. Capa de Dominio

Ubicada en `domain/`:

- **Models** - Data classes puras de Kotlin
- **Interfaces de Repository** - Contratos para acceso a datos
- **Casos de Uso** - Operaciones de negocio de única responsabilidad

### 2. Capa de Datos

Ubicada en `data/`:

- **Local** - Base de datos Room, entidades, DAOs
- **Remote** - DTOs de Supabase y fuentes de datos remotas
- **Implementaciones de Repository** - Combinan local + remoto
- **Mappers** - Convierten entre domain/entity/DTO

## Flujo de Datos

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

## Estrategia Offline-First

1. **Lectura**: Siempre retorna datos de Room (vía Flow)
2. **Sincronización**: Obtiene de Supabase → Guarda en Room
3. **Escritura**: Escribe en Supabase → Actualiza Room en éxito

```kotlin
override fun getMenusFlow(userId: String): Flow<List<Menu>> {
    return menuDao.getMenusFlow(userId).map { entities ->
        entities.map { it.toDomain() }
    }
}

override suspend fun syncMenus(userId: String): Result<List<Menu>> = runCatching {
    val dtos = supabase.from("menus").select { filter { eq("user_id", userId) } }.decodeList<MenuDto>()
    menuDao.insertMenus(dtos.map { it.toEntity() })
    // ...
}
```

## Estructura de Paquetes

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
└── di/
    ├── data/
    │   ├── DatabaseModule.kt
    │   └── RepositoryModule.kt
    └── domain/
        └── UseCaseModule.kt
```
