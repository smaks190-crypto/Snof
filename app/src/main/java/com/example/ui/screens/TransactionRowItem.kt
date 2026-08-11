package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TransactionEntity
import com.example.ui.components.SwipeDirection
import com.example.ui.components.SwipeToRevealBox
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

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

    val colorAndIcon: Pair<Color, ImageVector> = getCategoryColorAndIcon(item.category, item.subcategory)
    val catColor: Color = colorAndIcon.first
    val catIcon: ImageVector = colorAndIcon.second

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
