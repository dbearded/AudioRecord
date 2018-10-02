package com.bearded.derek.audiorecord.model.ankidb

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Card::class, Collection::class, Grave::class, Note::class, ReviewLog::class], version = 1, exportSchema = false)
abstract class AnkiDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun collectionDao(): CollectionDao
    abstract fun graveDao(): GraveDao
    abstract fun noteDao(): NoteDao
    abstract fun revlogDao(): ReviewLogDao

    companion object {

        private var INSTANCE: AnkiDatabase? = null

        fun getInstance(context: Context): AnkiDatabase? {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                        ?: buildDatabase(context).also { INSTANCE = it }
            }
        }


        private fun buildDatabase(context: Context): AnkiDatabase {
            return Room.databaseBuilder(context, AnkiDatabase::class.java, "ankiDatabase.db")
                    .build()
        }
    }
}