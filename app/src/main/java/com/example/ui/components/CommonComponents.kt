package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiningMode
import com.example.data.model.OrderStatus
import com.example.ui.theme.EmeraldReserve
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated

@Composable
fun LuxuryHeader(
    title: String = "SAVORIA",
    subtitle: String = "GRAND DINING & HOTEL SUITES",
    selectedDiningMode: DiningMode,
    onDiningModeChanged: (DiningMode) -> Unit,
    guestName: String? = null,
    onSignOut: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ObsidianBlack,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = "Logo",
                                tint = GoldChampagne,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = GoldChampagne
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 5-Star Hotel Rating Pill or Guest Sign-out Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianSurfaceElevated)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .then(
                            if (onSignOut != null) Modifier.clickable { onSignOut() }
                            else Modifier
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = GoldChampagne,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (!guestName.isNullOrBlank()) "Sign out" else "5-Star Luxury",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dining Mode Switcher: Table Dining, Suite Room Service, Takeaway
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurfaceElevated)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DiningModeTab(
                    mode = DiningMode.TABLE,
                    label = "Table Dine-in",
                    icon = Icons.Default.Restaurant,
                    isSelected = selectedDiningMode == DiningMode.TABLE,
                    onClick = { onDiningModeChanged(DiningMode.TABLE) },
                    modifier = Modifier.weight(1f)
                )
                DiningModeTab(
                    mode = DiningMode.ROOM_SERVICE,
                    label = "Suite Service",
                    icon = Icons.Default.Hotel,
                    isSelected = selectedDiningMode == DiningMode.ROOM_SERVICE,
                    onClick = { onDiningModeChanged(DiningMode.ROOM_SERVICE) },
                    modifier = Modifier.weight(1f)
                )
                DiningModeTab(
                    mode = DiningMode.TAKEOUT,
                    label = "Takeaway",
                    icon = Icons.Default.ShoppingBag,
                    isSelected = selectedDiningMode == DiningMode.TAKEOUT,
                    onClick = { onDiningModeChanged(DiningMode.TAKEOUT) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DiningModeTab(
    mode: DiningMode,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) GoldChampagne else Color.Transparent,
        label = "tabBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) ObsidianBlack else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tabText"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .testTag("dining_mode_${mode.name}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DietaryBadge(
    tag: String,
    modifier: Modifier = Modifier
) {
    val isSignature = tag.contains("Signature", ignoreCase = true) || tag.contains("Chef", ignoreCase = true)
    val isGlutenFree = tag.contains("Gluten", ignoreCase = true)
    val isVegetarian = tag.contains("Vegetarian", ignoreCase = true)

    val bgColor = when {
        isSignature -> GoldContainer
        isGlutenFree || isVegetarian -> EmeraldReserve.copy(alpha = 0.2f)
        else -> ObsidianSurfaceElevated
    }

    val textColor = when {
        isSignature -> GoldLight
        isGlutenFree || isVegetarian -> EmeraldReserve
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OrderProgressStepper(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val steps = OrderStatus.values().toList()
    val currentIndex = currentStatus.stepIndex

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                val isCompleted = index <= currentIndex
                val isCurrent = index == currentIndex

                val circleBg = when {
                    isCompleted -> GoldChampagne
                    else -> ObsidianSurfaceElevated
                }
                val iconColor = when {
                    isCompleted -> ObsidianBlack
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                // Step indicator
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(circleBg)
                        .border(
                            1.dp,
                            if (isCurrent) GoldLight else ObsidianBorder,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                            tint = iconColor,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Connector line
                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                if (index < currentIndex) GoldChampagne else ObsidianBorder
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Current status description
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currentStatus.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentStatus.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
