package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MenuCategory

// MenuItem entity is declared in MenuItem.kt
typealias MenuItemEntity = MenuItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val menuItemId: Long,
    val name: String,
    val price: Double,
    val quantity: Int,
    val specialInstructions: String = "",
    val cookingPreference: String = "", // e.g. "Medium Rare"
    val addedAddons: String = "" // e.g. "Black Truffle +$8"
)

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reservationCode: String,
    val guestName: String,
    val guestPhone: String,
    val guestEmail: String,
    val date: String,
    val timeSlot: String,
    val guestCount: Int,
    val tableNumber: Int,
    val zone: String,
    val specialRequests: String = "",
    val occasion: String = "Dining",
    val status: String = "CONFIRMED", // CONFIRMED, SEATED, CANCELLED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderCode: String,
    val orderType: String, // TABLE, ROOM_SERVICE, TAKEOUT
    val destination: String, // e.g. "Table #7" or "Suite #402"
    val guestName: String,
    val subtotal: Double,
    val taxAmount: Double,
    val tipAmount: Double,
    val grandTotal: Double,
    val paymentType: String,
    val paymentStatus: String, // COMPLETED, PENDING
    val transactionRef: String,
    val orderStatus: String, // ORDER_PLACED, PREPARING, PLATING, OUT_FOR_DELIVERY, SERVED
    val itemsSummary: String, // JSON or formatted string of items
    val timestamp: Long = System.currentTimeMillis()
)
