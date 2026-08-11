package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.AiAuditEntity
import com.example.data.db.CategoryEntity
import com.example.data.db.TransactionEntity
import com.example.ui.components.DatePickerField
import com.example.ui.components.IncomeExpenseSummaryDialog
import com.example.ui.components.MarkdownFormattedText
import com.example.ui.components.ReportDetailsDialog
import com.example.ui.components.RollingCurrencyText
import com.example.ui.components.SwipeDirection
import com.example.ui.components.SwipeToRevealBox
import com.example.ui.components.SwipeToRevealController
import com.example.ui.components.dialogs.AllTransactionsDialog
import com.example.ui.components.dialogs.CategoryLimitsDialog
import com.example.ui.components.dialogs.ReceiptDetailsDialog
import com.example.ui.components.formatFullCurrency
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.BudgetViewModel
import com.example.ui.viewmodel.PeriodType
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch

val MonthsRu = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
)

@Composable
fun PeriodBudgetScreen(
    periodType: PeriodType,
    selectedDateDay: String,
    selectedMonthIdx: Int,
    selectedYear: Int = 2026,
    allPeriodStart: String,
    allPeriodEnd: String,
    filteredTransactions: List<TransactionEntity>,
    allTransactions: List<TransactionEntity> = emptyList(),
    activeSubTab: String,
    expandedExpense: Boolean,
    expandedIncome: Boolean,
    aiAuditResult: String?,
    aiAuditLoading: Boolean,
    savedAiAudit: AiAuditEntity? = null,
    isAppLocked: Boolean = false,
    viewModel: BudgetViewModel? = null,
    onSetPeriodType: (PeriodType) -> Unit,
    onChangeSelectedDay: (String) -> Unit,
    onChangeSelectedMonthIdx: (Int) -> Unit,
    onChangeSelectedAnnualYear: (Int) -> Unit = {},
    onChangeAllPeriodStart: (String) -> Unit,
    onChangeAllPeriodEnd: (String) -> Unit,
    onChangeActiveSubTab: (String) -> Unit,
    onToggleExpandExpense: () -> Unit,
    onToggleExpandIncome: () -> Unit,
    onRequestAiAudit: () -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onEditTransaction: ((TransactionEntity) -> Unit)? = null
) {
    var isVoiceActive by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var voiceStatusText by remember { mutableStateOf("Слушаю...") }
    var audioLevel by remember { mutableStateOf(0.6f) }

    val mainFilteredTransactions = remember(filteredTransactions) {
        filteredTransactions.filter { it.parentId.isNullOrBlank() }
    }

    var selectedReceiptTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

    val monthStart = remember(selectedMonthIdx, selectedYear) {
        val monthFormatted = String.format(Locale.US, "%02d", selectedMonthIdx + 1)
        "$selectedYear-$monthFormatted-01"
    }

    val monthEnd = remember(selectedMonthIdx, selectedYear) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonthIdx)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    val mainAllTransactions = remember(allTransactions) {
        allTransactions.filter { it.parentId.isNullOrBlank() }
    }

    val monthTransactions = remember(mainAllTransactions, monthStart, monthEnd) {
        mainAllTransactions.filter { it.date >= monthStart && it.date <= monthEnd }
    }

    val monthIncomes = remember(monthTransactions) { monthTransactions.filter { it.type == "income" } }
    val monthExpenses = remember(monthTransactions) { monthTransactions.filter { it.type == "expense" } }

    val monthTotalIncome = remember(monthIncomes) { monthIncomes.sumOf { it.amount } }
    val monthTotalExpense = remember(monthExpenses) { monthExpenses.sumOf { it.amount } }
    val monthNetBalance = monthTotalIncome - monthTotalExpense

    val previousCarryover = remember(mainAllTransactions, monthStart) {
        val prevIncomes = mainAllTransactions
            .filter { it.type == "income" && it.date < monthStart }
            .sumOf { it.amount }
        val prevExpenses = mainAllTransactions
            .filter { it.type == "expense" && it.date < monthStart }
            .sumOf { it.amount }

        prevIncomes - prevExpenses
    }

    val monthTotalAccumulatedBalance = previousCarryover + monthNetBalance
    val monthSavingsRate = if (monthTotalIncome > 0) Math.max(0, Math.round((monthNetBalance / monthTotalIncome) * 100)).toInt() else 0

    val incomes = remember(mainFilteredTransactions) { mainFilteredTransactions.filter { it.type == "income" } }
    val expenses = remember(mainFilteredTransactions) { mainFilteredTransactions.filter { it.type == "expense" } }

    val categoryExpenseTotals = remember(expenses) {
        expenses.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    var showCategoryLimitsDialog by remember { mutableStateOf(false) }
    var showAllTransactionsDialog by remember { mutableStateOf(false) }
    var initialAllTransactionsFilter by remember { mutableStateOf("all") }

    val categoriesListState = viewModel?.categories?.collectAsStateWithLifecycle()
    val categoriesList = categoriesListState?.value ?: remember { emptyList() }

    val pagerState = rememberPagerState(initialPage = if (activeSubTab == "expense") 0 else 1, pageCount = { 2 })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val targetSubTab = if (page == 0) "expense" else "income"
            if (targetSubTab != activeSubTab) {
                onChangeActiveSubTab(targetSubTab)
            }
        }
    }

    LaunchedEffect(activeSubTab) {
        val targetPage = if (activeSubTab == "expense") 0 else 1
        if (pagerState.currentPage != targetPage && !pagerState.isScrollInProgress) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    SwipeToRevealController.requestCollapseAll()
                }
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainBalanceCard(
                monthSavingsRate = monthSavingsRate,
                monthTotalAccumulatedBalance = monthTotalAccumulatedBalance,
                monthTotalIncome = monthTotalIncome,
                monthTotalExpense = monthTotalExpense,
                onIncomesClick = {
                    initialAllTransactionsFilter = "income"
                    showAllTransactionsDialog = true
                },
                onExpensesClick = {
                    initialAllTransactionsFilter = "expense"
                    showAllTransactionsDialog = true
                }
            )

            com.example.ui.components.charts.ExpenseDynamicsAreaChartCard(
                transactions = monthTransactions,
                title = "ДИНАМИКА РАСХОДОВ",
                onClick = null
            )

            CategoriesGrid(
                categoryExpenseTotals = categoryExpenseTotals,
                categoriesList = categoriesList,
                onShowLimitsClick = { showCategoryLimitsDialog = true }
            )

            if (showCategoryLimitsDialog && !isAppLocked) {
                CategoryLimitsDialog(
                    categories = categoriesList,
                    transactions = monthExpenses,
                    onUpdateLimit = { catName, limit ->
                        viewModel?.updateCategoryLimit(catName, "expense", limit)
                    },
                    onDismiss = { showCategoryLimitsDialog = false }
                )
            }

            if (showAllTransactionsDialog && !isAppLocked) {
                val currentPeriodInitialDate = if (periodType == PeriodType.DAY || periodType == PeriodType.WEEK) selectedDateDay else monthStart
                AllTransactionsDialog(
                    transactions = if (allTransactions.isNotEmpty()) allTransactions.filter { it.parentId.isNullOrBlank() } else mainFilteredTransactions,
                    allTransactions = allTransactions,
                    onDeleteTransaction = onDeleteTransaction,
                    onEditTransaction = onEditTransaction,
                    initialFilterType = initialAllTransactionsFilter,
                    initialDate = currentPeriodInitialDate,
                    onDismiss = { showAllTransactionsDialog = false }
                )
            }

            val recentTxList = remember(mainFilteredTransactions) {
                mainFilteredTransactions.take(10)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, Slate800.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ПОСЛЕДНИЕ ОПЕРАЦИИ",
                                color = Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Всего ${mainFilteredTransactions.size} транзакций",
                                color = Slate500,
                                fontSize = 10.sp
                            )
                        }

                        if (mainFilteredTransactions.isNotEmpty()) {
                            Text(
                                text = "Все ›",
                                color = Indigo500,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        initialAllTransactionsFilter = "all"
                                        showAllTransactionsDialog = true
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (recentTxList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Нет операций за выбранный период",
                                color = Slate500,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recentTxList.forEach { tx ->
                                val receiptItems = remember(allTransactions, tx.id) {
                                    allTransactions.filter { it.parentId.equals(tx.id, ignoreCase = true) }
                                }

                                TransactionRowItem(
                                    item = tx,
                                    onDelete = onDeleteTransaction,
                                    onClick = {
                                        if (receiptItems.isNotEmpty()) {
                                            selectedReceiptTransaction = tx
                                        } else if (onEditTransaction != null) {
                                            onEditTransaction(tx)
                                        }
                                    },
                                    canDelete = false
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        AnimatedVisibility(
            visible = isVoiceActive,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                VoiceInputNeuralVisualizer(
                    audioLevel = audioLevel,
                    isListening = true,
                    statusText = voiceStatusText
                )

                VoiceInputNeonCapsule(
                    recognizedText = recognizedText,
                    statusText = voiceStatusText,
                    assistantText = "Давид AI",
                    isListening = true,
                    onConfirm = {
                        isVoiceActive = false
                    },
                    onDismiss = {
                        isVoiceActive = false
                    }
                )
            }
        }
    }

    selectedReceiptTransaction?.let { parentTx ->
        val childItems = remember(allTransactions, parentTx.id) {
            allTransactions.filter { it.parentId.equals(parentTx.id, ignoreCase = true) }
        }
        ReceiptDetailsDialog(
            parentTransaction = parentTx,
            receiptItems = childItems,
            onDismiss = { selectedReceiptTransaction = null }
        )
    }
}

@Composable
fun VoiceInputNeuralVisualizer(
    audioLevel: Float = 0.5f,
    isListening: Boolean = true,
    statusText: String = "Слушаю...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NeuralPhase")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f * 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PhaseSpec"
    )

    val animatedAudioLevel by animateFloatAsState(
        targetValue = if (isListening) audioLevel.coerceIn(0.1f, 1f) else 0.05f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "AudioLevelAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Slate900.copy(alpha = 0.75f))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Emerald400.copy(alpha = 0.3f),
                            Indigo500.copy(alpha = 0.5f),
                            Rose500.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1f + animatedAudioLevel * 0.15f)
                    .blur(20.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Indigo500.copy(alpha = 0.35f * animatedAudioLevel),
                                Emerald400.copy(alpha = 0.2f * animatedAudioLevel),
                                Color.Transparent
                            )
                        )
                    )
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                val width = size.width
                val height = size.height
                val centerY = height / 2f

                val waveColors = listOf(
                    Emerald400 to 1.0f,
                    Indigo500 to 0.7f,
                    Rose500 to 0.5f
                )

                waveColors.forEachIndexed { index, (color, speedMult) ->
                    val path = Path()
                    val wavePhase = phase * speedMult + (index * 1.2f)
                    val baseAmplitude = (8.dp.toPx() + (animatedAudioLevel * 16.dp.toPx())) * (1f - index * 0.2f)

                    path.moveTo(0f, centerY)

                    var x = 0f
                    val step = 4f
                    while (x <= width) {
                        val normalX = x / width
                        val envelope = sin(normalX * Math.PI).toFloat()

                        val y = centerY + (
                            sin(normalX * 3.5 * Math.PI + wavePhase).toFloat() * 0.7f +
                            sin(normalX * 7.0 * Math.PI - wavePhase * 1.3f).toFloat() * 0.3f
                        ) * baseAmplitude * envelope

                        path.lineTo(x, y)
                        x += step
                    }

                    drawPath(
                        path = path,
                        color = color.copy(alpha = if (isListening) 0.85f else 0.3f),
                        style = Stroke(
                            width = (2.2f - index * 0.4f).dp.toPx()
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isListening) Emerald400 else Slate400)
                )
                Text(
                    text = statusText.uppercase(),
                    color = if (isListening) Emerald400 else Slate400,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun VoiceInputNeonCapsule(
    recognizedText: String,
    statusText: String = "Слушаю...",
    assistantText: String = "Давид AI",
    isListening: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MicGlowTransition")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MicScale"
    )

    val neonGradient = remember {
        Brush.linearGradient(
            colors = listOf(Emerald400, Indigo500, Rose500)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = Indigo500,
                spotColor = Emerald400
            )
            .clip(CircleShape)
            .background(Slate900.copy(alpha = 0.92f))
            .border(
                width = 1.5.dp,
                brush = neonGradient,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .scale(if (isListening) micScale else 1f)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(neonGradient)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Микрофон",
                            tint = Emerald400,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Emerald400)
                            .border(1.5.dp, DarkBg, CircleShape)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            color = Emerald400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = assistantText,
                            color = Slate500,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    MiniWaveformVisualizer(
                        isListening = isListening,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .padding(vertical = 2.dp)
                    )

                    Text(
                        text = if (recognizedText.isNotBlank()) "«$recognizedText»" else "Скажите сумму и категорию...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape, spotColor = Emerald400)
                        .clip(CircleShape)
                        .background(Emerald400)
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Подтвердить",
                        tint = DarkBg,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Rose500.copy(alpha = 0.15f))
                        .border(1.dp, Rose500.copy(alpha = 0.35f), CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Отмена",
                        tint = Rose500,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MiniWaveformVisualizer(
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Canvas(modifier = modifier) {
        val bars = 22
        val barWidth = size.width / bars
        val gradient = Brush.horizontalGradient(
            colors = listOf(Emerald400, Indigo500, Rose500)
        )

        for (i in 0 until bars) {
            val h = if (isListening) {
                (sin(phase + i * 0.4f) * 4f + cos(phase * 1.5f + i * 0.3f) * 2f + 5f).dp.toPx()
            } else {
                3.dp.toPx()
            }

            val x = i * barWidth + barWidth / 4f
            val y = (size.height - h) / 2f

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(barWidth / 2.2f, h),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}

@Composable
fun MainBalanceCard(
    monthSavingsRate: Int,
    monthTotalAccumulatedBalance: Double,
    monthTotalIncome: Double,
    monthTotalExpense: Double,
    onIncomesClick: () -> Unit,
    onExpensesClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Slate900)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Emerald400.copy(alpha = 0.4f),
                        Indigo500.copy(alpha = 0.4f),
                        Rose500.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ТЕКУЩИЙ БАЛАНС",
                    color = Slate400,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium
                )

                Surface(
                    shape = CircleShape,
                    color = Emerald400.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, Emerald400.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Emerald400,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Норма ${monthSavingsRate}%",
                            color = Emerald400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatFullCurrency(monthTotalAccumulatedBalance),
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onIncomesClick() }
                        .background(Emerald400.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .border(1.dp, Emerald400.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = Emerald400,
                                spotColor = Emerald400
                            )
                            .clip(CircleShape)
                            .background(Emerald400.copy(alpha = 0.12f))
                            .border(1.dp, Emerald400.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SouthWest,
                            contentDescription = null,
                            tint = Emerald400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Доходы", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = "+\u00A0${formatFullCurrency(monthTotalIncome)}",
                            color = Emerald400,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onExpensesClick() }
                        .background(Rose500.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .border(1.dp, Rose500.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = Rose500,
                                spotColor = Rose500
                            )
                            .clip(CircleShape)
                            .background(Rose500.copy(alpha = 0.12f))
                            .border(1.dp, Rose500.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NorthEast,
                            contentDescription = null,
                            tint = Rose500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Расходы", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = "-\u00A0${formatFullCurrency(monthTotalExpense)}",
                            color = Rose500,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriesGrid(
    categoryExpenseTotals: Map<String, Double>,
    categoriesList: List<CategoryEntity>,
    onShowLimitsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "КАТЕГОРИИ",
                color = Slate300,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Все категории",
                color = Indigo500,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onShowLimitsClick() }
            )
        }

        val defaultCategories = listOf(
            Triple("Гейминг", 22000.0, Emerald400 to Icons.Default.SportsEsports),
            Triple("Бары", 4800.0, Indigo500 to Icons.Default.LocalBar),
            Triple("Транспорт", 1420.0, Rose500 to Icons.Default.DirectionsCar),
            Triple("Продукты", 1150.0, Emerald400 to Icons.Default.Restaurant),
            Triple("Форс-мажор", 300.0, Indigo500 to Icons.Default.Warning)
        )

        val displayCategories = remember(categoryExpenseTotals, categoriesList) {
            if (categoryExpenseTotals.isNotEmpty()) {
                val colors = listOf(Emerald400, Indigo500, Rose500, Color(0xFFF59E0B), Color(0xFF06B6D4))
                val realSorted = categoryExpenseTotals.entries.sortedByDescending { it.value }.take(5)
                val realItems = realSorted.mapIndexed { idx, entry ->
                    val (col, ic) = when {
                        entry.key.contains("игра", true) || entry.key.contains("гейм", true) || entry.key.contains("аниме", true) -> Emerald400 to Icons.Default.SportsEsports
                        entry.key.contains("бар", true) || entry.key.contains("тусов", true) || entry.key.contains("алко", true) -> Indigo500 to Icons.Default.LocalBar
                        entry.key.contains("такси", true) || entry.key.contains("транс", true) || entry.key.contains("авто", true) -> Rose500 to Icons.Default.DirectionsCar
                        entry.key.contains("продукт", true) || entry.key.contains("еда", true) || entry.key.contains("кафе", true) -> Emerald400 to Icons.Default.Restaurant
                        entry.key.contains("форс", true) || entry.key.contains("авар", true) -> Indigo500 to Icons.Default.Warning
                        else -> colors[idx % colors.size] to Icons.Default.ShoppingBag
                    }
                    Triple(entry.key, entry.value, col to ic)
                }

                if (realItems.size < 5) {
                    val remainingDb = categoriesList
                        .filter { it.type == "expense" && realItems.none { item -> item.first.equals(it.name, true) } }
                        .mapIndexed { index, cat ->
                            val (col, ic) = when {
                                cat.name.contains("игра", true) || cat.name.contains("гейм", true) || cat.name.contains("аниме", true) -> Emerald400 to Icons.Default.SportsEsports
                                cat.name.contains("бар", true) || cat.name.contains("тусов", true) || cat.name.contains("алко", true) -> Indigo500 to Icons.Default.LocalBar
                                cat.name.contains("такси", true) || cat.name.contains("транс", true) || cat.name.contains("авто", true) -> Rose500 to Icons.Default.DirectionsCar
                                cat.name.contains("продукт", true) || cat.name.contains("еда", true) || cat.name.contains("кафе", true) -> Emerald400 to Icons.Default.Restaurant
                                cat.name.contains("форс", true) || cat.name.contains("авар", true) -> Indigo500 to Icons.Default.Warning
                                else -> colors[(realItems.size + index) % colors.size] to Icons.Default.ShoppingBag
                            }
                            Triple(cat.name, 0.0, col to ic)
                        }
                    (realItems + remainingDb).take(5)
                } else {
                    realItems
                }
            } else {
                val colors = listOf(Emerald400, Indigo500, Rose500, Color(0xFFF59E0B), Color(0xFF06B6D4))
                val expenseDbCats = categoriesList.filter { it.type == "expense" }
                if (expenseDbCats.isNotEmpty()) {
                    expenseDbCats.mapIndexed { idx, cat ->
                        val (col, ic) = when {
                            cat.name.contains("игра", true) || cat.name.contains("гейм", true) || cat.name.contains("аниме", true) -> Emerald400 to Icons.Default.SportsEsports
                            cat.name.contains("бар", true) || cat.name.contains("тусов", true) || cat.name.contains("алко", true) -> Indigo500 to Icons.Default.LocalBar
                            cat.name.contains("такси", true) || cat.name.contains("транс", true) || cat.name.contains("авто", true) -> Rose500 to Icons.Default.DirectionsCar
                            cat.name.contains("продукт", true) || cat.name.contains("еда", true) || cat.name.contains("кафе", true) -> Emerald400 to Icons.Default.Restaurant
                            cat.name.contains("форс", true) || cat.name.contains("авар", true) -> Indigo500 to Icons.Default.Warning
                            else -> colors[idx % colors.size] to Icons.Default.ShoppingBag
                        }
                        Triple(cat.name, 0.0, col to ic)
                    }.take(5)
                } else {
                    defaultCategories.map { Triple(it.first, 0.0, it.third) }
                }
            }
        }

        val maxCategoryVal = remember(displayCategories) {
            displayCategories.maxOfOrNull { it.second }?.coerceAtLeast(1.0) ?: 1.0
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            displayCategories.take(3).forEach { (name, amount, colorAndIcon) ->
                val (color, icon) = colorAndIcon
                val ratio = (amount / maxCategoryVal).toFloat().coerceIn(0.12f, 1f)

                Card(
                    onClick = { onShowLimitsClick() },
                    colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, Slate800.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(118.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color.copy(alpha = 0.12f))
                                .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = name,
                            color = Slate300,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 12.sp,
                            modifier = Modifier.height(26.dp)
                        )

                        Text(
                            text = formatFullCurrency(amount),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF020617))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = ratio)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            displayCategories.drop(3).take(2).forEach { (name, amount, colorAndIcon) ->
                val (color, icon) = colorAndIcon
                val ratio = (amount / maxCategoryVal).toFloat().coerceIn(0.12f, 1f)

                Card(
                    onClick = { onShowLimitsClick() },
                    colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, Slate800.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(118.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color.copy(alpha = 0.12f))
                                .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = name,
                            color = Slate300,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 12.sp,
                            modifier = Modifier.height(26.dp)
                        )

                        Text(
                            text = formatFullCurrency(amount),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF020617))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = ratio)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }

            Card(
                onClick = { onShowLimitsClick() },
                colors = CardDefaults.cardColors(containerColor = Indigo500.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, Indigo500.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(118.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Indigo500),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Добавить",
                        color = Indigo500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = Indigo500,
                        spotColor = Indigo500
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Indigo500 else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) Indigo500.copy(alpha = 0.8f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Slate400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

fun getCategoryColorAndIcon(category: String, subcategory: String): Pair<Color, androidx.compose.ui.graphics.vector.ImageVector> {
    val text = "$category $subcategory".lowercase()
    return when {
        text.contains("кредит") || text.contains("займ") || text.contains("ипотек") || text.contains("долг") || text.contains("банк") -> Pair(Rose500, Icons.Default.AccountBalance)
        text.contains("аптек") || text.contains("лекарст") || text.contains("здоровье") || text.contains("больниц") || text.contains("врач") || text.contains("медицин") -> Pair(Rose500, Icons.Default.MedicalServices)
        text.contains("сбережен") || text.contains("копилк") || text.contains("накоплен") || text.contains("вклад") || text.contains("инвест") || text.contains("фонд") || text.contains("цель") -> Pair(Indigo500, Icons.Default.Savings)
        text.contains("янндекс") || text.contains("yandex") -> Pair(Color(0xFFFC3F1D), Icons.Default.ShoppingBag)
        text.contains("сбер") || text.contains("перевод") || text.contains("тинькофф") || text.contains("карта") || text.contains("спб") -> Pair(Emerald400, Icons.Default.Refresh)
        text.contains("продукт") || text.contains("супермаркет") || text.contains("еда") || text.contains("магнит") || text.contains("пятерочк") || text.contains("ашан") || text.contains("магазин") -> Pair(Color(0xFFF59E0B), Icons.Default.ShoppingBag)
        text.contains("кафе") || text.contains("ресторан") || text.contains("фастфуд") || text.contains("доставк") || text.contains("кофе") || text.contains("столовая") -> Pair(Color(0xFFEC4899), Icons.Default.ShoppingBag)
        text.contains("транспорт") || text.contains("такси") || text.contains("авто") || text.contains("бензин") || text.contains("заправк") || text.contains("метро") || text.contains("автобус") -> Pair(Indigo500, Icons.Default.DirectionsCar)
        text.contains("развлечени") || text.contains("кино") || text.contains("игры") || text.contains("подписк") || text.contains("музык") || text.contains("театр") || text.contains("спорт") -> Pair(Color(0xFFA855F7), Icons.Default.Theaters)
        text.contains("жилье") || text.contains("коммунал") || text.contains("дом") || text.contains("жкх") || text.contains("аренд") || text.contains("квартир") -> Pair(Color(0xFF06B6D4), Icons.Default.Home)
        text.contains("связь") || text.contains("интернет") || text.contains("телефон") || text.contains("мобильн") || text.contains("техник") -> Pair(Color(0xFF3B82F6), Icons.Default.Call)
        text.contains("зарплат") || text.contains("доход") || text.contains("преми") || text.contains("аванс") || text.contains("кэшбэк") -> Pair(Emerald400, Icons.Default.Payments)
        text.contains("одежд") || text.contains("обувь") || text.contains("гардероб") || text.contains("сумка") || text.contains("красот") || text.contains("салон") -> Pair(Color(0xFFEC4899), Icons.Default.Checkroom)
        text.contains("подарок") || text.contains("подарк") || text.contains("праздник") || text.contains("цветы") -> Pair(Rose500, Icons.Default.CardGiftcard)
        text.contains("книг") || text.contains("литератур") || text.contains("чтени") || text.contains("литрес") -> Pair(Color(0xFF8B5CF6), Icons.Default.List)
        text.contains("обучени") || text.contains("образовани") || text.contains("курсы") || text.contains("школа") -> Pair(Color(0xFF60A5FA), Icons.Default.School)
        else -> Pair(Slate400, Icons.Default.List)
    }
}

@Composable
fun TransactionRowItem(
    item: TransactionEntity,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    canDelete: Boolean = true
) {
    val numberFormat = remember {
        val symbols = DecimalFormatSymbols(Locale("ru", "RU")).apply {
            groupingSeparator = ' '
            decimalSeparator = ','
        }
        DecimalFormat("#,##0.##", symbols).apply {
            isGroupingUsed = true
        }
    }
    val isExpense = item.type == "expense"
    val prefix = if (isExpense) "-" else "+"
    val (catColor, catIcon) = getCategoryColorAndIcon(item.category, item.subcategory)
    val hasSubcategory = item.subcategory.isNotBlank() && !item.subcategory.equals(item.category, ignoreCase = true)
    val topTitle = if (hasSubcategory) item.subcategory else item.category
    val bottomTitle = if (hasSubcategory) item.category else ""

    val content = @Composable {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkBg.copy(alpha = 0.6f))
                .border(BorderStroke(1.dp, Slate800.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(catColor.copy(alpha = 0.1f))
                        .border(0.5.dp, catColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = catIcon,
                        contentDescription = item.category,
                        tint = catColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topTitle,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (hasSubcategory) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (bottomTitle.isNotEmpty()) {
                        Text(
                            text = bottomTitle,
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$prefix${numberFormat.format(item.amount)} ₽",
                color = if (isExpense) Color.White else Emerald400,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (canDelete) {
        SwipeToRevealBox(
            swipeDirection = SwipeDirection.Both,
            onDelete = { onDelete(item.id) },
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
        ) {
            content()
        }
    } else {
        content()
    }
}
