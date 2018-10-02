package com.bearded.derek.audiorecord.model.ankidb

import android.database.Cursor
import com.bearded.derek.audiorecord.model.ankidb.AnkiType.*

interface Converter<in From, out To> {
    fun convert(from: From): To
}


fun <F: Cursor, T: AnkiType> converterFactory(f: F, t: T): Converter<Cursor, *> = fromCursor(f, t)
//fun <T: AnkiType> converterFactory(f: String, t: T): Converter<String, *> = fromString(f, t)

fun <F, T: AnkiType> converterFactoryTwo(f: F, t: T): Converter<*, *> = when(f) {
//    is String -> fromString(f, t)
    is Cursor -> fromCursor(f, t)
    else -> throw IllegalArgumentException("Unkown input")
}

fun <T> T.convertFromCursorTwo(cursor: Cursor) where T : Note = inflateNote(cursor)
fun <T> T.convertFromCursorTwo(cursor: Cursor) where T : Card = inflateCard(cursor)

fun testingAgaing(cursor: Cursor, note: Note) = note.convertFromCursor(cursor)

class NoteConverter: Converter<Cursor, Note> {
    override fun convert(from: Cursor) = inflateNote(from)
}

fun testingOutput(cursor: Cursor) = converterFactory(cursor, CardAnki)

class CardConverter: Converter<Cursor, Card> {
    override fun convert(from: Cursor) = inflateCard(from)
}

fun Note.convertFromCursor(cursor: Cursor) = inflateNote(cursor)
fun Card.convertFromCursor(cursor: Cursor) = inflateCard(cursor)

private fun fromCursor(cursor: Cursor, ankiType: AnkiType): Converter<Cursor, Any> = when(ankiType) {
    is CardAnki -> object : Converter<Cursor, Any> {
        override fun convert(from: Cursor) = inflateCard(cursor)
    }
    is ColAnki -> object : Converter<Cursor, Any> {
        override fun convert(from: Cursor) = inflateCol(cursor)
    }
    is GraveAnki -> object : Converter<Cursor, Any> {
        override fun convert(from: Cursor) = inflateGrave(cursor)
    }
    is NoteAnki -> object : Converter<Cursor, Any> {
        override fun convert(from: Cursor) = inflateNote(cursor)
    }
    is RevlogAnki -> object : Converter<Cursor, Any> {
        override fun convert(from: Cursor) = inflateRevLog(cursor)
    }
}

//private fun fromString(str: String, ankiType: AnkiType): Converter<String, Any> = when(ankiType) {
//    is NoteAnki -> object : Converter<String, Any> {
//        override fun convert(from: String) = inflateNote(str)
//    }
//    is CardAnki -> object : Converter<String, Any> {
//        override fun convert(from: String) = inflateCard(str)
//    }
//}

inline fun <T, R> iterateCursor(receiver: T, block: T.() -> R) {

}

public inline fun <T> Iterable<T>.forEach(action: (T) -> Unit): Unit {
    for (element in this) action(element)
}

inline fun Cursor.forEach(action: (Cursor) -> Unit): Unit {
    this.use {
        while (this.moveToNext()) {
            action(this)
        }
    }
}

fun testsome(list: List<Note>) {
    list.map {  }
}

fun inflateCard(cursor: Cursor) = with(cursor) {
    Card(getLong(getColumnIndex(AnkiType.CardAnki._ID)),
            getLong(getColumnIndex(CardAnki.NID)),
            getLong(getColumnIndex(CardAnki.DID)),
            getLong(getColumnIndex(CardAnki.ORD)),
            getLong(getColumnIndex(CardAnki.MOD)),
            getLong(getColumnIndex(CardAnki.USN)),
            getLong(getColumnIndex(CardAnki.TYPE)),
            getLong(getColumnIndex(CardAnki.QUEUE)),
            getLong(getColumnIndex(CardAnki.DUE)),
            getLong(getColumnIndex(CardAnki.IVL)),
            getLong(getColumnIndex(CardAnki.FACTOR)),
            getLong(getColumnIndex(CardAnki.REPS)),
            getLong(getColumnIndex(CardAnki.LAPSES)),
            getLong(getColumnIndex(CardAnki.LEFT)),
            getLong(getColumnIndex(CardAnki.ODUE)),
            getLong(getColumnIndex(CardAnki.ODID)),
            getLong(getColumnIndex(CardAnki.FLAGS)),
            getString(getColumnIndex(CardAnki.DATA)))
}

fun inflateCol(cursor: Cursor) = with(cursor) {
    Collection(getLong(getColumnIndex(ColAnki._ID)),
            getLong(getColumnIndex(ColAnki.CRT)),
            getLong(getColumnIndex(ColAnki.MOD)),
            getLong(getColumnIndex(ColAnki.SCM)),
            getLong(getColumnIndex(ColAnki.VER)),
            getLong(getColumnIndex(ColAnki.DTY)),
            getLong(getColumnIndex(ColAnki.USN)),
            getLong(getColumnIndex(ColAnki.LS)),
            getString(getColumnIndex(ColAnki.CONF)),
            getString(getColumnIndex(ColAnki.MODELS)),
            getString(getColumnIndex(ColAnki.DECKS)),
            getString(getColumnIndex(ColAnki.DCONF)),
            getString(getColumnIndex(ColAnki.TAGS)))
}

fun inflateGrave(cursor: Cursor) = with(cursor) {
    Grave(getLong(getColumnIndex(GraveAnki.USN)),
            getLong(getColumnIndex(GraveAnki.OID)),
            getLong(getColumnIndex(GraveAnki.TYPE)))
}

fun inflateNote(cursor: Cursor) = with(cursor) {
        Note(getLong(getColumnIndex(NoteAnki._ID)),
                getString(getColumnIndex(NoteAnki.GUID)),
                getLong(getColumnIndex(NoteAnki.MID)),
                getLong(getColumnIndex(NoteAnki.MOD)),
                getLong(getColumnIndex(NoteAnki.USN)),
                getString(getColumnIndex(NoteAnki.TAGS)),
                getString(getColumnIndex(NoteAnki.FLDS)),
                getString(getColumnIndex(NoteAnki.SFLD)),
                getLong(getColumnIndex(NoteAnki.CSUM)),
                getLong(getColumnIndex(NoteAnki.FLAGS)),
                getString(getColumnIndex(NoteAnki.DATA)))
}

fun inflateRevLog(cursor: Cursor) = with(cursor) {
    ReviewLog(getLong(getColumnIndex(RevlogAnki._ID)),
            getLong(getColumnIndex(RevlogAnki.CID)),
            getLong(getColumnIndex(RevlogAnki.USN)),
            getLong(getColumnIndex(RevlogAnki.EASE)),
            getLong(getColumnIndex(RevlogAnki.IVL)),
            getLong(getColumnIndex(RevlogAnki.LASTIVL)),
            getLong(getColumnIndex(RevlogAnki.FACTOR)),
            getLong(getColumnIndex(RevlogAnki.TIME)),
            getLong(getColumnIndex(RevlogAnki.TYPE)))
}

fun inflateNote(str: String): Note = with(str) {
    Note(0L,
            NoteAnki.GUID,
            0L,
            0L,
            0L,
            NoteAnki.TAGS,
            NoteAnki.FLDS,
            NoteAnki.SFLD,
            0L,
            0L,
            NoteAnki.DATA)
}

fun inflateCard(str: String): Card = with(str) {
    Card(0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            CardAnki.DATA)
}
