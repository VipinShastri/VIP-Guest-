package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.AuthGateScreen
import com.example.ui.auth.signOutUser
import com.example.ui.components.LuxuryHeader
import com.example.ui.screens.AiConciergeScreen
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.TableReservationScreen
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.SavoriaTheme
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.SavoriaViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavoriaTheme(darkTheme = true) {
                SavoriaRoot()
            }
        }
    }
}

@Composable
fun SavoriaRoot() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthGateScreen(
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        val viewModel: SavoriaViewModel = viewModel()
        SavoriaApp(
            viewModel = viewModel,
            currentUser = currentUser!!,
            onSignOut = {
                signOutUser(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = { currentUser = null },
                    scope = coroutineScope
                )
            }
        )
    }
}

@Composable
fun SavoriaApp(
    viewModel: SavoriaViewModel = viewModel(),
    currentUser: FirebaseUser? = null,
    onSignOut: (() -> Unit)? = null
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val diningMode by viewModel.diningMode.collectAsStateWithLifecycle()
    val orderLocation by viewModel.orderLocation.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val tipPercentage by viewModel.tipPercentage.collectAsStateWithLifecycle()
    val splitBillCount by viewModel.splitBillCount.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()

    val tables by viewModel.tables.collectAsStateWithLifecycle()
    val reservations by viewModel.reservations.collectAsStateWithLifecycle()
    val selectedZone by viewModel.selectedZone.collectAsStateWithLifecycle()
    val selectedTable by viewModel.selectedTable.collectAsStateWithLifecycle()
    val lastCreatedReservation by viewModel.lastCreatedReservation.collectAsStateWithLifecycle()

    val orders by viewModel.orders.collectAsStateWithLifecycle()

    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val aiThoughtStream by viewModel.aiThoughtStream.collectAsStateWithLifecycle()

    val itemForCustomization by viewModel.itemForCustomization.collectAsStateWithLifecycle()

    // Handle system back navigation: return to Menu if on other tabs
    BackHandler(enabled = currentTab != AppNavTab.MENU) {
        viewModel.selectTab(AppNavTab.MENU)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = ObsidianBlack,
        topBar = {
            LuxuryHeader(
                selectedDiningMode = diningMode,
                onDiningModeChanged = { viewModel.setDiningMode(it) },
                guestName = currentUser?.displayName,
                onSignOut = onSignOut
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .border(androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)),
                containerColor = ObsidianSurface,
                contentColor = GoldChampagne
            ) {
                NavigationBarItem(
                    selected = currentTab == AppNavTab.MENU,
                    onClick = { viewModel.selectTab(AppNavTab.MENU) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = "Menu Chart"
                        )
                    },
                    label = {
                        Text(
                            text = "Menu",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = if (currentTab == AppNavTab.MENU) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldChampagne,
                        indicatorColor = GoldChampagne,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_menu_tab")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavTab.RESERVATIONS,
                    onClick = { viewModel.selectTab(AppNavTab.RESERVATIONS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.EventSeat,
                            contentDescription = "Reservations"
                        )
                    },
                    label = {
                        Text(
                            text = "Seating",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = if (currentTab == AppNavTab.RESERVATIONS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldChampagne,
                        indicatorColor = GoldChampagne,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_reservations_tab")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavTab.CART,
                    onClick = { viewModel.selectTab(AppNavTab.CART) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(
                                        containerColor = GoldChampagne,
                                        contentColor = ObsidianBlack
                                    ) {
                                        Text(
                                            text = "$cartItemCount",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Order & Pay"
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "Order",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = if (currentTab == AppNavTab.CART) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldChampagne,
                        indicatorColor = GoldChampagne,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_cart_tab")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavTab.TRACKER,
                    onClick = { viewModel.selectTab(AppNavTab.TRACKER) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = "Live Status"
                        )
                    },
                    label = {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = if (currentTab == AppNavTab.TRACKER) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldChampagne,
                        indicatorColor = GoldChampagne,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tracker_tab")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavTab.CONCIERGE,
                    onClick = { viewModel.selectTab(AppNavTab.CONCIERGE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Sommelier"
                        )
                    },
                    label = {
                        Text(
                            text = "AI Sommelier",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = if (currentTab == AppNavTab.CONCIERGE) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldChampagne,
                        indicatorColor = GoldChampagne,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_concierge_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.MENU -> {
                    MenuScreen(
                        menuItems = menuItems,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        onCategorySelected = { viewModel.setCategory(it) },
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onAddToCartRequested = { viewModel.openCustomizationDialog(it) },
                        itemForCustomization = itemForCustomization,
                        onConfirmCustomization = { item, qty, pref, addons, notes ->
                            viewModel.addToCart(item, qty, notes, pref, addons)
                        },
                        onDismissCustomization = { viewModel.closeCustomizationDialog() }
                    )
                }

                AppNavTab.RESERVATIONS -> {
                    TableReservationScreen(
                        tables = tables,
                        reservations = reservations,
                        selectedZone = selectedZone,
                        selectedTable = selectedTable,
                        lastCreatedReservation = lastCreatedReservation,
                        onZoneSelected = { viewModel.setZone(it) },
                        onTableSelected = { viewModel.selectTable(it) },
                        onBookReservation = { name, phone, email, date, time, guests, tableNum, zone, requests, occasion ->
                            viewModel.bookReservation(name, phone, email, date, time, guests, tableNum, zone, requests, occasion)
                        },
                        onDismissConfirmation = { viewModel.dismissReservationConfirmation() },
                        onCancelReservation = { viewModel.cancelReservation(it) }
                    )
                }

                AppNavTab.CART -> {
                    CartCheckoutScreen(
                        cartItems = cartItems,
                        diningMode = diningMode,
                        orderLocation = orderLocation,
                        tipPercentage = tipPercentage,
                        splitBillCount = splitBillCount,
                        paymentState = paymentState,
                        onOrderLocationChanged = { viewModel.setOrderLocation(it) },
                        onTipPercentageChanged = { viewModel.setTipPercentage(it) },
                        onSplitBillCountChanged = { viewModel.setSplitBillCount(it) },
                        onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                        onRemoveItem = { id -> viewModel.removeCartItem(id) },
                        onPaymentTypeSelected = { viewModel.updatePaymentType(it) },
                        onCardInfoUpdated = { num, exp, cvv, holder -> viewModel.updateCardInfo(num, exp, cvv, holder) },
                        onRoomFolioUpdated = { room, last -> viewModel.updateRoomFolio(room, last) },
                        onProcessPayment = { name, dest, mode ->
                            viewModel.processPaymentAndPlaceOrder(name, dest, mode)
                        },
                        onBrowseMenu = { viewModel.selectTab(AppNavTab.MENU) }
                    )
                }

                AppNavTab.TRACKER -> {
                    OrderTrackingScreen(
                        orders = orders,
                        onAdvanceOrderStatus = { orderId, step ->
                            viewModel.advanceOrderCookingState(orderId, step)
                        }
                    )
                }

                AppNavTab.CONCIERGE -> {
                    AiConciergeScreen(
                        messages = aiMessages,
                        isThinking = isAiThinking,
                        thoughtStream = aiThoughtStream,
                        onSendMessage = { viewModel.sendAiConsultation(it) }
                    )
                }
            }
        }
    }
}
