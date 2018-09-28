package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

@Dao
interface BaseDao<T> {
    @Insert
    fun insert(t: T): Long
}