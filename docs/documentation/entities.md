# Entidades Room

Todas las entidades Room están ubicadas en `data/local/entity/`.

## Resumen de Entidades

| Entidad                  | Tabla                | Llave Primaria | Llaves Foráneas                                 |
| ------------------------ | -------------------- | -------------- | ----------------------------------------------- |
| ProfileEntity            | profiles             | id (String)    | -                                               |
| MenuEntity               | menus                | id             | userId → profiles                               |
| MenuCategoryEntity       | menu_categories      | id             | menuId → menus                                  |
| DishEntity               | dishes               | id             | menuId → menus, categoryId → menu_categories    |
| PortfolioEntity          | portfolios           | id             | userId → profiles                               |
| PortfolioItemEntity      | portfolio_items      | id             | portfolioId → portfolios                        |
| CvEntity                 | cvs                  | id             | userId → profiles                               |
| CvEducationEntity        | cv_education         | id             | cvId → cvs                                      |
| CvExperienceEntity       | cv_experience        | id             | cvId → cvs                                      |
| CvSkillEntity            | cv_skills            | id             | cvId → cvs                                      |
| ShopEntity               | shops                | id             | userId → profiles                               |
| ProductCategoryEntity    | product_categories   | id             | shopId → shops                                  |
| ProductEntity            | products             | id             | shopId → shops, categoryId → product_categories |
| InvitationEntity         | invitations          | id             | userId → profiles                               |
| InvitationResponseEntity | invitation_responses | id             | invitationId → invitations                      |
| PlanEntity               | plans                | id             | -                                               |
| SubscriptionEntity       | subscriptions        | id             | userId → profiles, planId → plans               |

## Archivos

### ProfileEntity.kt

```kotlin
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val socialLinks: String?, // JSON
    val createdAt: Long,
    val updatedAt: Long
)
```

### MenuEntities.kt

Contiene: `MenuEntity`, `MenuCategoryEntity`, `DishEntity`

### PortfolioEntities.kt

Contiene: `PortfolioEntity`, `PortfolioItemEntity`

### CvEntities.kt

Contiene: `CvEntity`, `CvEducationEntity`, `CvExperienceEntity`, `CvSkillEntity`

### ShopEntities.kt

Contiene: `ShopEntity`, `ProductCategoryEntity`, `ProductEntity`

### InvitationEntities.kt

Contiene: `InvitationEntity`, `InvitationResponseEntity`

### SubscriptionEntities.kt

Contiene: `PlanEntity`, `SubscriptionEntity`

## DAOs

| DAO             | Entidades que Maneja                                           |
| --------------- | -------------------------------------------------------------- |
| ProfileDao      | ProfileEntity                                                  |
| MenuDao         | MenuEntity, MenuCategoryEntity, DishEntity                     |
| PortfolioDao    | PortfolioEntity, PortfolioItemEntity                           |
| CvDao           | CvEntity, CvEducationEntity, CvExperienceEntity, CvSkillEntity |
| ShopDao         | ShopEntity, ProductCategoryEntity, ProductEntity               |
| InvitationDao   | InvitationEntity, InvitationResponseEntity                     |
| SubscriptionDao | PlanEntity, SubscriptionEntity                                 |

Todos los DAOs soportan:

- **Queries basados en Flow** para datos reactivos
- **Funciones suspend** para operaciones one-shot
- **Resolución de conflictos** con `OnConflictStrategy.REPLACE`
