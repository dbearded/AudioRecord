package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.Query

@Dao
interface ReviewLogDao {

    @Query("SELECT * from reviewData")
    fun getAll(): List<Note>

    @Insert(onConflict = IGNORE)
    fun insert(reviewLog: ReviewLog): Long

    @Query("DELETE from reviewData")
    fun deleteAll()
}