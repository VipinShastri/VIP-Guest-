package com.example.data.repository

import com.example.data.local.CartItemEntity
import com.example.data.local.MenuItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ReservationEntity
import com.example.data.local.SavoriaDao
import com.example.data.model.DiningTable
import com.example.data.model.TableStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

class SavoriaRepository(private val dao: SavoriaDao) {

    // Dynamic state of floor plan tables
    private val _tablesState = MutableStateFlow(PreseedData.initialTables)
    val tablesState: StateFlow<List<DiningTable>> = _tablesState.asStateFlow()

    suspend fun ensureInitialized() {
        val count = dao.getMenuItemCount()
        if (count == 0) {
            dao.insertMenuItems(PreseedData.initialMenuItems)
        }
    }

    // --- Menu ---
    fun getAllMenuItems(): Flow<List<MenuItemEntity>> = dao.getAllMenuItems()

    fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>> {
        return if (category == "ALL") {
            dao.getAllMenuItems()
        } else {
            dao.getMenuItemsByCategory(category)
        }
    }

    suspend fun getMenuItemById(id: Long): MenuItemEntity? = dao.getMenuItemById(id)

    suspend fun insertMenuItem(item: MenuItemEntity): Long = dao.insertMenuItem(item)

    // --- Cart ---
    fun getCartItems(): Flow<List<CartItemEntity>> = dao.getCartItems()

    suspend fun addToCart(
        menuItem: MenuItemEntity,
        quantity: Int = 1,
        specialInstructions: String = "",
        cookingPreference: String = "",
        addedAddons: String = ""
    ) {
        val existingItems = dao.getCartItems().firstOrNull() ?: emptyList()
        val existing = existingItems.firstOrNull {
            it.menuItemId == menuItem.id &&
            it.specialInstructions == specialInstructions &&
            it.cookingPreference == cookingPreference &&
            it.addedAddons == addedAddons
        }

        if (existing != null) {
            dao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            dao.insertCartItem(
                CartItemEntity(
                    menuItemId = menuItem.id,
                    name = menuItem.name,
                    price = menuItem.price,
                    quantity = quantity,
                    specialInstructions = specialInstructions,
                    cookingPreference = cookingPreference,
                    addedAddons = addedAddons
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItem(cartItemId)
        } else {
            val items = dao.getCartItems().firstOrNull() ?: return
            val item = items.firstOrNull { it.id == cartItemId } ?: return
            dao.updateCartItem(item.copy(quantity = newQuantity))
        }
    }

    suspend fun removeCartItem(cartItemId: Long) {
        dao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    // --- Reservations ---
    fun getAllReservations(): Flow<List<ReservationEntity>> = dao.getAllReservations()

    fun getActiveReservations(): Flow<List<ReservationEntity>> = dao.getActiveReservations()

    suspend fun createReservation(
        guestName: String,
        guestPhone: String,
        guestEmail: String,
        date: String,
        timeSlot: String,
        guestCount: Int,
        tableNumber: Int,
        zone: String,
        specialRequests: String,
        occasion: String
    ): ReservationEntity {
        val code = "SAV-" + UUID.randomUUID().toString().take(6).uppercase()
        val entity = ReservationEntity(
            reservationCode = code,
            guestName = guestName,
            guestPhone = guestPhone,
            guestEmail = guestEmail,
            date = date,
            timeSlot = timeSlot,
            guestCount = guestCount,
            tableNumber = tableNumber,
            zone = zone,
            specialRequests = specialRequests,
            occasion = occasion,
            status = "CONFIRMED"
        )
        val id = dao.insertReservation(entity)

        // Mark table as reserved in live floor plan
        _tablesState.value = _tablesState.value.map { table ->
            if (table.tableNumber == tableNumber) {
                table.copy(status = TableStatus.RESERVED)
            } else table
        }

        return entity.copy(id = id)
    }

    suspend fun cancelReservation(reservationId: Long, tableNumber: Int) {
        dao.updateReservationStatus(reservationId, "CANCELLED")
        // Free the table
        _tablesState.value = _tablesState.value.map { table ->
            if (table.tableNumber == tableNumber && table.status == TableStatus.RESERVED) {
                table.copy(status = TableStatus.AVAILABLE)
            } else table
        }
    }

    // --- Orders ---
    fun getAllOrders(): Flow<List<OrderEntity>> = dao.getAllOrders()

    suspend fun placeOrder(
        orderType: String,
        destination: String,
        guestName: String,
        subtotal: Double,
        taxAmount: Double,
        tipAmount: Double,
        grandTotal: Double,
        paymentType: String,
        itemsSummary: String
    ): OrderEntity {
        val orderCode = "ORD-" + UUID.randomUUID().toString().take(6).uppercase()
        val transactionRef = "TXN-" + System.currentTimeMillis().toString().takeLast(8)

        val order = OrderEntity(
            orderCode = orderCode,
            orderType = orderType,
            destination = destination,
            guestName = guestName,
            subtotal = subtotal,
            taxAmount = taxAmount,
            tipAmount = tipAmount,
            grandTotal = grandTotal,
            paymentType = paymentType,
            paymentStatus = "COMPLETED",
            transactionRef = transactionRef,
            orderStatus = "ORDER_PLACED",
            itemsSummary = itemsSummary
        )

        val id = dao.insertOrder(order)
        dao.clearCart()
        return order.copy(id = id)
    }

    suspend fun advanceOrderStatus(orderId: Long, nextStatus: String) {
        dao.updateOrderStatus(orderId, nextStatus)
    }
}
