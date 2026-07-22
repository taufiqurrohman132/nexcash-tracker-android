package com.example.nexcashapplication.feature.category.presentation.addnew.category;

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextSecondary
import com.example.nexcashapplication.ui.theme.TextTertiary


@Composable
fun AddCategoryScreen(
    viewModel: AddCategoryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddCategoryContent(state)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryContent(
    state: AddCategoryState,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Kategori",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // nama kategori
            FormField(
                title = "Nama Kategori",
                textStyle = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { },
                    placeholder = { Text(text = "Misal: Langganan Film", color = TextTertiary) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(PrimaryCyan),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
            }

            // Tipe Kategori
            FormField(title = "Tipe Kategori") {
                TipeKategoriToggle(
                    isExpense = state.isExpense,
                    onToggle = { }
                )
            }

            // Ikon & Warna
            IconColorRow(
                colorIndex = state.selectedColorIndex,
                onChangeClick = { }
            )
            ColorSwatchRow(
                colors = listOf(Color.Red, Color.Green, Color.Blue),
                selectedIndex = state.selectedColorIndex,
                onColorSelected = { }
            )

            // Rencana Anggaran
            RencanaAnggaranCard(
                budgetLimit = state.budgetLimit,
                onBudgetLimitChange = { },
                frequency = state.frequency,
                onFrequencyChange = { _ ->},
            )

            // rekening default
            FormField("Rekening Default"){
                CategoryDropdownField(
                    value = state.defaultAccount,
                    isPlaceHolder = false,
                    options = listOf("Aset", "Bank", "Investasi"),
                    onOptionSelected = { _ -> },
                )
            }

            FormField("Catatan Tambahan"){
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { _ -> },
                    placeholder = {
                        Text(
                            "Tambahkan deskripsi atau catatan khusus untuk kategori ini...",
                            color = Color(0xFFBBBBBB)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(PrimaryCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )
            }

            Spacer(Modifier.height(4.dp))

            // Tombol Aksi
            Button(
                onClick = {  },
                enabled = state.isValid,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C5CE7),
                    disabledContainerColor = Color(0xFFB8AEEB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Selesaikan Pembuatan",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            TextButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Batal & Kembali", color = Color(0xFF999999))
            }

            Spacer(modifier = Modifier.height(24.dp))

        }

    }

}



@Composable
fun ColorSwatchRow(colors: List<Color>, selectedIndex: Int, onColorSelected: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (index == selectedIndex) {
                            Modifier.border(2.dp, Color.Black, CircleShape)
                        } else Modifier
                    )
                    .clickable { onColorSelected(index) }
            )
        }
    }
}

@Composable
fun IconColorRow(colorIndex: Int, onChangeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Share, contentDescription = null
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Ikon & Warna", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = "Sesuaikan tampilan visual", fontSize = 12.sp, color = Color(0xFF999999))

        }
        OutlinedButton(
            onClick = onChangeClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6C5CE7)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(text = "Ubah", fontSize = 13.sp)
        }
    }

}

@Composable
fun TipeKategoriToggle(isExpense: Boolean, onToggle: (Boolean) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF0F0F0))
            .padding(4.dp)
    ) {
        ToggleOption(
            text = "Pengeluaran",
            selected = isExpense,
            modifier = Modifier.weight(1f),
            onClick = { onToggle(true) }
        )
        ToggleOption(
            text = "Pemasukan",
            selected = !isExpense,
            modifier = Modifier.weight(1f),
            onClick = { onToggle(false) }
        )
    }
}

@Composable
fun ToggleOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) PrimaryCyan else TextTertiary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownField(
    value: String,
    isPlaceHolder: Boolean,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { options ->
                DropdownMenuItem(
                    text = { Text(options) },
                    onClick = {
                        onOptionSelected(options)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RencanaAnggaranCard(
    budgetLimit: String,
    onBudgetLimitChange: () -> Unit,
    frequency: String,
    onFrequencyChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(4.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PrimaryCyan)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "Rencana Anggaran", fontWeight = FontWeight.SemiBold, fontSize = 16.sp )
        }

        FormField(title = "Batas Anggaran"){
            OutlinedTextField(
                value = budgetLimit,
                onValueChange = { },
                leadingIcon = { },
                placeholder = { },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(PrimaryCyan),
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            )
        }

        FormField(title = "Frekuensi"){
            CategoryDropdownField(
                modifier = Modifier.padding(top = 8.dp),
                value = frequency,
                isPlaceHolder = false,
                options = listOf("Harian", "Mingguan", "Bulanan"),
                onOptionSelected = onFrequencyChange
            )
        }
    }
}

@Preview
@Composable
private fun AddCategoryPreview() {
    NexCashApplicationTheme {
        AddCategoryScreen()
    }
}



