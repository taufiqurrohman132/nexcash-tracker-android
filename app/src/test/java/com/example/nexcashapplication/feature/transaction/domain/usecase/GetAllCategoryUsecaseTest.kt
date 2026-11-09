package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAllCategoryUsecaseTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var useCase: GetAllCategoryUsecase

    @BeforeEach
    fun setUp() {
        categoryRepository = FakeCategoryRepository()
        useCase = GetAllCategoryUsecase(categoryRepository)
    }

    @Test
    fun `memanggil getAllCategory dari repository dan mengembalikan daftar kategori`() = runTest {
        val cat1 = Category(
            id = "cat-1",
            name = "Makanan",
            type = TypeCategory.EXPENSE,
            iconId = "food",
            color = "#FF0000",
        )
        val cat2 = Category(
            id = "cat-2",
            name = "Gaji",
            type = TypeCategory.INCOME,
            iconId = "salary",
            color = "#00FF00",
        )
        categoryRepository.seed(cat1, cat2)

        val result = useCase().first()

        assertEquals(2, result.size)
        assertEquals("Makanan", result[0]?.name)
        assertEquals("Gaji", result[1]?.name)
    }
}
