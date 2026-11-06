package com.example.nexcashapplication.feature.category.presentation.delete

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.delete.model.CategoryDeleteItemUI
import com.example.nexcashapplication.feature.category.presentation.delete.model.DeleteOption
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeleteCategoryStateTest {

    private val sampleCategory = CategoryDeleteItemUI(
        id = "target-1",
        name = "Target Kategori",
        icon = CategoryIcons.fromId("other"),
        iconColor = Color.Blue,
    )

    @Test
    fun `MOVE_TO_CATEGORY - isConfirmEnabled hanya true jika targetCategory dipilih dan tidak sedang submitting`() {
        val stateWithoutTarget = DeleteCategoryState(
            selectedOption = DeleteOption.MOVE_TO_CATEGORY,
            targetCategory = null,
            isSubmitting = false,
        )
        assertFalse(stateWithoutTarget.isConfirmEnabled)

        val stateWithTarget = DeleteCategoryState(
            selectedOption = DeleteOption.MOVE_TO_CATEGORY,
            targetCategory = sampleCategory,
            isSubmitting = false,
        )
        assertTrue(stateWithTarget.isConfirmEnabled)

        val stateSubmitting = DeleteCategoryState(
            selectedOption = DeleteOption.MOVE_TO_CATEGORY,
            targetCategory = sampleCategory,
            isSubmitting = true,
        )
        assertFalse(stateSubmitting.isConfirmEnabled)
    }

    @Test
    fun `DELETE_ALL_TRANSACTIONS - isConfirmEnabled true jika tidak sedang submitting`() {
        val state = DeleteCategoryState(
            selectedOption = DeleteOption.DELETE_ALL_TRANSACTIONS,
            isSubmitting = false,
        )
        assertTrue(state.isConfirmEnabled)

        val stateSubmitting = DeleteCategoryState(
            selectedOption = DeleteOption.DELETE_ALL_TRANSACTIONS,
            isSubmitting = true,
        )
        assertFalse(stateSubmitting.isConfirmEnabled)
    }

    @Test
    fun `UNCATEGORIZED - isConfirmEnabled true jika tidak sedang submitting`() {
        val state = DeleteCategoryState(
            selectedOption = DeleteOption.UNCATEGORIZED,
            isSubmitting = false,
        )
        assertTrue(state.isConfirmEnabled)

        val stateSubmitting = DeleteCategoryState(
            selectedOption = DeleteOption.UNCATEGORIZED,
            isSubmitting = true,
        )
        assertFalse(stateSubmitting.isConfirmEnabled)
    }
}
