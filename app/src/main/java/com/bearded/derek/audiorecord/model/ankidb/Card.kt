package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "cardData")
data class Card(@PrimaryKey var id: Long?,
                @ColumnInfo(name = "note_id") var nid: Long,
                @ColumnInfo(name = "deck_id") var did: Long,
                @ColumnInfo(name = "ordinal") var ord: Long,
                @ColumnInfo(name = "modification_time") var mod: Long,
                @ColumnInfo(name = "update_sequence") var usn: Long,
                @ColumnInfo(name = "type") var type: Long,
                @ColumnInfo(name = "queue") var queue: Long,
                @ColumnInfo(name = "due") var due: Long,
                @ColumnInfo(name = "Longerval") var ivl: Long,
                @ColumnInfo(name = "factor") var factor: Long,
                @ColumnInfo(name = "repetitions") var reps: Long,
                @ColumnInfo(name = "lapses") var lapses: Long,
                @ColumnInfo(name = "left") var left: Long,
                @ColumnInfo(name = "original_due") var odue: Long,
                @ColumnInfo(name = "original_deck_id") var odid: Long,
                @ColumnInfo(name = "flags") var flags: Long,
                @ColumnInfo(name = "data") var data: String) {
    @Ignore
    constructor(): this(null, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, "")
}

