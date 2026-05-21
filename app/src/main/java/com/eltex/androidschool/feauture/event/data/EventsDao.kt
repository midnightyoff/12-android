package com.eltex.androidschool.feauture.event.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getAll(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(event: EventEntity): Long

    @Query("DELETE FROM events WHERE id = :id")
    fun deleteById(id: Long)

    @Query(
        """
            UPDATE events SET 
                likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END,
                likes = CASE WHEN likedByMe THEN likes - 1 ELSE likes + 1 END
            WHERE id = :id
        """
    )
    fun likeById(id: Long)

    @Query(
        """
            UPDATE events SET 
                participatedByMe = CASE WHEN participatedByMe THEN 0 ELSE 1 END,
                participants = CASE WHEN participatedByMe THEN participants - 1 ELSE participants + 1 END
            WHERE id = :id
        """
    )
    fun participateById(id: Long)

    @Query("SELECT * FROM events WHERE id = :id")
    fun getById(id: Long): EventEntity?
}
