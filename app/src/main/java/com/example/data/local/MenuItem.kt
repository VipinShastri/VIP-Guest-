package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a restaurant menu item.
 */
@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val category: String, // e.g. STARTERS, MAINS, CHEF_SPECIALS, DESSERTS, BEVERAGES
    val price: Double,
    val calories: Int = 0,
    val prepMinutes: Int = 0,
    val tags: String = "", // Comma-separated tags, e.g. "Gluten-Free,Organic,Chef's Signature"
    val winePairing: String = "",
    val isChefSpecial: Boolean = false,
    val rating: Float = 4.9f
)
