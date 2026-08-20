package com.example.nexcashapplication.feature.common.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val formattedText = originalText.reversed().chunked(3).joinToString(".").reversed()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val length = originalText.length
                if (length <= 3) return offset

                // panjang grup pertama (dari kiri), sisa dari pembagian 3 dari kanan
                val firstGroupLen = if (length % 3 == 0) 3 else length % 3
                if (offset < firstGroupLen) return offset

                val capped = offset.coerceAtMost(length - 1)
                val dots = (capped - firstGroupLen) / 3 + 1
                return offset + dots

            }

            override fun transformedToOriginal(offset: Int): Int {
                val capped = offset.coerceIn(0, formattedText.length)
                val dotsBeforeOffset = formattedText.take(capped).count { it == '.' }
                return (offset - dotsBeforeOffset).coerceIn(0, originalText.length)

            }

        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}