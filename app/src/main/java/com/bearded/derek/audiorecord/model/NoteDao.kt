package com.bearded.derek.audiorecord.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.Query

@Dao
interface NoteDao {

    @Query("SELECT * from noteData")
    fun getAll(): List<Note>

    @Insert(onConflict = IGNORE)
    fun insert(note: Note): Long

    // @PrimaryKey var id: Long?,
//    @ColumnInfo(name = "globally_unique_id") var guid: String,
//    @ColumnInfo(name = "model_id") var mid: Long,
//    @ColumnInfo(name = "modification") var mod: Long,
//    @ColumnInfo(name = "update_sequence_number") var usn: Long,
//    @ColumnInfo(name = "tags") var tags: String,
//    @ColumnInfo(name = "fields") var flds: String,
//    @ColumnInfo(name = "sort_field") var sfld: String,
//    @ColumnInfo(name = "field_checksum") var csum: Long,
//    @ColumnInfo(name = "flags") var flags: Long,
//    @ColumnInfo(name = "data") var data: String)

    @Query("DELETE from noteData")
    fun deleteAll()
}