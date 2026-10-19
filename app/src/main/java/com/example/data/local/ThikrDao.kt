package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ThikrDao {

    @Query("SELECT * FROM athkar ORDER BY id ASC")
    fun getAllAthkar(): Flow<List<ThikrEntity>>

    @Query("SELECT * FROM athkar WHERE category = :category ORDER BY id ASC")
    fun getAthkarByCategory(category: String): Flow<List<ThikrEntity>>

    @Query("SELECT * FROM athkar WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoriteAthkar(): Flow<List<ThikrEntity>>

    @Query("SELECT * FROM athkar WHERE audioFilePath IS NOT NULL OR isCustomRecording = 1 ORDER BY id DESC")
    fun getCustomVoiceAthkar(): Flow<List<ThikrEntity>>

    @Query("SELECT COUNT(*) FROM athkar WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM athkar")
    suspend fun getCount(): Int

    @Query("SELECT MAX(id) FROM athkar")
    suspend fun getMaxId(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(athkar: List<ThikrEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(thikr: ThikrEntity)

    @Update
    suspend fun update(thikr: ThikrEntity)

    @Query("DELETE FROM athkar WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE athkar SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("UPDATE athkar SET currentCount = :currentCount, isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCount(id: Int, currentCount: Int, isCompleted: Boolean)

    @Query("UPDATE athkar SET currentCount = 0, isCompleted = 0 WHERE category = :category")
    suspend fun resetCategoryCounts(category: String)

    @Query("UPDATE athkar SET currentCount = 0, isCompleted = 0")
    suspend fun resetAllCounts()

    @Query("SELECT * FROM athkar WHERE id = :id")
    suspend fun getThikrById(id: Int): ThikrEntity?
}
