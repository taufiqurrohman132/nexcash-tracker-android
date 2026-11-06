package com.example.nexcashapplication.feature.category.domain.usecase

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteCategoryUseCaseTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var useCase: DeleteCategoryUseCase

    @BeforeEach
    fun setUp() {
        categoryRepository = FakeCategoryRepository()
        useCase = DeleteCategoryUseCase(categoryRepository)
    }

    @Test
    fun `invoke memanggil repository deleteCategory dengan resolution DeleteAllTransactions`() = runTest {
        categoryRepository.seed(
            Category(id = "cat-1", name = "Makanan", type = TypeCategory.EXPENSE, color = "#FF0000", iconId = "food"),
        )

        useCase("cat-1", CategoryDeleteResolution.DeleteAllTransactions)

        assertEquals("cat-1", categoryRepository.lastDeletedCategoryId)
        assertEquals(CategoryDeleteResolution.DeleteAllTransactions, categoryRepository.lastDeleteResolution)
    }

    @Test
    fun `invoke memanggil repository deleteCategory dengan resolution MoveToCategory`() = runTest {
        categoryRepository.seed(
            Category(id = "cat-1", name = "Makanan", type = TypeCategory.EXPENSE, color = "#FF0000", iconId = "food"),
            Category(id = "cat-2", name = "Lainnya", type = TypeCategory.EXPENSE, color = "#00FF00", iconId = "other"),
        )

        useCase("cat-1", CategoryDeleteResolution.MoveToCategory("cat-2"))

        assertEquals("cat-1", categoryRepository.lastDeletedCategoryId)
        assertEquals(CategoryDeleteResolution.MoveToCategory("cat-2"), categoryRepository.lastDeleteResolution)
    }

    @Test
    fun `invoke memanggil repository deleteCategory dengan resolution Uncategorize`() = runTest {
        categoryRepository.seed(
            Category(id = "cat-1", name = "Makanan", type = TypeCategory.EXPENSE, color = "#FF0000", iconId = "food"),
        )

        useCase("cat-1", CategoryDeleteResolution.Uncategorize)

        assertEquals("cat-1", categoryRepository.lastDeletedCategoryId)
        assertEquals(CategoryDeleteResolution.Uncategorize, categoryRepository.lastDeleteResolution)
    }
}
