package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiResponse
import com.example.data.api.GeminiService
import com.example.data.firebase.SavoriaFirestoreRepository
import com.example.data.local.CartItemEntity
import com.example.data.local.MenuItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ReservationEntity
import com.example.data.local.SavoriaDatabase
import com.example.data.model.DiningMode
import com.example.data.model.DiningTable
import com.example.data.model.MenuCategory
import com.example.data.model.PaymentType
import com.example.data.model.TableStatus
import com.example.data.model.TableZone
import com.example.data.repository.SavoriaRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    MENU("Menu Chart"),
    RESERVATIONS("Reservations"),
    CART("Order & Pay"),
    TRACKER("Live Status"),
    CONCIERGE("AI Sommelier")
}

data class PaymentFormState(
    val paymentType: PaymentType = PaymentType.CREDIT_CARD,
    val cardNumber: String = "4532 •••• •••• 8892",
    val cardExpiry: String = "12/28",
    val cardCvv: String = "•••",
    val cardHolder: String = "Alexander Vance",
    val roomNumber: String = "402",
    val hotelGuestLastName: String = "Vance",
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false,
    val lastTransactionId: String = ""
)

data class AiMessage(
    val sender: String, // "guest" or "sommelier"
    val content: String,
    val thoughtProcess: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class SavoriaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SavoriaDatabase.getInstance(application)
    private val repository = SavoriaRepository(database.savoriaDao())
    private val firestoreRepository = SavoriaFirestoreRepository(application)
    private val geminiService = GeminiService()

    // Navigation
    private val _currentTab = MutableStateFlow(AppNavTab.MENU)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    // Dining Mode
    private val _diningMode = MutableStateFlow(DiningMode.TABLE)
    val diningMode: StateFlow<DiningMode> = _diningMode.asStateFlow()

    fun setDiningMode(mode: DiningMode) {
        _diningMode.value = mode
    }

    // Order Target (e.g. "Table 4" or "Suite 402")
    private val _orderLocation = MutableStateFlow("Table #4")
    val orderLocation: StateFlow<String> = _orderLocation.asStateFlow()

    fun setOrderLocation(location: String) {
        _orderLocation.value = location
    }

    // Menu state
    private val _selectedCategory = MutableStateFlow(MenuCategory.ALL)
    val selectedCategory: StateFlow<MenuCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val menuItems: StateFlow<List<MenuItemEntity>> = combine(
        repository.getAllMenuItems(),
        _selectedCategory,
        _searchQuery
    ) { items, category, query ->
        items.filter { item ->
            val matchesCategory = category == MenuCategory.ALL || item.category == category.name
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.tags.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State
    val cartItems: StateFlow<List<CartItemEntity>> = repository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemCount: StateFlow<Int> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Tip percentage: 15, 18, 20, 0
    private val _tipPercentage = MutableStateFlow(18)
    val tipPercentage: StateFlow<Int> = _tipPercentage.asStateFlow()

    fun setTipPercentage(percent: Int) {
        _tipPercentage.value = percent
    }

    // Split bill
    private val _splitBillCount = MutableStateFlow(1)
    val splitBillCount: StateFlow<Int> = _splitBillCount.asStateFlow()

    fun setSplitBillCount(count: Int) {
        if (count in 1..8) {
            _splitBillCount.value = count
        }
    }

    // Payment Form
    private val _paymentState = MutableStateFlow(PaymentFormState())
    val paymentState: StateFlow<PaymentFormState> = _paymentState.asStateFlow()

    fun updatePaymentType(type: PaymentType) {
        _paymentState.value = _paymentState.value.copy(paymentType = type)
    }

    fun updateCardInfo(number: String, expiry: String, cvv: String, holder: String) {
        _paymentState.value = _paymentState.value.copy(
            cardNumber = number,
            cardExpiry = expiry,
            cardCvv = cvv,
            cardHolder = holder
        )
    }

    fun updateRoomFolio(room: String, lastName: String) {
        _paymentState.value = _paymentState.value.copy(
            roomNumber = room,
            hotelGuestLastName = lastName
        )
    }

    // Reservations
    val tables: StateFlow<List<DiningTable>> = repository.tablesState
    val reservations: StateFlow<List<ReservationEntity>> = repository.getAllReservations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedZone = MutableStateFlow(TableZone.GRAND_HALL)
    val selectedZone: StateFlow<TableZone> = _selectedZone.asStateFlow()

    fun setZone(zone: TableZone) {
        _selectedZone.value = zone
    }

    private val _selectedTable = MutableStateFlow<DiningTable?>(null)
    val selectedTable: StateFlow<DiningTable?> = _selectedTable.asStateFlow()

    fun selectTable(table: DiningTable?) {
        _selectedTable.value = table
    }

    // Reservation creation status
    private val _lastCreatedReservation = MutableStateFlow<ReservationEntity?>(null)
    val lastCreatedReservation: StateFlow<ReservationEntity?> = _lastCreatedReservation.asStateFlow()

    // Orders
    val orders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    // AI Concierge
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                sender = "sommelier",
                content = "Good evening. I am your Executive Culinary Concierge & Sommelier, operating with Deep Gastronomic Reasoning. How may I orchestrate your dining experience tonight? Inquire about wine pairings, multi-course sequencing, or bespoke dietary accommodations."
            )
        )
    )
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _aiThoughtStream = MutableStateFlow("")
    val aiThoughtStream: StateFlow<String> = _aiThoughtStream.asStateFlow()

    // Customization Modal State for Menu Item
    private val _itemForCustomization = MutableStateFlow<MenuItemEntity?>(null)
    val itemForCustomization: StateFlow<MenuItemEntity?> = _itemForCustomization.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    fun setCategory(category: MenuCategory) {
        _selectedCategory.value = category
    }

    fun addMenuItem(
        name: String,
        price: Double,
        description: String,
        category: String,
        calories: Int = 350,
        prepMinutes: Int = 15,
        tags: String = "Artisanal",
        winePairing: String = "",
        isChefSpecial: Boolean = false,
        onSuccess: (MenuItemEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val entity = MenuItemEntity(
                name = name.trim(),
                description = description.trim(),
                category = category,
                price = price,
                calories = calories,
                prepMinutes = prepMinutes,
                tags = tags.trim(),
                winePairing = winePairing.trim(),
                isChefSpecial = isChefSpecial,
                rating = 4.9f
            )
            val generatedId = repository.insertMenuItem(entity)
            onSuccess(entity.copy(id = generatedId))
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openCustomizationDialog(item: MenuItemEntity) {
        _itemForCustomization.value = item
    }

    fun closeCustomizationDialog() {
        _itemForCustomization.value = null
    }

    fun addToCart(
        item: MenuItemEntity,
        quantity: Int = 1,
        specialInstructions: String = "",
        cookingPreference: String = "",
        addedAddons: String = ""
    ) {
        viewModelScope.launch {
            repository.addToCart(item, quantity, specialInstructions, cookingPreference, addedAddons)
            _itemForCustomization.value = null
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, newQuantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Reservation submission
    fun bookReservation(
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
    ) {
        viewModelScope.launch {
            val res = repository.createReservation(
                guestName = guestName,
                guestPhone = guestPhone,
                guestEmail = guestEmail,
                date = date,
                timeSlot = timeSlot,
                guestCount = guestCount,
                tableNumber = tableNumber,
                zone = zone,
                specialRequests = specialRequests,
                occasion = occasion
            )
            _lastCreatedReservation.value = res
            _selectedTable.value = null

            // Sync with Firestore if authenticated
            try {
                if (Firebase.auth.currentUser != null) {
                    firestoreRepository.saveReservation(
                        reservationCode = res.reservationCode,
                        guestName = guestName,
                        guestPhone = guestPhone,
                        guestEmail = guestEmail,
                        date = date,
                        timeSlot = timeSlot,
                        guestCount = guestCount,
                        tableNumber = tableNumber,
                        zone = zone,
                        specialRequests = specialRequests,
                        occasion = occasion
                    )
                }
            } catch (e: Exception) {
                android.util.Log.w("SavoriaVM", "Firestore reservation sync deferred: ${e.message}")
            }
        }
    }

    fun dismissReservationConfirmation() {
        _lastCreatedReservation.value = null
    }

    fun cancelReservation(reservation: ReservationEntity) {
        viewModelScope.launch {
            repository.cancelReservation(reservation.id, reservation.tableNumber)
        }
    }

    // Secure Payment Processing & Order Placement
    fun processPaymentAndPlaceOrder(
        guestName: String,
        destination: String,
        orderType: String
    ) {
        viewModelScope.launch {
            _paymentState.value = _paymentState.value.copy(isProcessing = true)

            // Simulate realistic bank / 3D Secure / Tokenization roundtrip
            delay(1800)

            val currentCart = cartItems.value
            if (currentCart.isEmpty()) {
                _paymentState.value = _paymentState.value.copy(isProcessing = false)
                return@launch
            }

            val subtotal = currentCart.sumOf { it.price * it.quantity }
            val tax = subtotal * 0.08
            val tip = subtotal * (_tipPercentage.value / 100.0)
            val grandTotal = subtotal + tax + tip

            val summary = currentCart.joinToString(", ") { "${it.quantity}x ${it.name}" }

            val order = repository.placeOrder(
                orderType = orderType,
                destination = destination,
                guestName = guestName.ifBlank { _paymentState.value.cardHolder },
                subtotal = subtotal,
                taxAmount = tax,
                tipAmount = tip,
                grandTotal = grandTotal,
                paymentType = _paymentState.value.paymentType.title,
                itemsSummary = summary
            )

            _lastPlacedOrder.value = order

            // Sync with Firestore if authenticated
            try {
                if (Firebase.auth.currentUser != null) {
                    firestoreRepository.saveOrder(
                        orderCode = order.orderCode,
                        orderType = orderType,
                        destination = destination,
                        guestName = order.guestName,
                        subtotal = subtotal,
                        taxAmount = tax,
                        tipAmount = tip,
                        grandTotal = grandTotal,
                        paymentType = order.paymentType,
                        paymentStatus = order.paymentStatus,
                        transactionRef = order.transactionRef,
                        orderStatus = order.orderStatus,
                        itemsSummary = summary
                    )
                }
            } catch (e: Exception) {
                android.util.Log.w("SavoriaVM", "Firestore order sync deferred: ${e.message}")
            }

            _paymentState.value = _paymentState.value.copy(
                isProcessing = false,
                isSuccess = true,
                lastTransactionId = order.transactionRef
            )

            // Switch to tracker tab so user can see live order progression!
            delay(500)
            _currentTab.value = AppNavTab.TRACKER
        }
    }

    fun dismissOrderSuccess() {
        _lastPlacedOrder.value = null
        _paymentState.value = _paymentState.value.copy(isSuccess = false)
    }

    // Simulate Kitchen workflow progression for orders
    fun advanceOrderCookingState(orderId: Long, currentStepIndex: Int) {
        viewModelScope.launch {
            val nextStatus = when (currentStepIndex) {
                0 -> "PREPARING"
                1 -> "PLATING"
                2 -> "OUT_FOR_DELIVERY"
                3 -> "SERVED"
                else -> "SERVED"
            }
            repository.advanceOrderStatus(orderId, nextStatus)
        }
    }

    // AI Concierge Consultation with High Thinking
    fun sendAiConsultation(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = AiMessage(sender = "guest", content = prompt)
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiThinking.value = true
        _aiThoughtStream.value = "Initiating high-depth gastronomic reasoning with gemini-3.1-pro-preview...\nSynthesizing culinary profiles, pairing matrices & guest dietary context..."

        viewModelScope.launch {
            val currentCart = cartItems.value.joinToString { "${it.quantity}x ${it.name}" }
            val menuSummary = menuItems.value.take(15).joinToString { "${it.name} (${it.category}, $${it.price})" }

            val response: GeminiResponse = geminiService.consultHighThinkingSommelier(
                userPrompt = prompt,
                cartContext = currentCart,
                menuContext = menuSummary
            )

            _aiThoughtStream.value = response.thoughtProcess
            delay(400)

            _aiMessages.value = _aiMessages.value + AiMessage(
                sender = "sommelier",
                content = response.text,
                thoughtProcess = response.thoughtProcess
            )
            _isAiThinking.value = false
        }
    }
}
