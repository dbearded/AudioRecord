package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.Query

@Dao
interface CollectionDao {

    @Query("SELECT * from col")
    fun getAll(): List<Collection>

    @Insert(onConflict = IGNORE)
    fun insert(collection: Collection): Long

    @Query("DELETE from col")
    fun deleteAll()
}