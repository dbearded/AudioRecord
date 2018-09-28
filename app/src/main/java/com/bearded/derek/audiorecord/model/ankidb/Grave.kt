package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "graveData")
data class Grave(@ColumnInfo(name = "update_sequence_number") var usn: Long,
                 @ColumnInfo(name = "original_id") var oid: Long,
                 @ColumnInfo(name = "type") var type: Long) {
    constructor():this(0, 0, 0)
}