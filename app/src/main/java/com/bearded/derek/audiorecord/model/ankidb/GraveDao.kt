package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.Query

@Dao
interface GraveDao {

    @Query("SELECT * from graves")
    fun getAll(): List<Grave>

    @Insert(onConflict = IGNORE)
    fun insert(grave: Grave): Long

    @Query("DELETE from graves")
    fun deleteAll()
}