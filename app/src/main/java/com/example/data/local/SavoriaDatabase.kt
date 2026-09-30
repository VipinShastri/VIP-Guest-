package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MenuItem::class,
        CartItemEntity::class,
        ReservationEntity::class,
        OrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SavoriaDatabase : RoomDatabase() {
    abstract fun savoriaDao(): SavoriaDao
    abstract fun menuItemDao(): MenuItemDao

    companion object {
        @Volatile
        private var INSTANCE: SavoriaDatabase? = null

        fun getInstance(context: Context): SavoriaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavoriaDatabase::class.java,
                    "savoria_dining.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
