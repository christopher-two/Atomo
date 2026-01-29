package org.override.atomo.domain.usecase.subscription

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription
import org.override.atomo.domain.model.SubscriptionStatus
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.repository.SubscriptionRepository

class CanAddDishUseCaseTest {

    private lateinit var canAddDishUseCase: CanAddDishUseCase
    private val subscriptionRepository: SubscriptionRepository = mockk()
    private val menuRepository: MenuRepository = mockk()
    private val getServiceLimitsUseCase = GetServiceLimitsUseCase()

    @Before
    fun setUp() {
        canAddDishUseCase = CanAddDishUseCase(subscriptionRepository, menuRepository, getServiceLimitsUseCase)
    }

    @Test
    fun `should return LimitReached when free user exceeds 10 dishes`() = runTest {
        // Arrange
        val userId = "user1"
        val menuId = "menu1"
        val freePlan = Plan("p1", "Free", null, 0.0, "USD", "month", emptyList(), true, 0L)
        val subscription = Subscription("s1", userId, "p1", SubscriptionStatus.ACTIVE, 0L, null, false, 0L, 0L)
        
        coEvery { subscriptionRepository.getSubscription(userId) } returns subscription
        coEvery { subscriptionRepository.getPlan("p1") } returns freePlan
        
        // Mock menu with 10 dishes
        val dishes = List(10) { mockk<org.override.atomo.domain.model.Dish>() }
        val menu = mockk<Menu> {
            coEvery { this@mockk.dishes } returns dishes
        }
        coEvery { menuRepository.getMenu(menuId) } returns menu

        // Act
        val result = canAddDishUseCase(userId, menuId)

        // Assert
        assertTrue(result is CanAddItemResult.LimitReached)
        assertEquals(10, (result as CanAddItemResult.LimitReached).limit)
    }

    @Test
    fun `should return Success when free user has less than 10 dishes`() = runTest {
        // Arrange
        val userId = "user1"
        val menuId = "menu1"
        val freePlan = Plan("p1", "Free", null, 0.0, "USD", "month", emptyList(), true, 0L)
        val subscription = Subscription("s1", userId, "p1", SubscriptionStatus.ACTIVE, 0L, null, false, 0L, 0L)
        
        coEvery { subscriptionRepository.getSubscription(userId) } returns subscription
        coEvery { subscriptionRepository.getPlan("p1") } returns freePlan
        
        val dishes = List(5) { mockk<org.override.atomo.domain.model.Dish>() }
        val menu = mockk<Menu> {
            coEvery { this@mockk.dishes } returns dishes
        }
        coEvery { menuRepository.getMenu(menuId) } returns menu

        // Act
        val result = canAddDishUseCase(userId, menuId)

        // Assert
        assertTrue(result is CanAddItemResult.Success)
    }
    
    private fun assertEquals(expected: Any, actual: Any) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
