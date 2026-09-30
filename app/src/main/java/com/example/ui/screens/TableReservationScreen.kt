package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReservationEntity
import com.example.data.model.DiningTable
import com.example.data.model.TableStatus
import com.example.data.model.TableZone
import com.example.ui.theme.AmberPending
import com.example.ui.theme.EmeraldReserve
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.WineBurgundy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TableReservationScreen(
    tables: List<DiningTable>,
    reservations: List<ReservationEntity>,
    selectedZone: TableZone,
    selectedTable: DiningTable?,
    lastCreatedReservation: ReservationEntity?,
    onZoneSelected: (TableZone) -> Unit,
    onTableSelected: (DiningTable?) -> Unit,
    onBookReservation: (
        name: String,
        phone: String,
        email: String,
        date: String,
        timeSlot: String,
        guestCount: Int,
        tableNumber: Int,
        zone: String,
        specialRequests: String,
        occasion: String
    ) -> Unit,
    onDismissConfirmation: () -> Unit,
    onCancelReservation: (ReservationEntity) -> Unit
) {
    var reservationViewMode by remember { mutableStateOf("BOOK") } // "BOOK" or "MY_RESERVATIONS"

    // Booking form fields
    var guestName by remember { mutableStateOf("Victoria Sterling") }
    var guestPhone by remember { mutableStateOf("+1 (555) 392-8812") }
    var guestEmail by remember { mutableStateOf("v.sterling@hotelguest.com") }
    var selectedDate by remember { mutableStateOf("Today, Oct 1") }
    var selectedTime by remember { mutableStateOf("19:30") }
    var guestCount by remember { mutableIntStateOf(2) }
    var selectedOccasion by remember { mutableStateOf("Romantic Dinner") }
    var specialRequests by remember { mutableStateOf("Chilled Dom Pérignon waiting at table") }

    val zoneTables = tables.filter { it.zone == selectedZone }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Tab Row: Book New vs My Active Reservations
        TabRow(
            selectedTabIndex = if (reservationViewMode == "BOOK") 0 else 1,
            containerColor = ObsidianBlack,
            contentColor = GoldChampagne,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[if (reservationViewMode == "BOOK") 0 else 1]
                    ),
                    color = GoldChampagne
                )
            }
        ) {
            Tab(
                selected = reservationViewMode == "BOOK",
                onClick = { reservationViewMode = "BOOK" },
                text = {
                    Text(
                        "Floor Plan & Seating",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                modifier = Modifier.testTag("tab_book_table")
            )
            Tab(
                selected = reservationViewMode == "MY_RESERVATIONS",
                onClick = { reservationViewMode = "MY_RESERVATIONS" },
                text = {
                    Text(
                        "My Reservations (${reservations.filter { it.status == "CONFIRMED" }.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                modifier = Modifier.testTag("tab_my_reservations")
            )
        }

        if (reservationViewMode == "MY_RESERVATIONS") {
            // Display active and past reservations
            MyReservationsList(
                reservations = reservations,
                onCancelReservation = onCancelReservation
            )
        } else {
            // Floor Plan & Live Table Selection
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                // Zone Selector Header
                item {
                    Text(
                        text = "SELECT DINING ATMOSPHERE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldChampagne
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(TableZone.values()) { zone ->
                            val isSelected = selectedZone == zone
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) GoldChampagne else ObsidianSurfaceElevated)
                                    .border(1.dp, if (isSelected) GoldLight else ObsidianBorder, RoundedCornerShape(12.dp))
                                    .clickable {
                                        onZoneSelected(zone)
                                        onTableSelected(null)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("zone_chip_${zone.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = zone.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) ObsidianBlack else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• ${selectedZone.ambiance} (Min spend: $${String.format("%.0f", selectedZone.baseMinSpend)}/person)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Visual Seating Grid / Floor Plan
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Live Interactive Floor Plan",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Status Legend
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LegendIndicator(color = EmeraldReserve, label = "Available")
                                    LegendIndicator(color = AmberPending, label = "Reserved")
                                    LegendIndicator(color = WineBurgundy, label = "Occupied")
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Grid of tables
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                zoneTables.forEach { table ->
                                    val isSelected = selectedTable?.id == table.id
                                    TableCard(
                                        table = table,
                                        isSelected = isSelected,
                                        onClick = {
                                            if (table.status == TableStatus.AVAILABLE) {
                                                onTableSelected(if (isSelected) null else table)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Table Booking Form if a table is selected
                if (selectedTable != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reservation_form_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Selected Table #${selectedTable.tableNumber}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Serif
                                            ),
                                            color = GoldChampagne
                                        )
                                        Text(
                                            text = "${selectedTable.capacity} Guests • ${selectedTable.zone.title}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GoldContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Live Hold Active",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = ObsidianBorder)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Date Selector Chips
                                Text(
                                    text = "Dining Date",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(listOf("Today, Oct 1", "Tomorrow, Oct 2", "Friday, Oct 3", "Saturday, Oct 4")) { d ->
                                        val isSel = selectedDate == d
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) GoldChampagne else ObsidianSurface)
                                                .border(1.dp, if (isSel) GoldLight else ObsidianBorder, RoundedCornerShape(8.dp))
                                                .clickable { selectedDate = d }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = d,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = if (isSel) ObsidianBlack else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Time Slot Chips
                                Text(
                                    text = "Seating Time Slot",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("12:30", "13:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00").forEach { t ->
                                        val isSel = selectedTime == t
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) GoldChampagne else ObsidianSurface)
                                                .border(1.dp, if (isSel) GoldLight else ObsidianBorder, RoundedCornerShape(8.dp))
                                                .clickable { selectedTime = t }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = t,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSel) ObsidianBlack else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Guest Count
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Party Size: $guestCount Guests (Max ${selectedTable.capacity})",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        for (count in 1..selectedTable.capacity) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(if (guestCount == count) GoldChampagne else ObsidianSurface)
                                                    .clickable { guestCount = count },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$count",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (guestCount == count) ObsidianBlack else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Occasion Chips
                                Text(
                                    text = "Dining Occasion",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Romantic Dinner", "Anniversary Celebration", "Executive Banquet", "Hotel Stay Dining", "Birthday").forEach { occ ->
                                        val isSel = selectedOccasion == occ
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) GoldContainer else ObsidianSurface)
                                                .border(1.dp, if (isSel) GoldLight else ObsidianBorder, RoundedCornerShape(8.dp))
                                                .clickable { selectedOccasion = occ }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = occ,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSel) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Contact Info Inputs
                                OutlinedTextField(
                                    value = guestName,
                                    onValueChange = { guestName = it },
                                    label = { Text("Primary Guest Name") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reservation_name_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldChampagne,
                                        unfocusedBorderColor = ObsidianBorder,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = guestPhone,
                                        onValueChange = { guestPhone = it },
                                        label = { Text("Mobile Phone") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldChampagne,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedContainerColor = ObsidianSurface,
                                            unfocusedContainerColor = ObsidianSurface
                                        )
                                    )
                                    OutlinedTextField(
                                        value = guestEmail,
                                        onValueChange = { guestEmail = it },
                                        label = { Text("Confirmation Email") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldChampagne,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedContainerColor = ObsidianSurface,
                                            unfocusedContainerColor = ObsidianSurface
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = specialRequests,
                                    onValueChange = { specialRequests = it },
                                    label = { Text("Butler & Dietary Requests") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldChampagne,
                                        unfocusedBorderColor = ObsidianBorder,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Booking Submit Button
                                Button(
                                    onClick = {
                                        onBookReservation(
                                            guestName,
                                            guestPhone,
                                            guestEmail,
                                            selectedDate,
                                            selectedTime,
                                            guestCount,
                                            selectedTable.tableNumber,
                                            selectedZone.title,
                                            specialRequests,
                                            selectedOccasion
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldChampagne,
                                        contentColor = ObsidianBlack
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("confirm_reservation_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.EventSeat, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Confirm Real-Time Reservation",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Reservation Success Dialog
    if (lastCreatedReservation != null) {
        AlertDialog(
            onDismissRequest = onDismissConfirmation,
            containerColor = ObsidianSurface,
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(GoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = GoldChampagne,
                        modifier = Modifier.size(34.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Reservation Confirmed",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = GoldChampagne
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Booking Reference: ${lastCreatedReservation.reservationCode}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Table #${lastCreatedReservation.tableNumber} • ${lastCreatedReservation.zone}\n" +
                                "Guest: ${lastCreatedReservation.guestName} (${lastCreatedReservation.guestCount} guests)\n" +
                                "Date & Time: ${lastCreatedReservation.date} at ${lastCreatedReservation.timeSlot}\n" +
                                "Occasion: ${lastCreatedReservation.occasion}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (lastCreatedReservation.specialRequests.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Special Requests: ${lastCreatedReservation.specialRequests}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissConfirmation,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun TableCard(
    table: DiningTable,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isAvailable = table.status == TableStatus.AVAILABLE
    val isReserved = table.status == TableStatus.RESERVED
    val isOccupied = table.status == TableStatus.OCCUPIED

    val borderColor = when {
        isSelected -> GoldChampagne
        isAvailable -> EmeraldReserve.copy(alpha = 0.6f)
        isReserved -> AmberPending.copy(alpha = 0.4f)
        else -> WineBurgundy.copy(alpha = 0.4f)
    }

    val containerColor = when {
        isSelected -> GoldContainer
        isAvailable -> ObsidianSurfaceElevated
        else -> ObsidianSurface.copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = isAvailable, onClick = onClick)
            .padding(10.dp)
            .testTag("table_cell_${table.tableNumber}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "T-${table.tableNumber}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurface
                )
                if (table.isWindowSeat) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.Window,
                        contentDescription = "Window",
                        tint = GoldChampagne,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${table.capacity}p",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when {
                    isAvailable -> "Available"
                    isReserved -> "Reserved"
                    else -> "Occupied"
                },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = when {
                    isSelected -> GoldLight
                    isAvailable -> EmeraldReserve
                    isReserved -> AmberPending
                    else -> WineBurgundy
                },
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MyReservationsList(
    reservations: List<ReservationEntity>,
    onCancelReservation: (ReservationEntity) -> Unit
) {
    if (reservations.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.EventSeat,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No active table reservations.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Switch to 'Floor Plan & Seating' to reserve a luxury dining table.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(reservations, key = { it.id }) { res ->
                val isCancelled = res.status == "CANCELLED"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCancelled) ObsidianSurface.copy(alpha = 0.5f) else ObsidianSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCancelled) ObsidianBorder else GoldChampagne.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = res.reservationCode,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    color = if (isCancelled) MaterialTheme.colorScheme.onSurfaceVariant else GoldChampagne
                                )
                                Text(
                                    text = "${res.zone} • Table #${res.tableNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCancelled) WineBurgundy.copy(alpha = 0.3f) else EmeraldReserve.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = res.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCancelled) WineBurgundy else EmeraldReserve,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Date: ${res.date} at ${res.timeSlot} (${res.guestCount} guests)\n" +
                                    "Primary Guest: ${res.guestName} (${res.guestPhone})\n" +
                                    "Occasion: ${res.occasion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!isCancelled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { onCancelReservation(res) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WineBurgundy),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WineBurgundy.copy(alpha = 0.5f))
                            ) {
                                Text("Cancel Reservation", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
