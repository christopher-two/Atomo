# Repositorios

Todos los repositorios siguen el **Patrón Repository** combinando Room (local) y Supabase (remoto).

## Interfaces

Ubicadas en `domain/repository/`:

| Interfaz               | Descripción                                 |
| ---------------------- | ------------------------------------------- |
| ProfileRepository      | Operaciones CRUD de perfil de usuario       |
| MenuRepository         | Menús, categorías y platillos               |
| PortfolioRepository    | Portafolios y elementos                     |
| CvRepository           | CVs, educación, experiencia, habilidades    |
| ShopRepository         | Tiendas, categorías de productos, productos |
| InvitationRepository   | Invitaciones y respuestas de invitados      |
| SubscriptionRepository | Planes y suscripciones de usuario           |

## ProfileRepository

```kotlin
interface ProfileRepository {
    fun getProfileFlow(userId: String): Flow<Profile?>
    suspend fun getProfile(userId: String): Profile?
    suspend fun getProfileByUsername(username: String): Profile?
    suspend fun syncProfile(userId: String): Result<Profile>
    suspend fun updateProfile(profile: Profile): Result<Profile>
    suspend fun deleteProfile(userId: String): Result<Unit>
}
```

## MenuRepository

```kotlin
interface MenuRepository {
    // Menú
    fun getMenusFlow(userId: String): Flow<List<Menu>>
    suspend fun getMenu(menuId: String): Menu?
    fun getMenuFlow(menuId: String): Flow<Menu?>
    suspend fun syncMenus(userId: String): Result<List<Menu>>
    suspend fun createMenu(menu: Menu): Result<Menu>
    suspend fun updateMenu(menu: Menu): Result<Menu>
    suspend fun deleteMenu(menuId: String): Result<Unit>

    // Categoría
    fun getCategoriesFlow(menuId: String): Flow<List<MenuCategory>>
    suspend fun createCategory(category: MenuCategory): Result<MenuCategory>

    // Platillo
    fun getDishesFlow(menuId: String): Flow<List<Dish>>
    suspend fun createDish(dish: Dish): Result<Dish>
    suspend fun updateDish(dish: Dish): Result<Dish>
    suspend fun deleteDish(dishId: String): Result<Unit>
}
```

## Implementaciones

Ubicadas en `data/repository/`:

Todas las implementaciones:

1. Inyectan DAO + SupabaseClient
2. Retornan Flow desde Room para lecturas
3. Sincronizan a Supabase y actualizan Room en escrituras
4. Usan `runCatching` para tipo Result

### Ejemplo: ProfileRepositoryImpl

```kotlin
class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val supabase: SupabaseClient
) : ProfileRepository {

    override fun getProfileFlow(userId: String): Flow<Profile?> {
        return profileDao.getProfileFlow(userId).map { it?.toDomain() }
    }

    override suspend fun syncProfile(userId: String): Result<Profile> = runCatching {
        val dto = supabase.from("profiles")
            .select { filter { eq("id", userId) } }
            .decodeSingle<ProfileDto>()

        val entity = dto.toEntity()
        profileDao.insertProfile(entity)
        entity.toDomain()
    }
}
```

## Mappers

Ubicados en `data/mapper/`:

Cada archivo de mapper contiene funciones de extensión:

- `Entity.toDomain()` - Entity → Modelo de Dominio
- `Domain.toEntity()` - Modelo de Dominio → Entity
- `Dto.toEntity()` - DTO → Entity
- `Domain.toDto()` - Modelo de Dominio → DTO

Archivos:

- ProfileMapper.kt
- MenuMapper.kt
- PortfolioMapper.kt
- CvMapper.kt
- ShopMapper.kt
- InvitationMapper.kt
- SubscriptionMapper.kt
