# Inyección de Dependencias

Toda la configuración de DI usa **Koin** y está ubicada en `di/`.

## Módulos

### DatabaseModule

Ubicación: `di/data/DatabaseModule.kt`

Proporciona:

- Singleton de `AtomoDatabase`
- Los 7 DAOs

```kotlin
val DatabaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AtomoDatabase::class.java,
            "atomo_database"
        ).build()
    }

    single { get<AtomoDatabase>().profileDao() }
    single { get<AtomoDatabase>().menuDao() }
    single { get<AtomoDatabase>().portfolioDao() }
    single { get<AtomoDatabase>().cvDao() }
    single { get<AtomoDatabase>().shopDao() }
    single { get<AtomoDatabase>().invitationDao() }
    single { get<AtomoDatabase>().subscriptionDao() }
}
```

### RepositoryModule

Ubicación: `di/data/RepositoryModule.kt`

Enlaza interfaces a implementaciones:

```kotlin
val RepositoryModule = module {
    singleOf(::ProfileRepositoryImpl) bind ProfileRepository::class
    singleOf(::MenuRepositoryImpl) bind MenuRepository::class
    singleOf(::PortfolioRepositoryImpl) bind PortfolioRepository::class
    singleOf(::CvRepositoryImpl) bind CvRepository::class
    singleOf(::ShopRepositoryImpl) bind ShopRepository::class
    singleOf(::InvitationRepositoryImpl) bind InvitationRepository::class
    singleOf(::SubscriptionRepositoryImpl) bind SubscriptionRepository::class
}
```

### UseCaseModule

Ubicación: `di/domain/UseCaseModule.kt`

Proporciona todos los casos de uso y grupos:

```kotlin
val UseCaseModule = module {
    // Casos de uso individuales
    singleOf(::GetMenusUseCase)
    singleOf(::CreateMenuUseCase)
    // ...

    // Casos de uso agrupados
    singleOf(::MenuUseCases)
    singleOf(::ProfileUseCases)
    // ...
}
```

## Configuración en MainApp

Ubicación: `MainApp.kt`

```kotlin
class MainApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApp)
            modules(
                modules = FeaturesModule + DataModule + SupabaseModule +
                        DatabaseModule + RepositoryModule + UseCaseModule +
                        NavModule
            )
        }
    }
}
```

## Orden de Carga de Módulos

1. `SupabaseModule` - Cliente de Supabase
2. `DataModule` - DataStore, Session, Settings
3. `DatabaseModule` - Room DB + DAOs
4. `RepositoryModule` - Implementaciones de Repository
5. `UseCaseModule` - Casos de uso
6. `FeaturesModule` - ViewModels + Navegación
7. `NavModule` - Componentes de navegación

## Inyectando Dependencias

### En ViewModel

```kotlin
class MenuViewModel(
    private val menuUseCases: MenuUseCases
) : ViewModel()
```

### En Composable

```kotlin
@Composable
fun MenuScreen() {
    val viewModel: MenuViewModel = koinViewModel()
}
```
