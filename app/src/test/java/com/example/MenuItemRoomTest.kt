package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MenuItemEntity
import com.example.data.local.SavoriaDao
import com.example.data.local.SavoriaDatabase
import com.example.data.model.MenuCategory
import com.example.data.repository.SavoriaRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MenuItemRoomTest {

    private lateinit var database: SavoriaDatabase
    private lateinit var dao: SavoriaDao
    private lateinit var repository: SavoriaRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SavoriaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.savoriaDao()
        repository = SavoriaRepository(dao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertMenuItemWithRequiredFields() = runBlocking {
        val newItem = MenuItemEntity(
            name = "Pan-Seared Hokkaido Scallops",
            price = 38.50,
            description = "Caramelized wild scallops with saffron foam",
            category = MenuCategory.STARTERS.name,
            calories = 310,
            prepMinutes = 14,
            tags = "Seafood,Gluten-Free",
            winePairing = "Chablis Grand Cru",
            isChefSpecial = true
        )

        val id = repository.insertMenuItem(newItem)
        val retrieved = dao.getMenuItemById(id)

        assertNotNull(retrieved)
        assertEquals("Pan-Seared Hokkaido Scallops", retrieved?.name)
        assertEquals(38.50, retrieved?.price ?: 0.0, 0.001)
        assertEquals("Caramelized wild scallops with saffron foam", retrieved?.description)
        assertEquals(MenuCategory.STARTERS.name, retrieved?.category)
    }
}
