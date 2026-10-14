package com.example.nexcashapplication.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit test konversi hex ↔ [Color].
 *
 * DataStore menyimpan warna sebagai hex `#RRGGBB`; konversinya harus membulat balik
 * dengan tepat, dan string yang tidak terbaca harus jatuh ke warna bawaan — bukan
 * hitam, dan bukan crash.
 */
class ColorHexTest {

    @Test
    fun `warna membulat balik ke hex dan kembali utuh`() {
        listOf(PrimaryCyan, Color.Blue, Color.Yellow, Color.Black, Color.LightGray)
                .forEach { color ->
                    assertEquals(color, color.toHex().toComposeColor(), "gagal untuk ${color.toHex()}")
                }
    }

    @Test
    fun `toHex menghasilkan format yang diharapkan`() {
        assertEquals("#003DFF", PrimaryCyan.toHex())
        assertEquals("#0000FF", Color.Blue.toHex())
    }

    @Test
    fun `hex tanpa diawali pagar tetap terbaca`() {
        assertEquals(Color.Blue, "0000FF".toComposeColor())
    }

    @Test
    fun `string kosong jatuh ke warna bawaan, bukan hitam`() {
        assertEquals(PrimaryCyan, "".toComposeColor())
    }

    @Test
    fun `string tak terbaca jatuh ke warna bawaan`() {
        assertEquals(PrimaryCyan, "#GGGGGG".toComposeColor())
        assertEquals(PrimaryCyan, "#123".toComposeColor())
        assertEquals(PrimaryCyan, "biru".toComposeColor())
    }
}
