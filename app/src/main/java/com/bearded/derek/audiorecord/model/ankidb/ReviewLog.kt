package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "reviewData")
data class ReviewLog(@PrimaryKey var id: Long?,
                     @ColumnInfo(name = "card_id") var cid: Long,
                     @ColumnInfo(name = "update_sequence") var usn: Long,
                     @ColumnInfo(name = "ease") var ease: Long,
                     @ColumnInfo(name = "Longerval") var ivl: Long,
                     @ColumnInfo(name = "last_Longerval") var lastIvl: Long,
                     @ColumnInfo(name = "factor") var factor: Long,
                     @ColumnInfo(name = "time") var time: Long,
                     @ColumnInfo(name = "type") var type: Long) {
    constructor():this(null, 0, 0, 0, 0, 0, 0, 0,
            0)
}