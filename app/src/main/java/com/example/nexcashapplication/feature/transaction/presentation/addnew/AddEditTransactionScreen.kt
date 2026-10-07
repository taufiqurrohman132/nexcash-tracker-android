package com.example.nexcashapplication.feature.transaction.presentation.addnew

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.AppButton
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.resolve
import com.example.nexcashapplication.feature.common.components.AppDatePickerDialog
import com.example.nexcashapplication.feature.common.components.AppTimePickerDialog
import com.example.nexcashapplication.feature.common.transformation.ThousandSeparatorTransformation
import com.example.nexcashapplication.feature.common.utils.formatDate
import com.example.nexcashapplication.feature.common.utils.formatTime
import com.example.nexcashapplication.feature.common.utils.hour
import com.example.nexcashapplication.feature.common.utils.minute
import com.example.nexcashapplication.feature.common.utils.toDatePickerMillis
import com.example.nexcashapplication.feature.transaction.presentation.addnew.components.AccountBottomSheetContent
import com.example.nexcashapplication.feature.transaction.presentation.addnew.components.CategoryBottomSheetContent
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import kotlinx.coroutines.launch

@Composable
fun AddEditTransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditTransactionViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // `collect`, bukan collectLatest: showSnackbar itu suspend, collectLatest
        // bisa membatalkannya di tengah jalan kalau effect berikutnya datang.
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddEditTransactionEffect.NavigateBack -> {
                    // Navigate back
                    navController.popBackStack()
                }

                is AddEditTransactionEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
                }
            }
        }
    }
    AddEditTransactionContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionContent(
    state: AddEditTransactionState,
    onEvent: (AddEditTransactionEvent) -> Unit,
    modifier: Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val accent = PrimaryCyan // light blue, matching the design

    // Satu sheetState per sheet: berbagi satu instance membuat sheet baru muncul
    // langsung `Expanded` (tanpa animasi masuk) karena state-nya masih dipakai
    // sheet sebelumnya. Tinggi konten tetap → skip partial supaya drag sedikit
    // tidak mengecup setengah tinggi.
    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accountSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetScope = rememberCoroutineScope()

    // Menutup lewat `hide()` dulu, baru state dibalik: sheet keluar dengan animasi
    // dan SheetState kembali `Hidden`, jadi buka berikutnya juga ikut animasi.
    val dismissCategorySheet: () -> Unit = {
        sheetScope.launch { categorySheetState.hide() }
                .invokeOnCompletion { cause ->
                    if (cause == null) onEvent(AddEditTransactionEvent.OnCategoryDismiss)
                }
    }
    val dismissAccountSheet: () -> Unit = {
        sheetScope.launch { accountSheetState.hide() }
                .invokeOnCompletion { cause ->
                    if (cause == null) onEvent(AddEditTransactionEvent.OnAccountDismiss)
                }
    }

    // dibungkus remember: daftar ini dihitung dari filter tipe, buang biar tidak
    // dibuat ulang tiap recomposition
    val parentCategories = remember(state.availableCategories) {
        state.availableCategories.map { it.categoriItem }
    }

    // --- alur cepat isi form: amount -> Nama -> sheet Category ---
    val amountFocus = remember { FocusRequester() }
    val nameFocus = remember { FocusRequester() }
    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    // Auto-focus amount + buka keyboard hanya saat TAMBAH BARU. Saat edit
    // transaksi lama keyboard tidak muncul sendiri supaya tidak mengganggu.
    val isAddMode = state.transactionId == null
    LaunchedEffect(isAddMode) {
        if (isAddMode) {
            amountFocus.requestFocus()
            keyboard?.show()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.tx_add_title),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddEditTransactionEvent.OnBackClick) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tx_back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            // STICKY — does not scroll with the content
            // imePadding: ikut terangkat saat keyboard muncul, jadi tidak tertimpa
            Surface(shadowElevation = 8.dp) {
                Box(modifier = Modifier.padding(16.dp)) {
                    // AppButton sudah fillMaxWidth + 52.dp + radius 16.dp,
                    // warna default PrimaryCyan sama dengan `accent` di layar ini
                    AppButton(
                        isEnabled = state.isValid && !state.isSaving,
                        // AppButton belum punya state simpan, jadi teksnya yang menandai proses
                        title = if (state.isSaving) {
                            stringResource(R.string.tx_saving)
                        } else {
                            stringResource(R.string.tx_save_transaction)
                        },
                        onClick = { onEvent(AddEditTransactionEvent.OnSaveClick) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                    .fillMaxSize()
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
            AmountOutlinedTextField(
                rawAmount = state.amount.toString(),
                onAmountChange = {
                    onEvent(AddEditTransactionEvent.OnAmountChange(it))
                },
                // tombol Next di keyboard: lompat ke field Nama
                imeAction = ImeAction.Next,
                onImeAction = { nameFocus.requestFocus() },
                focusRequester = amountFocus,
                label = {
                    Text(
                        text = if (state.type == TransactionType.EXPENSE) {
                            stringResource(R.string.tx_amount_out)
                        } else {
                            stringResource(R.string.tx_amount_in)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                prefix = {
                    Text(
                        stringResource(R.string.tx_currency_prefix),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                textStyle = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )


            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        title = stringResource(R.string.tx_date),
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
                        title = stringResource(R.string.tx_time),
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
                title = stringResource(R.string.tx_name),
            ) {
                OutlinedTextField(
                    value = state.nameTransaction,
                    onValueChange = { onEvent(AddEditTransactionEvent.OnNameTransactionChange(it)) },
                    modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nameFocus),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text(stringResource(R.string.tx_name_placeholder)) },
                    singleLine = true,
                    // Next di sini: tutup keyboard lalu langsung buka sheet Category
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            nameFocus.freeFocus()
                            keyboard?.hide()
                            // jalur cepat: setelah kategori dipilih lanjut ke sheet Account
                            onEvent(AddEditTransactionEvent.OnCategoryClick(autoAdvance = true))
                        },
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderGray,
                    ),

                    )
            }


            // ---- Category (required field #2) ----
            FormField(
                stringResource(R.string.tx_category),
            ) {
                SelectorRow(
                    icon = state.selectedCategory?.icon?.icon ?: Icons.Outlined.Category,
                    iconBg = state.selectedCategory?.iconColor?.copy(alpha = 0.2f) ?: Color(
                        0xFFDDF4FF,
                    ),
                    title = state.selectedCategory?.name
                        ?: stringResource(R.string.tx_select_category),
                    subtitle = stringResource(R.string.tx_select_category_hint),
                    onClick = { onEvent(AddEditTransactionEvent.OnCategoryClick()) },
                )
            }


            // ---- Funding Source / Account (required field #3) ----
            FormField(
                stringResource(R.string.tx_funding_source),
            ) {
                SelectorRow(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconBg = state.selectedAccount?.provider?.color?.copy(alpha = 0.2f) ?: Color(
                        0xFFEDE7FF,
                    ),
                    title = state.selectedAccount?.name
                        ?: stringResource(R.string.tx_select_account),
                    subtitle = state.selectedAccount?.provider?.name
                        ?: stringResource(R.string.tx_select_account_hint),
                    onClick = { onEvent(AddEditTransactionEvent.OnAccountClick) },
                )
            }




            FormField(
                stringResource(R.string.tx_note),
            ) {
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { onEvent(AddEditTransactionEvent.OnNoteChange(it)) },
                    placeholder = { Text(stringResource(R.string.tx_note_placeholder)) },
                    // Done: tutup keyboard, selesai mengisi
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboard?.hide() },
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Notes,
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
                stringResource(R.string.tx_attachment),
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
                        Icons.Outlined.Repeat,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.tx_recurring),
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
                initialDateMillis = state.transactionAt.toDatePickerMillis(),
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
                    onEvent(AddEditTransactionEvent.OnTimeChange(hour, minute))
                },
                onDismiss = {
                    onEvent(AddEditTransactionEvent.OnTimeClick)
                },
            )
        }

        // sheet
        if (state.showCategoryBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = dismissCategorySheet,
                sheetState = categorySheetState,
            ) {
                CategoryBottomSheetContent(
                    parentCategories = parentCategories,
                    subCategories = state.subCategories,
                    selectedParentId = state.selectedParentGroupId,
                    selectedSubId = state.selectedSubCategoryId,
                    isLoading = state.isCategoriesLoading,
                    onCategorySelected = { item ->
                        // Induk ber-sub menahan sheet: event langsung dikirim.
                        // Selainnya sheet menutup → tunggu animasi hide selesai dulu,
                        // supaya perpindahan ke sheet Account tidak terjadi dalam satu
                        // frame (sheet hilang mendadak, sheet baru muncul tanpa animasi).
                        if (state.keepsCategorySheetOpen(item.id)) {
                            onEvent(AddEditTransactionEvent.OnCategorySelected(item))
                        } else {
                            sheetScope.launch { categorySheetState.hide() }
                                    .invokeOnCompletion { cause ->
                                        if (cause == null) {
                                            onEvent(AddEditTransactionEvent.OnCategorySelected(item))
                                        }
                                    }
                        }
                    },
                )
            }
        }

        if (state.showAccountBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = dismissAccountSheet,
                sheetState = accountSheetState,
            ) {
                AccountBottomSheetContent(
                    accounts = state.availableAccounts,
                    selectedAccountId = state.selectedAccountId,
                    onAccountSelected = { account ->
                        // sama seperti kategori: animasi keluar dulu, baru state
                        sheetScope.launch { accountSheetState.hide() }
                                .invokeOnCompletion { cause ->
                                    if (cause == null) {
                                        onEvent(AddEditTransactionEvent.OnAccountSelected(account))
                                    }
                                }
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
    val inset = 2.dp
    val gap = 4.dp

    BoxWithConstraints(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF0F0F0)),
    ) {
        // Sama dengan hasil Modifier.weight(1f) di Row bawah:
        // (lebar track - padding kiri/kanan - jarak antar chip) / 2
        val chipWidth = ((maxWidth - inset * 2f - gap) / 2f).coerceAtLeast(0.dp)

        // Composable terpisah supaya hanya pil ini yang rekomposisi tiap frame
        // animasi; Row di bawahnya ikut membaca nilai animasi kalau disatukan.
        ToggleIndicator(
            trackWidth = maxWidth,
            chipWidth = chipWidth,
            inset = inset,
            gap = gap,
            selected = selected,
        )

        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(inset),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            ToggleChip(
                label = stringResource(TransactionType.EXPENSE.labelResId),
                icon = Icons.Default.TrendingDown,
                selected = selected == TransactionType.EXPENSE,
                selectedColor = Color(0xFFE8433D),
                modifier = Modifier.weight(1f),
                onClick = { onSelect(TransactionType.EXPENSE) },
            )
            ToggleChip(
                label = stringResource(TransactionType.INCOME.labelResId),
                icon = Icons.Default.TrendingUp,
                selected = selected == TransactionType.INCOME,
                selectedColor = PrimaryCyan,
                modifier = Modifier.weight(1f),
                onClick = { onSelect(TransactionType.INCOME) },
            )
        }
    }
}

/**
 * Pil putih yang menandai pilihan aktif, digambar **di belakang** isi track.
 *
 * Dideklarasi sebelum [Row] chip supaya ditempatkan lebih dulu (urutan tempat =
 * urutan gambar), lebar/sisinya diatur lewat padding dari satu nilai animasi
 * sehingga pil meluncur tanpa berubah lebar, dan tingginya ikut track lewat
 * `matchParentSize` — track ada di dalam `verticalScroll` sehingga tingginya
 * tidak bisa diturunkan dari constraint.
 */
@Composable
private fun BoxScope.ToggleIndicator(
    trackWidth: Dp,
    chipWidth: Dp,
    inset: Dp,
    gap: Dp,
    selected: TransactionType,
) {
    // Tepi kiri pil: Expense (kiri) -> Income (kanan). Satu nilai animasi untuk
    // dua sisi supaya lebar pil selalu tetap saat meluncur.
    val startPadding by animateDpAsState(
        targetValue = if (selected == TransactionType.EXPENSE) {
            inset
        } else {
            inset + chipWidth + gap
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "typeToggleIndicator",
    )

    val safeStart = startPadding.coerceAtLeast(0.dp)
    val safeEnd = (trackWidth - chipWidth - startPadding).coerceAtLeast(0.dp)
    val safeTop = inset.coerceAtLeast(0.dp)
    val safeBottom = inset.coerceAtLeast(0.dp)

    Box(
        modifier = Modifier
                .matchParentSize()
                .padding(
                    start = safeStart,
                    end = safeEnd,
                    top = safeTop,
                    bottom = safeBottom,
                )
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp)),
    )
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
    // warna ikut memudar selama pil meluncur, bukan berubah mendadak
    val tint by animateColorAsState(
        targetValue = if (selected) selectedColor else Color.Gray,
        label = "toggleChipTint",
    )

    Surface(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                // selectable, bukan clickable: pembaca layar mengumumkan "terpilih"
                .selectable(
                    selected = selected,
                    role = Role.Tab,
                    onClick = onClick,
                ),
        // background putih digambar pil di belakang, jadi chip tetap bening
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                color = tint,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
fun AmountOutlinedTextField(
    rawAmount: String,
    onAmountChange: (String) -> Unit,
    textStyle: TextStyle,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    // opsional supaya pemakai lain (AddEditBudgetScreen) tidak ikut berubah
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {
    var amountField by remember {
        mutableStateOf(TextFieldValue(rawAmount, selection = TextRange(rawAmount.length)))
    }

    LaunchedEffect(rawAmount) {
        if (rawAmount != amountField.text) {
            amountField = TextFieldValue(rawAmount, selection = TextRange(rawAmount.length))
        }
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
        modifier = modifier
                .fillMaxWidth()
                .then(
                    // focusRequester null -> tidak ditambahkan (pemakai lain)
                    if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier,
                ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() },
        ),
        visualTransformation = ThousandSeparatorTransformation(),
        textStyle = textStyle,
        label = label,
        prefix = prefix,
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
            Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = Color.DarkGray)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            fileName ?: stringResource(R.string.tx_take_photo),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.tx_attachment_hint),
            fontSize = 10.sp,
            color = Color.Gray,
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun AddTransactionPreview() {
    NexCashApplicationTheme {
        AddEditTransactionContent(
            state = AddEditTransactionState(),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}


