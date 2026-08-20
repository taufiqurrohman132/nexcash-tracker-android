package com.example.nexcashapplication.feature.transaction.presentation.addnew

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.feature.common.components.AppDatePickerDialog
import com.example.nexcashapplication.feature.common.components.AppTimePickerDialog
import com.example.nexcashapplication.feature.common.transformation.ThousandSeparatorTransformation
import com.example.nexcashapplication.feature.common.utils.DateTimeFormatter
import com.example.nexcashapplication.feature.common.utils.formatDate
import com.example.nexcashapplication.feature.common.utils.formatTime
import com.example.nexcashapplication.feature.common.utils.hour
import com.example.nexcashapplication.feature.common.utils.minute
import com.example.nexcashapplication.feature.transaction.presentation.addnew.components.AccountBottomSheetContent
import com.example.nexcashapplication.feature.transaction.presentation.addnew.components.CategoryBottomSheetContent
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import kotlinx.coroutines.flow.collectLatest


@Composable
fun AddEditTransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditTransactionViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AddEditTransactionEffect.NavigateBack -> {
                    // Navigate back
                    navController.popBackStack()
                }

                AddEditTransactionEffect.NavigateToAddEditCategory -> {

                }

                is AddEditTransactionEffect.ShowSnackbar -> {

                }
            }
        }
    }
    AddEditTransactionContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionContent(
    state: AddEditTransactionState,
    onEvent: (AddEditTransactionEvent) -> Unit,
    modifier: Modifier,
) {
    var detailExpanded by remember { mutableStateOf(false) }
    val accent = PrimaryCyan // light blue, matching the design
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Transaction", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddEditTransactionEvent.OnBackClick) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* notifications */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                    }
                },
            )
        },
        bottomBar = {
            // STICKY — does not scroll with the content
            Surface(shadowElevation = 8.dp) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { onEvent(AddEditTransactionEvent.OnSaveClick) },
                        enabled = state.isValid,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            disabledContainerColor = accent.copy(alpha = 0.35f),
                        ),
                    ) {
                        Text(
                            "Save Transaction",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            // ---- Segmented toggle Expense / Income ----
            SegmentedTypeToggle(
                selected = state.type,
                onSelect = { onEvent(AddEditTransactionEvent.OnTypeChange(it)) },
            )

            Spacer(Modifier.height(16.dp))

            // ---- Amount (required field #1) ----
            EditableAmountCard(
                type = state.type,
                rawAmount = state.amount.toString(),
                onAmountChange = {
                    onEvent(AddEditTransactionEvent.OnAmountChange(it))
                },
            )


            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        title = "Date",
                    ) {
                        CompactSelector(
                            icon = Icons.Outlined.DateRange,
                            text = state.transactionAt.formatDate(),
                            isActive = state.showDatePicker,
                            onClick = { onEvent(AddEditTransactionEvent.OnDateClick) },
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        title = "Time",
                    ) {
                        CompactSelector(
                            icon = Icons.Outlined.Timer,
                            isActive = state.showTimePicker,
                            text = state.transactionAt.formatTime(),
                            onClick = { onEvent(AddEditTransactionEvent.OnTimeClick) },
                        )
                    }
                }
            }

            FormField(
                title = "Nama",
            ) {
                OutlinedTextField(
                    value = state.nameTransaction,
                    onValueChange = { onEvent(AddEditTransactionEvent.OnNameTransactionChange(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("Nama Transaksi") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderGray,
                    ),

                    )
            }


            // ---- Category (required field #2) ----
            FormField(
                "Category",
            ) {
                SelectorRow(
                    icon = state.selectedCategory?.icon?.icon ?: Icons.Outlined.Category,
                    iconBg = state.selectedCategory?.iconColor?.copy(alpha = 0.2f) ?: Color(0xFFDDF4FF),
                    title = state.selectedCategory?.name ?: "Select Category",
                    subtitle = "e.g. Food, Transportation...",
                    onClick = { onEvent(AddEditTransactionEvent.OnCategoryClick) },
                )
            }


            // ---- Funding Source / Account (required field #3) ----
            FormField(
                "Funding Source",
            ) {
                SelectorRow(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconBg = state.selectedAccount?.provider?.color?.copy(alpha = 0.2f) ?: Color(0xFFEDE7FF),
                    title = state.selectedAccount?.name ?: "Select Account",
                    subtitle = state.selectedAccount?.provider?.name ?: "Cash, Bank BCA, E-Wallet...",
                    onClick = { onEvent(AddEditTransactionEvent.OnAccountClick) },
                )
            }




            FormField(
                "Note",
            ) {
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { onEvent(AddEditTransactionEvent.OnNoteChange(it)) },
                    placeholder = { Text("Add a note here...") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                )
            }


            FormField(
                "Attachment",
            ) {
                AttachmentBox(
                    fileName = state.attachmentName,
                    onClick = { onEvent(AddEditTransactionEvent.OnAttachmentClick) },
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Recurring Transaction",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                }
                Switch(
                    checked = state.isRecurring,
                    onCheckedChange = { onEvent(AddEditTransactionEvent.OnRecurringChange(it)) },
                )
            }

            Spacer(Modifier.height(24.dp))
        }


        // dialog
        if (state.showDatePicker) {
            // DatePickerDialog
            AppDatePickerDialog(
                initialDateMillis = state.transactionAt.toEpochMilliseconds(),
                onDateSelected = {
                    onEvent(AddEditTransactionEvent.OnDateChange(it))
                },
                onDismiss = {
                    onEvent(AddEditTransactionEvent.OnDateClick)
                },
            )
        }
        if (state.showTimePicker) {
            // TimePickerDialog
            AppTimePickerDialog(
                initialHour = state.transactionAt.hour(),
                initialMinute = state.transactionAt.minute(),
                onTimeSelected = { hour, minute ->
                    onEvent(
                        AddEditTransactionEvent.OnTimeChange(
                            DateTimeFormatter.timeToInstant(
                                hour,
                                minute,
                            ),
                        ),
                    )
                },
                onDismiss = {
                    onEvent(AddEditTransactionEvent.OnTimeClick)
                },
            )
        }

        // sheet
        if (state.showCategoryBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    onEvent(AddEditTransactionEvent.OnCategoryClick)
                },
                sheetState = sheetState,
            ) {
                CategoryBottomSheetContent(
                    parentCategories = state.availableCategories.map { it.categoriItem },
                    subCategories = state.subCategories,
                    selectedParentId = state.selectedCategoryId,
                    onCategorySelected = {
                        onEvent(AddEditTransactionEvent.OnCategorySelected(it))
                    },
                )
            }
        }

        if (state.showAccountBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    onEvent(AddEditTransactionEvent.OnAccountClick)
                },
                sheetState = sheetState,
            ) {
                AccountBottomSheetContent(
                    accounts = state.availableAccounts,
                    selectedAccountId = state.selectedAccountId,
                    onAccountSelected = { account ->
                        onEvent(AddEditTransactionEvent.OnAccountSelected(account))
                    },
                )
            }
        }
    }
}

@Composable
private fun SegmentedTypeToggle(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit,
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
                .padding(2.dp),
    ) {
        ToggleChip(
            label = "Expense",
            icon = Icons.Default.Output,
            selected = selected == TransactionType.EXPENSE,
            selectedColor = Color(0xFFE8433D),
            modifier = Modifier.weight(1f),
            onClick = { onSelect(TransactionType.EXPENSE) },
        )
        Spacer(Modifier.width(4.dp))
        ToggleChip(
            label = "Income",
            icon = Icons.Default.ThumbUp,
            selected = selected == TransactionType.INCOME,
            selectedColor = PrimaryCyan,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(TransactionType.INCOME) },
        )
    }
}

@Composable
private fun ToggleChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) selectedColor else Color.Gray,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                color = if (selected) selectedColor else Color.Gray,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
fun EditableAmountCard(
    type: TransactionType,
    rawAmount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var amountField by remember {
        mutableStateOf(TextFieldValue(rawAmount, selection = TextRange(rawAmount.length)))
    }

    OutlinedTextField(
        value = amountField,
        onValueChange = { new ->
            val digitsOnly = new.text.filter { it.isDigit() }
            if (digitsOnly.length > 12) return@OutlinedTextField

            val normalized = digitsOnly.trimStart('0').ifEmpty { "0" }

            amountField = TextFieldValue(
                text = normalized,
                selection = TextRange(normalized.length), // selalu ke akhir
            )
            onAmountChange(normalized)
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = ThousandSeparatorTransformation(),
        textStyle = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        ),
        label = {
            Text(
                text = if (type == TransactionType.EXPENSE) "AMOUNT (OUT)" else "AMOUNT (IN)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        prefix = { Text("Rp", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BorderGray,
            focusedBorderColor = PrimaryCyan,
        ),
    )
}

@Composable
private fun SelectorRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, BorderGray), RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        IconCircular(
            icon = icon,
            iconColor = PrimaryCyan,
            backgroundColor = iconBg,
            iconToCountainerRatio = 2,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = BorderGray)
    }
}

@Composable
private fun CompactSelector(
    icon: ImageVector,
    text: String,
    isActive: Boolean = false,   // kontrol manual, misal true saat dialog kebuka
    onClick: () -> Unit,
) {
    val borderColor = if (isActive) PrimaryCyan else Color(0xFFE6E6E6)
    val boderWidth = if (isActive) 2.dp else 1.dp

    Row(
        modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(boderWidth, borderColor), RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onClick() }
                .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun AttachmentBox(fileName: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(
                    BorderStroke(1.5.dp, Color(0xFFD0D0D0)),
                    RoundedCornerShape(14.dp),
                ) // TODO: switch to a dashed border via Modifier.drawBehind + PathEffect for an exact match with the design
                .clickable { onClick() }
                .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color(0xFFE6E6E6)), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color.DarkGray)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            fileName ?: "Take a photo or choose a file",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(4.dp))
        Text("JPG, PNG OR PDF (MAX. 5MB)", fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
private fun ExpandableDetailHeader(expanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            "More Details (optional)",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = Color(0xFF17C3E8),
        )
        Icon(
            imageVector = if (expanded) Icons.Default.Email else Icons.Default.MoreVert,
            contentDescription = null,
            tint = Color(0xFF17C3E8),
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun AddTransactionPreview() {
    MaterialTheme {
        AddEditTransactionContent(
            state = AddEditTransactionState(),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}


