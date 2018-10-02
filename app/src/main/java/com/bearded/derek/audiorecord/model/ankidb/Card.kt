package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.*

@Entity(tableName = "cards", indices = [Index(value = ["deck_id", "queue", "due"], name = "ix_cards_sched")])
data class Card(@PrimaryKey var id: Long?,
                @ColumnInfo(name = "note_id", index = true) var nid: Long,
                @ColumnInfo(name = "deck_id") var did: Long,
                @ColumnInfo(name = "ordinal") var ord: Long,
                @ColumnInfo(name = "modification_time") var mod: Long,
                @ColumnInfo(name = "update_sequence", index = true) var usn: Long,
                @ColumnInfo(name = "type") var type: Long,
                @ColumnInfo(name = "queue") var queue: Long,
                @ColumnInfo(name = "due") var due: Long,
                @ColumnInfo(name = "Interval") var ivl: Long,
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

