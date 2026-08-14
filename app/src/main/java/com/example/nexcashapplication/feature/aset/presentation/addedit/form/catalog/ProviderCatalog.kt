package com.example.nexcashapplication.feature.aset.presentation.addedit.form.catalog

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount
import com.example.nexcashapplication.feature.aset.presentation.addedit.form.model.AccountProviderUiModel


object ProviderCatalog {

    private val bankProviders = listOf(
        AccountProviderUiModel("bca", "BCA", TypeAccount.BANK, Color(0xFF0066B3)),
        AccountProviderUiModel("mandiri", "Mandiri", TypeAccount.BANK, Color(0xFFFFC107)),
        AccountProviderUiModel("bri", "BRI", TypeAccount.BANK, Color(0xFF00529C)),
        AccountProviderUiModel("bni", "BNI", TypeAccount.BANK, Color(0xFFFF6600)),
        AccountProviderUiModel("cimb", "CIMB Niaga", TypeAccount.BANK, Color(0xFFA6192E)),
        AccountProviderUiModel("permata", "Permata Bank", TypeAccount.BANK, Color(0xFF6CC24A)),
        AccountProviderUiModel("btn", "BTN", TypeAccount.BANK, Color(0xFF00458B)),
        AccountProviderUiModel("danamon", "Danamon", TypeAccount.BANK, Color(0xFFFFD100)),
        AccountProviderUiModel("btpn_jenius", "Jenius (BTPN)", TypeAccount.BANK, Color(0xFF3EC1D3)),
        AccountProviderUiModel("seabank", "SeaBank", TypeAccount.BANK, Color(0xFFEE4D2D)),
        AccountProviderUiModel("blu_bca", "blu by BCA Digital", TypeAccount.BANK, Color(0xFF0066B3)),
    )

    private val ewalletProviders = listOf(
        AccountProviderUiModel("gopay", "GoPay", TypeAccount.EWALLET, Color(0xFF00AED6)),
        AccountProviderUiModel("dana", "DANA", TypeAccount.EWALLET, Color(0xFF118EEA)),
        AccountProviderUiModel("shopeepay", "ShopeePay", TypeAccount.EWALLET, Color(0xFFEE4D2D)),
        AccountProviderUiModel("ovo", "OVO", TypeAccount.EWALLET, Color(0xFF4C3494)),
        AccountProviderUiModel("linkaja", "LinkAja", TypeAccount.EWALLET, Color(0xFFE4162F)),
        AccountProviderUiModel("isaku", "i.saku", TypeAccount.EWALLET, Color(0xFFFF6B00)),
    )

    private val investmentProviders = listOf(
        AccountProviderUiModel("bibit", "Bibit", TypeAccount.INVESTMENT, Color(0xFF4CAF50)),
        AccountProviderUiModel("ajaib", "Ajaib", TypeAccount.INVESTMENT, Color(0xFF2196F3)),
        AccountProviderUiModel("pluang", "Pluang", TypeAccount.INVESTMENT, Color(0xFF6C63FF)),
        AccountProviderUiModel("stockbit", "Stockbit", TypeAccount.INVESTMENT, Color(0xFF00C853)),
        AccountProviderUiModel("ipot", "IPOT", TypeAccount.INVESTMENT, Color(0xFFD32F2F)),
        AccountProviderUiModel("bareksa", "Bareksa", TypeAccount.INVESTMENT, Color(0xFF1976D2)),
        AccountProviderUiModel("pintu", "Pintu", TypeAccount.INVESTMENT, Color(0xFF0F1E3D)),
        AccountProviderUiModel("indodax", "Indodax", TypeAccount.INVESTMENT, Color(0xFF1C4587)),
    )

    private val allProviders = bankProviders + ewalletProviders + investmentProviders

    fun getByType(type: TypeAccount): List<AccountProviderUiModel> = when (type) {
        // Kartu & tabungan dianggap produk dari bank yang sama —
        // reuse daftar bank, bukan katalog terpisah.
        TypeAccount.BANK, TypeAccount.CARD, TypeAccount.DEBIT_CARD, TypeAccount.SAVINGS ->
            bankProviders

        TypeAccount.EWALLET -> ewalletProviders
        TypeAccount.INVESTMENT -> investmentProviders

        // Belum ada katalog provider untuk kategori ini — form perlu
        // sembunyikan section "Pilih Provider" kalau hasilnya kosong.
        TypeAccount.CASH,
        TypeAccount.PREPAID_CARD,
        TypeAccount.LOAN,
        TypeAccount.INSURANCE,
        TypeAccount.OTHER -> emptyList()
    }

    fun getById(id: String): AccountProviderUiModel? =
        allProviders.firstOrNull { it.id == id }
}