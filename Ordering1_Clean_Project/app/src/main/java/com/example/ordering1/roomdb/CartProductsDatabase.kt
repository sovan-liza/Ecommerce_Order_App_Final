package com.example.ordering1.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CartProducts::class],
    version = 2,
    exportSchema = false
)
abstract class CartProductsDatabase : RoomDatabase() {

    abstract fun cartProductsDao(): CartProductDao

    companion object {

        @Volatile
        private var INSTANCE: CartProductsDatabase? = null

        fun getDatabaseInstance(context: Context): CartProductsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartProductsDatabase::class.java,
                    "cart_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}