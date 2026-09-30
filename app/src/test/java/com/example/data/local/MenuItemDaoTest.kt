package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MenuItemDaoTest {

    private lateinit var database: SavoriaDatabase
    private lateinit var menuItemDao: MenuItemDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SavoriaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        menuItemDao = database.menuItemDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetMenuItemById() = runBlocking {
        val item = MenuItem(
            id = 1L,
            name = "Black Truffle Risotto",
            description = "Carnaroli rice with black summer truffle and aged parmesan",
            category = "MAINS",
            price = 45.0,
            calories = 520,
            prepMinutes = 20,
            tags = "Vegetarian,Gluten-Free",
            winePairing = "Barolo DOCG",
            isChefSpecial = true,
            rating = 4.95f
        )

        menuItemDao.insertMenuItem(item)

        val retrieved = menuItemDao.getMenuItemById(1L)
        assertNotNull(retrieved)
        assertEquals("Black Truffle Risotto", retrieved?.name)
        assertEquals("MAINS", retrieved?.category)
        assertEquals(45.0, retrieved?.price ?: 0.0, 0.01)
        assertTrue(retrieved?.isChefSpecial == true)
    }

    @Test
    fun queryAllAndFilterByCategory() = runBlocking {
        val items = listOf(
            MenuItem(id = 1L, name = "Caviar Tartlet", description = "Beluga caviar", category = "STARTERS", price = 35.0),
            MenuItem(id = 2L, name = "Wagyu Tenderloin", description = "A5 Miyazaki", category = "MAINS", price = 120.0, isChefSpecial = true),
            MenuItem(id = 3L, name = "Gold Souffle", description = "Tahitian vanilla", category = "DESSERTS", price = 22.0)
        )
        menuItemDao.insertMenuItems(items)

        val allItems = menuItemDao.getAllMenuItems().first()
        assertEquals(3, allItems.size)

        val starters = menuItemDao.getMenuItemsByCategory("STARTERS").first()
        assertEquals(1, starters.size)
        assertEquals("Caviar Tartlet", starters[0].name)

        val chefSpecials = menuItemDao.getChefSpecials().first()
        assertEquals(1, chefSpecials.size)
        assertEquals("Wagyu Tenderloin", chefSpecials[0].name)
    }

    @Test
    fun updateAndDeleteMenuItem() = runBlocking {
        val item = MenuItem(id = 10L, name = "Original Dish", description = "Test", category = "MAINS", price = 30.0)
        menuItemDao.insertMenuItem(item)

        val updated = item.copy(name = "Updated Dish", price = 35.0)
        menuItemDao.updateMenuItem(updated)

        val retrieved = menuItemDao.getMenuItemById(10L)
        assertEquals("Updated Dish", retrieved?.name)
        assertEquals(35.0, retrieved?.price ?: 0.0, 0.01)

        menuItemDao.deleteMenuItemById(10L)
        val deleted = menuItemDao.getMenuItemById(10L)
        assertNull(deleted)
    }
}
