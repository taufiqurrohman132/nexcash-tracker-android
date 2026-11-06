package com.example.nexcashapplication.feature.category.domain.usecase

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetCategoryByIdUseCaseTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var useCase: GetCategoryByIdUseCase

    @BeforeEach
    fun setUp() {
        categoryRepository = FakeCategoryRepository()
        useCase = GetCategoryByIdUseCase(categoryRepository)
    }

    @Test
    fun `invoke mengembalikan category ketika id ditemukan`() = runTest {
        val category = Category(
            id = "cat-1",
            name = "Makanan",
            type = TypeCategory.EXPENSE,
            color = "#FF0000",
            iconId = "food",
        )
        categoryRepository.seed(category)

        val result = useCase("cat-1").first()

        assertEquals(category, result)
    }

    @Test
    fun `invoke mengembalikan null ketika id tidak ditemukan`() = runTest {
        val result = useCase("unknown-id").first()

        assertNull(result)
    }
}
