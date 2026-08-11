package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.formatFullCurrency
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.utils.MonthsRu

@Composable
fun AnnualReportScreen(
    monthlyIncomes: List<Double> = emptyList(),
    monthlyExpenses: List<Double> = emptyList(),
    modifier: Modifier = Modifier
) {
    val totalIncome = remember(monthlyIncomes) { monthlyIncomes.sum() }
    val totalExpense = remember(monthlyExpenses) { monthlyExpenses.sum() }
    val totalNet = totalIncome - totalExpense

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Итоговая карточка за год
        Card(
            colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.8f)),
            border = BorderStroke(1.dp, Slate800),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "ГОДОВОЙ ИТОГ",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Доходы", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = formatFullCurrency(totalIncome),
                            color = Emerald400,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(text = "Расходы", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = formatFullCurrency(totalExpense),
                            color = Rose500,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(text = "Сальдо", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = formatFullCurrency(totalNet),
                            color = if (totalNet >= 0) Emerald400 else Rose500,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Карточка с детализацией по месяцам
        Card(
            colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Slate800),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Детализация по месяцах",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Шапка таблицы
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBg)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "МЕСЯЦ",
                        color = Slate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ДОХОД",
                        color = Emerald400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "РАСХОД",
                        color = Rose500,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "САЛЬДО",
                        color = Slate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Список месяцев
                for (idx in MonthsRu.indices) {
                    val monthName = MonthsRu[idx]
                    val inc = monthlyIncomes.getOrElse(idx) { 0.0 }
                    val exp = monthlyExpenses.getOrElse(idx) { 0.0 }
                    val net = inc - exp

                    val incStr = formatFullCurrency(inc)
                    val expStr = formatFullCurrency(exp)
                    val netStr = formatFullCurrency(net)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = monthName,
                            color = Slate400,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = incStr,
                            color = Emerald400,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = expStr,
                            color = Rose500,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = netStr,
                            color = if (net >= 0) Emerald400 else Rose500,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
