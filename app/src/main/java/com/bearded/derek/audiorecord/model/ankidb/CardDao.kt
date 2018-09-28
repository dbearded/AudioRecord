package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface CardDao: BaseDao<Card> {

    @Query("SELECT * from cardData")
    fun getAll(): List<Card>

    @Insert(onConflict = REPLACE)
    override fun insert(card: Card): Long

    @Query("DELETE from cardData")
    fun deleteAll()
}