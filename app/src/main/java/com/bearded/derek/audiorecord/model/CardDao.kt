package com.bearded.derek.audiorecord.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface CardDao {

    @Query("SELECT * from cardData")
    fun getAll(): List<Card>

    @Insert(onConflict = REPLACE)
    fun insert(card: Card)

    @Query("DELETE from cardData")
    fun deleteAll()
}