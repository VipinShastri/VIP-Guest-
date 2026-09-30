package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.CartItemEntity
import com.example.data.model.DiningMode
import com.example.data.model.PaymentType
import com.example.ui.theme.EmeraldReserve
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.viewmodel.PaymentFormState

@Composable
fun CartCheckoutScreen(
    cartItems: List<CartItemEntity>,
    diningMode: DiningMode,
    orderLocation: String,
    tipPercentage: Int,
    splitBillCount: Int,
    paymentState: PaymentFormState,
    onOrderLocationChanged: (String) -> Unit,
    onTipPercentageChanged: (Int) -> Unit,
    onSplitBillCountChanged: (Int) -> Unit,
    onUpdateQuantity: (cartItemId: Long, newQuantity: Int) -> Unit,
    onRemoveItem: (cartItemId: Long) -> Unit,
    onPaymentTypeSelected: (PaymentType) -> Unit,
    onCardInfoUpdated: (number: String, exp: String, cvv: String, holder: String) -> Unit,
    onRoomFolioUpdated: (room: String, lastName: String) -> Unit,
    onProcessPayment: (guestName: String, destination: String, orderType: String) -> Unit,
    onBrowseMenu: () -> Unit
) {
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val tax = subtotal * 0.08
    val tip = subtotal * (tipPercentage / 100.0)
    val grandTotal = subtotal + tax + tip
    val perPersonSplit = if (splitBillCount > 0) grandTotal / splitBillCount else grandTotal

    var guestFullName by remember { mutableStateOf(paymentState.cardHolder) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty Cart",
                        tint = GoldChampagne,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Dining Cart is Empty",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore our exquisite menu chart and curate your meal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onBrowseMenu,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldChampagne,
                            contentColor = ObsidianBlack
                        ),
                        modifier = Modifier.testTag("browse_menu_from_cart_btn")
                    ) {
                        Text("Explore Menu Chart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
            ) {
                // Dining Location Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(GoldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (diningMode == DiningMode.ROOM_SERVICE) Icons.Default.Hotel else Icons.Default.PinDrop,
                                    contentDescription = null,
                                    tint = GoldChampagne,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Delivery Destination (${diningMode.title})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedTextField(
                                    value = orderLocation,
                                    onValueChange = onOrderLocationChanged,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("order_location_input"),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldChampagne,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }

                // Cart Items Section
                item {
                    Text(
                        text = "ORDER SUMMARY (${cartItems.sumOf { it.quantity }} ITEMS)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldChampagne
                    )
                }

                items(cartItems, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { onUpdateQuantity(item.id, item.quantity + 1) },
                        onDecrease = { onUpdateQuantity(item.id, item.quantity - 1) },
                        onDelete = { onRemoveItem(item.id) }
                    )
                }

                // Bill Splitter Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.People, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Split Bill Among Guests",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onSplitBillCountChanged(splitBillCount - 1) },
                                        enabled = splitBillCount > 1,
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = if (splitBillCount > 1) GoldChampagne else Color.Gray)
                                    }
                                    Text(
                                        text = "$splitBillCount ${if (splitBillCount == 1) "Guest" else "Guests"}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { onSplitBillCountChanged(splitBillCount + 1) },
                                        enabled = splitBillCount < 8,
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = if (splitBillCount < 8) GoldChampagne else Color.Gray)
                                    }
                                }
                            }

                            if (splitBillCount > 1) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GoldContainer)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = String.format("Split equally: $%.2f per guest across %d people", perPersonSplit, splitBillCount),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Tip Selection Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Hospitality Gratuity & Service",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(0, 15, 18, 20).forEach { percent ->
                                    val isSelected = tipPercentage == percent
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) GoldChampagne else ObsidianSurfaceElevated)
                                            .border(1.dp, if (isSelected) GoldLight else ObsidianBorder, RoundedCornerShape(10.dp))
                                            .clickable { onTipPercentageChanged(percent) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (percent == 0) "No Tip" else "$percent%",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) ObsidianBlack else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bill Totals Breakdown
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            BillRow(label = "Curation Subtotal", value = String.format("$%.2f", subtotal))
                            Spacer(modifier = Modifier.height(4.dp))
                            BillRow(label = "State & Luxury Dining Tax (8%)", value = String.format("$%.2f", tax))
                            Spacer(modifier = Modifier.height(4.dp))
                            BillRow(label = "Service & Gratuity ($tipPercentage%)", value = String.format("$%.2f", tip))

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = ObsidianBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Grand Total",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    color = GoldChampagne
                                )
                                Text(
                                    text = String.format("$%.2f", grandTotal),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    color = GoldChampagne
                                )
                            }
                        }
                    }
                }

                // Secure Payment Gateway Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("secure_payment_section"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldReserve, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Secure Payment Gateway",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = EmeraldReserve, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "256-Bit SSL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldReserve,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Payment Type Tabs
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PaymentType.values().forEach { pType ->
                                    val isSel = paymentState.paymentType == pType
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) GoldChampagne else ObsidianSurfaceElevated)
                                            .border(1.dp, if (isSel) GoldLight else ObsidianBorder, RoundedCornerShape(8.dp))
                                            .clickable { onPaymentTypeSelected(pType) }
                                            .padding(vertical = 8.dp, horizontal = 4.dp)
                                            .testTag("payment_type_${pType.name}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (pType) {
                                                PaymentType.CREDIT_CARD -> "Card"
                                                PaymentType.HOTEL_ROOM_FOLIO -> "Room Folio"
                                                PaymentType.DIGITAL_WALLET -> "Google Pay"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSel) ObsidianBlack else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            when (paymentState.paymentType) {
                                PaymentType.CREDIT_CARD -> {
                                    OutlinedTextField(
                                        value = paymentState.cardHolder,
                                        onValueChange = { onCardInfoUpdated(paymentState.cardNumber, paymentState.cardExpiry, paymentState.cardCvv, it) },
                                        label = { Text("Cardholder Name") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("card_holder_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldChampagne,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedContainerColor = ObsidianSurfaceElevated,
                                            unfocusedContainerColor = ObsidianSurfaceElevated
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = paymentState.cardNumber,
                                        onValueChange = { onCardInfoUpdated(it, paymentState.cardExpiry, paymentState.cardCvv, paymentState.cardHolder) },
                                        label = { Text("Card Number (PCI Tokenized)") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = GoldChampagne)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("card_number_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldChampagne,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedContainerColor = ObsidianSurfaceElevated,
                                            unfocusedContainerColor = ObsidianSurfaceElevated
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = paymentState.cardExpiry,
                                            onValueChange = { onCardInfoUpdated(paymentState.cardNumber, it, paymentState.cardCvv, paymentState.cardHolder) },
                                            label = { Text("Expiry (MM/YY)") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldChampagne,
                                                unfocusedBorderColor = ObsidianBorder,
                                                focusedContainerColor = ObsidianSurfaceElevated,
                                                unfocusedContainerColor = ObsidianSurfaceElevated
                                            )
                                        )
                                        OutlinedTextField(
                                            value = paymentState.cardCvv,
                                            onValueChange = { onCardInfoUpdated(paymentState.cardNumber, paymentState.cardExpiry, it, paymentState.cardHolder) },
                                            label = { Text("CVV (3-digit)") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldChampagne,
                                                unfocusedBorderColor = ObsidianBorder,
                                                focusedContainerColor = ObsidianSurfaceElevated,
                                                unfocusedContainerColor = ObsidianSurfaceElevated
                                            )
                                        )
                                    }
                                }

                                PaymentType.HOTEL_ROOM_FOLIO -> {
                                    OutlinedTextField(
                                        value = paymentState.roomNumber,
                                        onValueChange = { onRoomFolioUpdated(it, paymentState.hotelGuestLastName) },
                                        label = { Text("Hotel Suite / Room #") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("room_folio_number_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldChampagne,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedContainerColor = ObsidianSurfaceElevated,
                                            unfocusedContainerColor = ObsidianSurfaceElevated
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = paymentState.hotelGuestLastName,
                                        onValueChange = { onRoomFolioUpdated(paymentState.roomNumber, it) },
                                        label = { Text("Registered Guest Last Name") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("room_guest_lastname_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldChampagne,
                                            unfocusedBorderColor = ObsidianBorder,
                                            focusedContainerColor = ObsidianSurfaceElevated,
                                            unfocusedContainerColor = ObsidianSurfaceElevated
                                        )
                                    )
                                }

                                PaymentType.DIGITAL_WALLET -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ObsidianSurfaceElevated)
                                            .padding(14.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Ready to authorize via Google Pay Tokenized Biometrics",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GoldLight
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Authorize & Pay Button
                            Button(
                                onClick = {
                                    onProcessPayment(
                                        guestFullName.ifBlank { paymentState.cardHolder },
                                        orderLocation,
                                        diningMode.name
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldChampagne,
                                    contentColor = ObsidianBlack
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("authorize_payment_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = String.format("Authorize & Pay $%.2f", grandTotal),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Processing 3D-Secure Overlay Dialog
        if (paymentState.isProcessing) {
            Dialog(onDismissRequest = {}) {
                Card(
                    modifier = Modifier.width(300.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = GoldChampagne,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Securing Authorization...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldChampagne
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Connecting to 3D-Secure Bank Gateway & Tokenizing Folio Charge...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format("$%.2f each", item.price),
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldLight
                )

                if (item.cookingPreference.isNotBlank()) {
                    Text(
                        text = "Prep: ${item.cookingPreference}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (item.addedAddons.isNotBlank()) {
                    Text(
                        text = "Addons: ${item.addedAddons}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne
                    )
                }
                if (item.specialInstructions.isNotBlank()) {
                    Text(
                        text = "Note: ${item.specialInstructions}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quantity Modifier
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianSurfaceElevated)
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
                }
                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldChampagne,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = GoldChampagne, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun BillRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
    }
}
