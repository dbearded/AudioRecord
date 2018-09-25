package com.bearded.derek.audiorecord.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface NoteDao {

    @Query("SELECT * from noteData")
    fun getAll(): List<Note>

    @Insert(onConflict = REPLACE)
    fun insert(note: Note)

    @Query("DELETE from noteData")
    fun deleteAll()
}