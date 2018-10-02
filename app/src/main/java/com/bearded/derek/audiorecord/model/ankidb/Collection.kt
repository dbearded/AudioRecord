package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "col")
data class Collection(@PrimaryKey var id: Long?,
                      @ColumnInfo(name = "created") var crt: Long,
                      @ColumnInfo(name = "modified") var mod: Long,
                      @ColumnInfo(name = "schema_modified") var scm: Long,
                      @ColumnInfo(name = "version") var ver: Long,
                      @ColumnInfo(name = "dirty") var dty: Long,
                      @ColumnInfo(name = "update_sequence_number") var usn: Long,
                      @ColumnInfo(name = "last_sync") var ls: Long,
                      @ColumnInfo(name = "configuration") var conf: String,
                      @ColumnInfo(name = "models") var models: String,
                      @ColumnInfo(name = "decks") var decks: String,
                      @ColumnInfo(name = "deck_configuration") var dconf: String,
                      @ColumnInfo(name = "tags") var tags: String) {
    @Ignore
    constructor():this(null, 0, 0, 0, 0, 0, 0, 0,
            "", "", "", "", "")
}