# Documentación de la Capa de Datos

Documentación completa de la implementación de la capa de datos de Atomo.

## Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                   Capa de Presentación                  │
│                      (ViewModels)                       │
├─────────────────────────────────────────────────────────┤
│                    Casos de Uso                         │
│  ProfileUseCases · MenuUseCases · PortfolioUseCases     │
│  CvUseCases · ShopUseCases · InvitationUseCases         │
│  SubscriptionUseCases                                   │
├─────────────────────────────────────────────────────────┤
│                     Repositorios                        │
│           Interfaz ← Implementación                     │
├───────────────────────┬─────────────────────────────────┤
│     Room (Local)      │      Supabase (Remoto)          │
│   17 Entidades        │    DTOs + Postgrest             │
│   7 DAOs              │                                 │
└───────────────────────┴─────────────────────────────────┘
```

## Índice de Documentación

| Documento                         | Descripción                            |
| --------------------------------- | -------------------------------------- |
| [Arquitectura](./architecture.md) | Desglose capa por capa                 |
| [Entidades](./entities.md)        | Entidades Room para las 17 tablas      |
| [Repositorios](./repositories.md) | Patrón Repository con Room + Supabase  |
| [Casos de Uso](./usecases.md)     | Lógica de negocio agrupada por dominio |
| [Módulos DI](./di.md)             | Configuración de Koin                  |

## Inicio Rápido

### Inyectando Casos de Uso

```kotlin
class MenuViewModel(
    private val menuUseCases: MenuUseCases
) : ViewModel() {

    val menus = menuUseCases.getMenus(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createMenu(menu: Menu) {
        viewModelScope.launch {
            menuUseCases.createMenu(menu)
        }
    }
}
```

### Grupos de Casos de Uso Disponibles

| Data Class             | Casos de Uso                                                                                                                         |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| `ProfileUseCases`      | getProfile, syncProfile, updateProfile                                                                                               |
| `MenuUseCases`         | getMenus, getMenu, syncMenus, createMenu, updateMenu, deleteMenu, createCategory, createDish, updateDish, deleteDish                 |
| `PortfolioUseCases`    | getPortfolios, getPortfolio, syncPortfolios, createPortfolio, updatePortfolio, deletePortfolio, createItem, updateItem, deleteItem   |
| `CvUseCases`           | getCvs, getCv, syncCvs, createCv, updateCv, deleteCv, addEducation, addExperience, addSkill                                          |
| `ShopUseCases`         | getShops, getShop, syncShops, createShop, updateShop, deleteShop, createCategory, createProduct, updateProduct, deleteProduct        |
| `InvitationUseCases`   | getInvitations, getInvitation, syncInvitations, createInvitation, updateInvitation, deleteInvitation, addResponse, getConfirmedCount |
| `SubscriptionUseCases` | getPlans, syncPlans, getSubscription, syncSubscription, cancelSubscription                                                           |
