package com.example.data.model

typealias MenuItem = com.example.data.local.MenuItem

enum class MenuCategory(val displayName: String, val iconName: String) {
    ALL("All Curations", "restaurant"),
    STARTERS("Hors d'oeuvres", "tapas"),
    MAINS("Main Entrées", "dinner_dining"),
    CHEF_SPECIALS("Chef's Signature", "stars"),
    DESSERTS("Artisan Desserts", "cake"),
    BEVERAGES("Cellar & Spirits", "wine_bar")
}

enum class DiningMode(val title: String, val subtitle: String) {
    TABLE("Table Dining", "Served directly to your restaurant table"),
    ROOM_SERVICE("Hotel Suite Service", "Delivered hot to your hotel suite"),
    TAKEOUT("Curbside & Takeaway", "Packaged in luxury thermal boxes")
}

enum class TableZone(val title: String, val ambiance: String, val baseMinSpend: Double) {
    GRAND_HALL("Grand Dining Hall", "Crystal chandeliers & live jazz quartet", 75.0),
    GARDEN_TERRACE("Veranda & Garden", "Fountain views, heated ambient terrace", 60.0),
    SKYLINE_VIP("Skyline VIP Lounge", "Panoramic top-floor city & harbor views", 120.0),
    PRIVATE_CELLAR("Private Cellar Room", "Sommelier-guided bespoke estate vault", 250.0)
}

enum class TableStatus {
    AVAILABLE,
    RESERVED,
    OCCUPIED
}

data class DiningTable(
    val id: Int,
    val tableNumber: Int,
    val capacity: Int,
    val zone: TableZone,
    val status: TableStatus,
    val isWindowSeat: Boolean = false,
    val description: String = ""
)

enum class PaymentType(val title: String) {
    CREDIT_CARD("Credit / Debit Card"),
    HOTEL_ROOM_FOLIO("Charge to Hotel Room Folio"),
    DIGITAL_WALLET("Google Pay / One-Touch")
}

enum class OrderStatus(val title: String, val stepIndex: Int, val description: String) {
    ORDER_PLACED("Order Received", 0, "Chef is reviewing your order details"),
    PREPARING("Kitchen Prep & Mise en Place", 1, "Culinary brigade is preparing fresh ingredients"),
    PLATING("Artisanal Plating & Seal", 2, "Executive Chef inspecting presentation"),
    OUT_FOR_DELIVERY("On Its Way", 3, "Butler or server dispatching to destination"),
    SERVED("Delivered & Served", 4, "Bon appétit! Enjoy your dining experience")
}
