package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.AthkarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ThikrEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun thikrDao(): ThikrDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noor_al_salah_athkar_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(getDatabase(context).thikrDao())
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = getDatabase(context).thikrDao()
                    if (dao.getCount() == 0) {
                        populateInitialData(dao)
                    }
                }
            }

            private suspend fun populateInitialData(dao: ThikrDao) {
                val allEntities = mutableListOf<ThikrEntity>()

                AthkarRepository.morningAthkar.forEachIndexed { index, it ->
                    allEntities.add(
                        ThikrEntity(
                            id = it.id,
                            category = it.category.name,
                            textArabic = it.textArabic,
                            virtueArabic = it.virtueArabic,
                            referenceArabic = it.referenceArabic,
                            targetCount = it.targetCount,
                            currentCount = 0,
                            isCompleted = false,
                            isFavorite = index == 1 // Pre-favorite Sayyid al-Istighfar as a lovely default!
                        )
                    )
                }

                AthkarRepository.eveningAthkar.forEachIndexed { index, it ->
                    allEntities.add(
                        ThikrEntity(
                            id = it.id,
                            category = it.category.name,
                            textArabic = it.textArabic,
                            virtueArabic = it.virtueArabic,
                            referenceArabic = it.referenceArabic,
                            targetCount = it.targetCount,
                            currentCount = 0,
                            isCompleted = false,
                            isFavorite = index == 1
                        )
                    )
                }

                AthkarRepository.afterPrayerAthkar.forEach {
                    allEntities.add(
                        ThikrEntity(
                            id = it.id,
                            category = it.category.name,
                            textArabic = it.textArabic,
                            virtueArabic = it.virtueArabic,
                            referenceArabic = it.referenceArabic,
                            targetCount = it.targetCount,
                            currentCount = 0,
                            isCompleted = false,
                            isFavorite = false
                        )
                    )
                }

                dao.insertAll(allEntities)
            }
        }
    }
}
