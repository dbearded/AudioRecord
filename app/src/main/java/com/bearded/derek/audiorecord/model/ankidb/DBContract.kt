package com.bearded.derek.audiorecord.model.ankidb

sealed class AnkiType {

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

    object ColAnki: AnkiType() {
        const val _ID: String = "id"
        const val CRT: String = "crt"
        const val MOD: String = "mod"
        const val SCM: String = "scm"
        const val VER: String = "ver"
        const val DTY: String = "dty"
        const val USN: String = "usn"
        const val LS: String = "ls"
        const val CONF: String = "conf"
        const val MODELS: String = "models"
        const val DECKS: String = "decks"
        const val DCONF: String = "dconf"
        const val TAGS: String = "tags"
    }

    object GraveAnki: AnkiType() {
        const val USN: String = "usn"
        const val OID: String = "oid"
        const val TYPE: String = "type"
    }

    object NoteAnki: AnkiType() {
        const val _ID: String = "_id"
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

    object RevlogAnki: AnkiType() {
        const val _ID: String = "id"
        const val CID: String = "cid"
        const val USN: String = "usn"
        const val EASE: String = "ease"
        const val IVL: String = "ivl"
        const val LASTIVL: String = "lastIvl"
        const val FACTOR: String = "factor"
        const val TIME: String = "time"
        const val TYPE: String = "type"
    }
}
