# Casos de Uso

Todos los casos de uso están ubicados en `domain/usecase/` y agrupados en **data classes** por dominio.

## Patrón

Cada caso de uso:

1. Tiene una única función operador `invoke`
2. Recibe parámetros específicos
3. Retorna `Flow<T>` para datos reactivos o `Result<T>` para operaciones

## Grupos de Casos de Uso

### ProfileUseCases

```kotlin
data class ProfileUseCases(
    val getProfile: GetProfileUseCase,      // Flow<Profile?>
    val syncProfile: SyncProfileUseCase,    // Result<Profile>
    val updateProfile: UpdateProfileUseCase // Result<Profile>
)
```

### MenuUseCases

```kotlin
data class MenuUseCases(
    val getMenus: GetMenusUseCase,      // Flow<List<Menu>>
    val getMenu: GetMenuUseCase,        // Flow<Menu?>
    val syncMenus: SyncMenusUseCase,    // Result<List<Menu>>
    val createMenu: CreateMenuUseCase,  // Result<Menu>
    val updateMenu: UpdateMenuUseCase,  // Result<Menu>
    val deleteMenu: DeleteMenuUseCase,  // Result<Unit>
    val createCategory: CreateCategoryUseCase,
    val createDish: CreateDishUseCase,
    val updateDish: UpdateDishUseCase,
    val deleteDish: DeleteDishUseCase
)
```

### PortfolioUseCases

```kotlin
data class PortfolioUseCases(
    val getPortfolios: GetPortfoliosUseCase,
    val getPortfolio: GetPortfolioUseCase,
    val syncPortfolios: SyncPortfoliosUseCase,
    val createPortfolio: CreatePortfolioUseCase,
    val updatePortfolio: UpdatePortfolioUseCase,
    val deletePortfolio: DeletePortfolioUseCase,
    val createItem: CreatePortfolioItemUseCase,
    val updateItem: UpdatePortfolioItemUseCase,
    val deleteItem: DeletePortfolioItemUseCase
)
```

### CvUseCases

```kotlin
data class CvUseCases(
    val getCvs: GetCvsUseCase,
    val getCv: GetCvUseCase,
    val syncCvs: SyncCvsUseCase,
    val createCv: CreateCvUseCase,
    val updateCv: UpdateCvUseCase,
    val deleteCv: DeleteCvUseCase,
    val addEducation: AddEducationUseCase,
    val addExperience: AddExperienceUseCase,
    val addSkill: AddSkillUseCase
)
```

### ShopUseCases

```kotlin
data class ShopUseCases(
    val getShops: GetShopsUseCase,
    val getShop: GetShopUseCase,
    val syncShops: SyncShopsUseCase,
    val createShop: CreateShopUseCase,
    val updateShop: UpdateShopUseCase,
    val deleteShop: DeleteShopUseCase,
    val createCategory: CreateProductCategoryUseCase,
    val createProduct: CreateProductUseCase,
    val updateProduct: UpdateProductUseCase,
    val deleteProduct: DeleteProductUseCase
)
```

### InvitationUseCases

```kotlin
data class InvitationUseCases(
    val getInvitations: GetInvitationsUseCase,
    val getInvitation: GetInvitationUseCase,
    val syncInvitations: SyncInvitationsUseCase,
    val createInvitation: CreateInvitationUseCase,
    val updateInvitation: UpdateInvitationUseCase,
    val deleteInvitation: DeleteInvitationUseCase,
    val addResponse: AddResponseUseCase,
    val getConfirmedCount: GetConfirmedCountUseCase
)
```

### SubscriptionUseCases

```kotlin
data class SubscriptionUseCases(
    val getPlans: GetPlansUseCase,             // Flow<List<Plan>>
    val syncPlans: SyncPlansUseCase,           // Result<List<Plan>>
    val getSubscription: GetSubscriptionUseCase, // Flow<Subscription?>
    val syncSubscription: SyncSubscriptionUseCase,
    val cancelSubscription: CancelSubscriptionUseCase
)
```

## Uso en ViewModel

```kotlin
class MenuViewModel(
    private val menuUseCases: MenuUseCases
) : ViewModel() {

    val menus = menuUseCases.getMenus(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sync() {
        viewModelScope.launch {
            menuUseCases.syncMenus(userId)
                .onSuccess { /* Actualizado */ }
                .onFailure { /* Manejar error */ }
        }
    }

    fun createMenu(menu: Menu) {
        viewModelScope.launch {
            menuUseCases.createMenu(menu)
        }
    }
}
```
