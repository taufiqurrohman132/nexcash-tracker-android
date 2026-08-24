package com.example.nexcashapplication.feature.transaction.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.component.IconRounded
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.presentation.detail.model.CategoryUiModel
import com.example.nexcashapplication.feature.common.extentions.toAmountColor
import com.example.nexcashapplication.feature.common.utils.formatDate
import com.example.nexcashapplication.feature.common.utils.formatTime
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.feature.transaction.presentation.detail.DetailTransactionEffect
import com.example.nexcashapplication.feature.transaction.presentation.detail.DetailTransactionEvent
import com.example.nexcashapplication.feature.transaction.presentation.detail.DetailTransactionState
import com.example.nexcashapplication.feature.transaction.presentation.detail.DetailTransactionViewModel
import com.example.nexcashapplication.feature.transaction.presentation.detail.components.DeleteTransactionDialog
import com.example.nexcashapplication.feature.transaction.presentation.detail.model.AttachmentUiModel
import com.example.nexcashapplication.feature.transaction.presentation.detail.model.DetailTransactionUiModel
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.TextSecondary
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun DetailTransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailTransactionViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DetailTransactionEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                is DetailTransactionEffect.NavigateToEdit -> {
                    navController.navigate(
                        Route.TransactionRoute.AddEdit(effect.transactionId),
                    )
                }

                is DetailTransactionEffect.ShareTransaction -> TODO()
                DetailTransactionEffect.ShowDeleteConfirmationDialog -> TODO()
                is DetailTransactionEffect.ShowSnackbar -> {

                }
            }
        }
    }

    DetailTransactionContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransactionContent(
    state: DetailTransactionState,
    onEvent: (DetailTransactionEvent) -> Unit,
    modifier: Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Transaksi",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DetailTransactionEvent.OnBackClick) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
        ) {
            if (state.transaction != null) {

                HeaderContent(
                    titleName = state.transaction.name,
                    amount = state.transaction.amount,
                    amountColor = state.transaction.type.toAmountColor(),
                    iconColor = Color.Black,
                    icon = Icons.Outlined.DirectionsCar,
                    categoryName = state.transaction.category?.name ?: "",
                    modifier = Modifier.fillMaxWidth(),
                )

                DetailContent(
                    date = state.transaction.dateTime.formatDate(),
                    time = state.transaction.dateTime.formatTime(),
                    categoryName = state.transaction.category?.name ?: "Unknown Category",
                    accountName = state.transaction.account?.name ?: "Unknown Account",
                    catatan = state.transaction.note ?: "",
                    type = state.transaction.type,
                )

                LampiranContent(

                )


                if (state.transaction.attachment != null) {
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Lampiran",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                                .clickable(onClick = { onEvent(DetailTransactionEvent.OnAttachmentClick) })
                                .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(BorderGray),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (state.transaction.attachment.thumbnailUri != null) {
                                AsyncImage(
                                    model = state.transaction.attachment.thumbnailUri,
                                    contentDescription = null,
                                    modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFF999999),
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = state.transaction.attachment.fileName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                            )
                            Text(
                                text = state.transaction.attachment.fileSizeLabel,
                                fontSize = 12.sp,
                                color = Color(0xFF999999),
                            )
                            Text(
                                text = "Lihat Fullscreen",
                                fontSize = 12.sp,
                                color = Color(0xFF2B9FE8),
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, BorderGray, RoundedCornerShape(20.dp))
                        .background(Color(0xFFF7F7F7))
                        .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ActionButton(
                    icon = Icons.Default.Edit,
                    label = "UBAH",
                    onClick = { onEvent(DetailTransactionEvent.OnEditClick) },
                )
                ActionButton(
                    icon = Icons.Default.ContentCopy,
                    label = "SALIN",
                    onClick = { onEvent(DetailTransactionEvent.OnDuplicateClick) },
                )
                ActionButton(
                    icon = Icons.Default.Share,
                    label = "BAGIKAN",
                    onClick = { onEvent(DetailTransactionEvent.OnShareClick) },
                )
                ActionButton(
                    icon = Icons.Default.Delete,
                    label = "HAPUS",
                    onClick = { onEvent(DetailTransactionEvent.OnDeleteClick) },
                    iconTint = Color(0xFFE94057),
                    labelColor = Color(0xFFE94057),
                    backgroundColor = Color(0xFFFDE9EC),
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "ID TRANSAKSI: ${state.transaction?.transactionId}",
                fontSize = 11.sp,
                color = Color(0xFFB0B0B0),
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
            )

        }

        if (state.deleteDialogState != null) {
            DeleteTransactionDialog(
                deleteDialogUiState = state.deleteDialogState,
                onConfirm = { onEvent(DetailTransactionEvent.OnDeleteConfirm) },
                onDismiss = { onEvent(DetailTransactionEvent.OnDismissDeleteDialog) },
            )
        }
    }
}

@Composable
fun LampiranContent() {

}


@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconTint: Color = Color(0xFF444444),
    labelColor: Color = Color(0xFF444444),
    backgroundColor: Color = Color.White,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        IconRounded(
            imageVector = icon,
            iconColor = iconTint,
            backgroundColor = backgroundColor,
            size = 44.dp,
        )

        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    type: TransactionType,
    date: String,
    time: String,
    categoryName: String,
    accountName: String,
    catatan: String,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        DetailContentRow(
            icon = Icons.Outlined.DateRange,
            label = "Tanggal & Waktu",
            value = "$date, $time WIB",
        )
        HorizontalDivider(color = BorderGray)
        DetailContentRow(
            icon = Icons.Outlined.Label,
            label = "Kategori",
            value = categoryName,
        )
        HorizontalDivider(color = BorderGray)
        DetailContentRow(
            icon = Icons.Outlined.AccountBalanceWallet,
            label = if (type == TransactionType.EXPENSE) "Sumber Dana" else "Diterima di",
            value = accountName,
        )
        HorizontalDivider(color = BorderGray)
        DetailContentRow(
            icon = Icons.Outlined.NoteAlt,
            label = "Catatan",
            value = catatan,
        )

    }
}

@Composable
fun DetailContentRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Icon(
            imageVector = icon,
            tint = TextSecondary,
            contentDescription = null,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun HeaderContent(
    titleName: String,
    amount: Long,
    amountColor: Color,
    iconColor: Color,
    icon: ImageVector,
    categoryName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
                .padding(24.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconCircular(
            icon = icon,
            iconColor = iconColor,
            backgroundColor = iconColor.copy(alpha = 0.1f),
            size = 68.dp,
            iconToCountainerRatio = 2,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "$titleName - $categoryName",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.total_balance_format, amount.formatCurrency()),
            style = MaterialTheme.typography.headlineLarge,
            color = amountColor,
            fontWeight = FontWeight.Bold,
        )


    }
}


@Preview
@Composable
private fun DetailTransactionScreenPreview() {
    DetailTransactionContent(
        state = DetailTransactionState(
            transaction = DetailTransactionUiModel(
                name = "Gojek - Transportasi",
                amount = 100000,
                attachment = AttachmentUiModel(
                    fileName = "Resi_Gojek_141023.jpg",
                    fileSizeLabel = "Image • 245 KB",
                    thumbnailUri = null,
                ),
                transactionId = "",
                type = TransactionType.EXPENSE,
                dateTime = Clock.System.now(),
                category = null,
                account = null,
                note = "",
            ),
        ),
        onEvent = {},
        modifier = Modifier,
    )
}