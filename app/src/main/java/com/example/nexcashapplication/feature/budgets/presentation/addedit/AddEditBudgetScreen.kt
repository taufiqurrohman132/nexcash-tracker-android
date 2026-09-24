package com.example.nexcashapplication.feature.budgets.presentation.addedit

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AreaChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.common.toLocalDate
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.common.components.AppDatePickerDialog
import com.example.nexcashapplication.feature.transaction.presentation.addnew.AmountOutlinedTextField
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun AddEditBudgetScreen(
    viewModel: AddEditBudgetViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddEditBudgetEffect.NavigateBack -> navController.popBackStack()
                is AddEditBudgetEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                AddEditBudgetEffect.ShowDiscardConfirmation -> navController.popBackStack()
            }
        }
    }

    AddEditBudgetContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBudgetContent(
    state: AddEditBudgetState,
    onEvent: (AddEditBudgetEvent) -> Unit,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Ubah Anggaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Kategori: ${state.kategoriNama}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddEditBudgetEvent.BatalClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
//                actions = {
//                    IconButton(onClick = { onEvent(AddEditBudgetEvent.OpenMenu) }) {
//                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
//                    }
//                },
            )
        },
        bottomBar = {
            EditBudgetBottomBar(
                onCancel = { onEvent(AddEditBudgetEvent.BatalClicked) },
                onSave = { onEvent(AddEditBudgetEvent.SimpanClicked) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
        ) {

            FormField(
                title = "Batas Anggaran Baru",
                icon = Icons.Outlined.AccountBalanceWallet,
            ) {
                AmountOutlinedTextField(
                    rawAmount = state.batasBaruParsed.toString(),
                    onAmountChange = {
                        onEvent(AddEditBudgetEvent.BatasBaruChanged(it))
                    },
                    prefix = {
                        Text("Rp ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

            }

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Batas sebelumnya adalah ${state.batasSebelumnya.formatCurrency()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(12.dp))
            PeriodTypeChipRow(
                selected = state.periodeTipe,
                onSelect = { onEvent(AddEditBudgetEvent.PeriodeTipeChanged(it)) },
            )

            FormField(
                title = "Periode Efektif",
                icon = Icons.Outlined.CalendarMonth,
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DateField(
                            label = "MULAI",
                            date = state.tanggalMulai.toString(),
                            enabled = state.periodeTipe == RecurrenceType.CUSTOM,
                            onClick = { onEvent(AddEditBudgetEvent.TanggalMulaiClicked(state.tanggalMulai)) },
                            modifier = Modifier.weight(1f),
                        )
                        DateField(
                            label = "BERAKHIR",
                            date = state.tanggalBerakhir.toString(),
                            enabled = state.periodeTipe == RecurrenceType.CUSTOM,
                            onClick = {
                                onEvent(
                                    AddEditBudgetEvent.TanggalBerakhirClicked(
                                        state.tanggalBerakhir,
                                    ),
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Text(
                        text = "Durasi ${state.durasiHari} Hari",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }

                if (state.isSaving) {
                    Spacer(Modifier.height(20.dp))
                    ChangeSummaryCard(
                        oldValue = state.batasSebelumnya.formatCurrency(),
                        newValue = state.batasBaruParsed.formatCurrency(),
                    )
                }

            }
            FormField(
                title = "Pengaturna Berulang",
                icon = Icons.Outlined.AreaChart,
            ) {
                RecurringBudgetCard(
                    isRecurring = state.isBerulang,
                    enabled = state.isBerulangToggleEnabled,
                    onToggle = { onEvent(AddEditBudgetEvent.BerulangToggled(it)) },
                    previewText = state.previewTeksBerulang,
                )
            }
        }

        // di Composable
        state.activeDateDialog?.let { target ->
            val initialDate = when (target) {
                DateFieldTarget.TanggalMulai -> state.tanggalMulai
                DateFieldTarget.TanggalAkhir -> state.tanggalBerakhir
            }

            AppDatePickerDialog(
                initialDateMillis = initialDate.toEpochMillis(zoneId = ZoneOffset.UTC),
                onDismiss = { onEvent(AddEditBudgetEvent.TanggalDismissClicked) },
                onDateSelected = { millis ->
                    val selected = millis.toLocalDate(zoneId = ZoneOffset.UTC)
                    onEvent(
                        when (target) {
                            DateFieldTarget.TanggalMulai -> AddEditBudgetEvent.TanggalMulaiChanged(selected)
                            DateFieldTarget.TanggalAkhir -> AddEditBudgetEvent.TanggalBerakhirChanged(selected)
                        }
                    )
                },
            )
        }
    }
}

@Composable
private fun NewLimitField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Text(
                "Rp",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
    )
}

@Composable
private fun PeriodTypeChipRow(selected: RecurrenceType, onSelect: (RecurrenceType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RecurrenceType.entries.forEach { type ->
            FilterChip(
                shape = CircleShape,
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(type.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

@Composable
private fun DateField(
    label: String,
    date: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedCard(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
        ) {
            Box(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart, // teks rata kiri, center vertikal
            ) {
                Text(
                    date,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}


@Composable
private fun RecurringBudgetCard(
    isRecurring: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    previewText: String,
) {

    Column(
        modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = PrimaryCyan,
                    spotColor = PrimaryCyan,
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Anggaran Berulang",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Otomatis perbarui anggaran ini setiap awal periode",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = isRecurring, onCheckedChange = { onToggle(it) }, enabled = enabled)
        }

        AnimatedVisibility(visible = isRecurring) {
            Column {
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        previewText,
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeSummaryCard(oldValue: String, newValue: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                alpha = 0.15f,
            ),
        ),
    ) {
        Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "RINGKASAN PERUBAHAN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Lama",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        oldValue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Baru",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        newValue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun EditBudgetBottomBar(onCancel: () -> Unit, onSave: () -> Unit) {
    Surface(shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
            ) {
                Text("Batal")
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
            ) {
                Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Preview(name = "Default - Monthly", showBackground = true)
@Composable
private fun AddEditBudgetContentPreview() {
    NexCashApplicationTheme {
        AddEditBudgetContent(
            state = AddEditBudgetState(
                categoryId = "cat_1",
                kategoriNama = "Makanan",
                batasSebelumnya = 2_000_000L,
                batasBaruParsed = 2_500_000L,
                periodeTipe = RecurrenceType.MONTHLY,
                tanggalMulai = LocalDate.of(2023, 10, 1),
                tanggalBerakhir = LocalDate.of(2023, 10, 31),
                isTanggalEditable = false,
                isBerulang = true,
                isBerulangToggleEnabled = false,
                isLoading = false,
                isFormValid = true,
            ),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(name = "Custom Period - Editable Dates", showBackground = true)
@Composable
private fun AddEditBudgetContentCustomPreview() {
    NexCashApplicationTheme {
        AddEditBudgetContent(
            state = AddEditBudgetState(
                categoryId = "cat_2",
                kategoriNama = "Transportasi",
                batasSebelumnya = 500_000L,
                batasBaruParsed = 750_000L,
                periodeTipe = RecurrenceType.CUSTOM,
                tanggalMulai = LocalDate.of(2023, 10, 5),
                tanggalBerakhir = LocalDate.of(2023, 11, 5),
                isTanggalEditable = true,
                isBerulang = false,
                isBerulangToggleEnabled = true,
                isLoading = false,
                isFormValid = true,
            ),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(name = "No Change Yet", showBackground = true)
@Composable
private fun AddEditBudgetContentNoChangePreview() {
    NexCashApplicationTheme {
        AddEditBudgetContent(
            state = AddEditBudgetState(
                categoryId = "cat_3",
                kategoriNama = "Hiburan",
                batasSebelumnya = 300_000L,
                batasBaruParsed = 300_000L, // sama persis -> hasChange = false
                periodeTipe = RecurrenceType.MONTHLY,
                tanggalMulai = LocalDate.now(),
                isBerulang = false,
                isLoading = false,
                isFormValid = false,
            ),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
//
//@Preview(name = "Loading", showBackground = true)
//@Composable
//private fun AddEditBudgetContentLoadingPreview() {
//    NexCashApplicationTheme {
//        AddEditBudgetContent(
//            state = AddEditBudgetState(isLoading = true),
//            onEvent = {},
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
//
//@Preview(name = "Error State", showBackground = true)
//@Composable
//private fun AddEditBudgetContentErrorPreview() {
//    NexCashApplicationTheme {
//        AddEditBudgetContent(
//            state = AddEditBudgetState(
//                kategoriNama = "Makanan",
//                batasSebelumnya = 2_000_000L,
//                batasBaruParsed = 0L,
//                isLoading = false,
//                isFormValid = false,
//                errorMessage = "Batas anggaran tidak boleh kosong"
//            ),
//            onEvent = {},
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
