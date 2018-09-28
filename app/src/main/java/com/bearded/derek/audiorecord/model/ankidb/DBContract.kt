package com.bearded.derek.audiorecord.model.ankidb

sealed class AnkiType {

    object NoteAnki: AnkiType()
    {
        const val _ID: String = "id"
        const val GUID: String = "guid"
        const val MID: String = "mid"
        const val MOD: String = "mod"
        const val USN: String = "usn"
        const val TAGS: String = "tags"
        const val FLDS: String = "flds"
        const val SFLD: String = "sfld"
        const val CSUM: String = "csum"
        const val FLAGS: String = "flags"
        const val DATA: String = "data"
    }

    object CardAnki: AnkiType() {
        const val _ID: String = "id"
        const val NID: String = "nid"
        const val DID: String = "did"
        const val ORD: String = "ord"
        const val MOD: String = "mod"
        const val USN: String = "usn"
        const val TYPE: String = "type"
        const val QUEUE: String = "queue"
        const val DUE: String = "due"
        const val IVL: String = "ivl"
        const val FACTOR: String = "factor"
        const val REPS: String = "reps"
        const val LAPSES: String = "lapses"
        const val LEFT: String = "left"
        const val ODUE: String = "odue"
        const val ODID: String = "odid"
        const val FLAGS: String = "flags"
        const val DATA: String = "data"
    }
}
