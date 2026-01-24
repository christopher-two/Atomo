package org.override.atomo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import org.override.atomo.data.local.dao.CvDao
import org.override.atomo.data.local.dao.InvitationDao
import org.override.atomo.data.local.dao.MenuDao
import org.override.atomo.data.local.dao.PortfolioDao
import org.override.atomo.data.local.dao.ProfileDao
import org.override.atomo.data.local.dao.ShopDao
import org.override.atomo.data.local.dao.SubscriptionDao
import org.override.atomo.data.local.entity.CvEducationEntity
import org.override.atomo.data.local.entity.CvEntity
import org.override.atomo.data.local.entity.CvExperienceEntity
import org.override.atomo.data.local.entity.CvSkillEntity
import org.override.atomo.data.local.entity.DishEntity
import org.override.atomo.data.local.entity.InvitationEntity
import org.override.atomo.data.local.entity.InvitationResponseEntity
import org.override.atomo.data.local.entity.MenuCategoryEntity
import org.override.atomo.data.local.entity.MenuEntity
import org.override.atomo.data.local.entity.PlanEntity
import org.override.atomo.data.local.entity.PortfolioEntity
import org.override.atomo.data.local.entity.PortfolioItemEntity
import org.override.atomo.data.local.entity.ProductCategoryEntity
import org.override.atomo.data.local.entity.ProductEntity
import org.override.atomo.data.local.entity.ProfileEntity
import org.override.atomo.data.local.entity.ShopEntity
import org.override.atomo.data.local.entity.SubscriptionEntity

@Database(
    entities = [
        ProfileEntity::class,
        MenuEntity::class,
        MenuCategoryEntity::class,
        DishEntity::class,
        PortfolioEntity::class,
        PortfolioItemEntity::class,
        CvEntity::class,
        CvEducationEntity::class,
        CvExperienceEntity::class,
        CvSkillEntity::class,
        ShopEntity::class,
        ProductCategoryEntity::class,
        ProductEntity::class,
        InvitationEntity::class,
        InvitationResponseEntity::class,
        PlanEntity::class,
        SubscriptionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AtomoDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun menuDao(): MenuDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun cvDao(): CvDao
    abstract fun shopDao(): ShopDao
    abstract fun invitationDao(): InvitationDao
    abstract fun subscriptionDao(): SubscriptionDao
}
