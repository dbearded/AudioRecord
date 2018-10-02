package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "revlog")
data class ReviewLog(@PrimaryKey var id: Long?,
                     @ColumnInfo(name = "card_id", index = true) var cid: Long,
                     @ColumnInfo(name = "update_sequence", index = true) var usn: Long,
                     @ColumnInfo(name = "ease") var ease: Long,
                     @ColumnInfo(name = "Longerval") var ivl: Long,
                     @ColumnInfo(name = "last_Longerval") var lastIvl: Long,
                     @ColumnInfo(name = "factor") var factor: Long,
                     @ColumnInfo(name = "time") var time: Long,
                     @ColumnInfo(name = "type") var type: Long) {
    @Ignore
    constructor():this(null, 0, 0, 0, 0, 0, 0, 0,
            0)
}