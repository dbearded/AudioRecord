package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "notes")
class Note(@PrimaryKey var id: Long?,
                @ColumnInfo(name = "globally_unique_id") var guid: String,
                @ColumnInfo(name = "model_id") var mid: Long,
                @ColumnInfo(name = "modification") var mod: Long,
                @ColumnInfo(name = "update_sequence_number", index = true) var usn: Long,
                @ColumnInfo(name = "tags") var tags: String,
                @ColumnInfo(name = "fields") var flds: String,
                @ColumnInfo(name = "sort_field") var sfld: String,
                @ColumnInfo(name = "field_checksum", index = true) var csum: Long,
                @ColumnInfo(name = "flags") var flags: Long,
                @ColumnInfo(name = "data") var data: String) {
    @Ignore
    constructor(): this(null, "", 0, 0, 0, "", "", "",
            0, 0, "")
}