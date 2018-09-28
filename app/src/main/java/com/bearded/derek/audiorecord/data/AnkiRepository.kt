package com.bearded.derek.audiorecord.data

import android.content.ContentProviderClient
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.SystemClock
import android.util.Log
import com.bearded.derek.audiorecord.model.ankidb.*
import com.ichi2.anki.FlashCardsContract
import java.lang.ref.WeakReference

class AnkiRepository {

    interface Callback<Result: Any?> {
        fun onComplete(result: Result)
    }

    abstract class BaseTaskContext<Param: Any?, Result: Any?>(context: Context, callback: Callback<Result>? = null): AsyncTask<Param, Unit, Result>() {
        private val contextWeakReference = WeakReference<Context>(context)
        private var cancelled: Boolean = false
        var callbackWeakReference = WeakReference<Callback<Result>>(callback)

         final override fun onPostExecute(result: Result) {
             if (cancelled) {
                 return
             } else {
                 callbackWeakReference.get()?.let { it.onComplete(result) }
             }
        }

        final override fun doInBackground(vararg params: Param): Result? {
            if (params.isEmpty()) {
                cancelled = true
                return null
            }
            return executeOnBackground(params[0], contextWeakReference.get() ?: return null)
        }

        abstract fun executeOnBackground(param: Param, context: Context): Result?
    }

    abstract class InsertCursorTask<Entity, Dao>(context: Context, callback: Callback<List<Long>>? = null): BaseTaskContext<Cursor, List<Long>>(context, callback) {

        final override fun executeOnBackground(param: Cursor, context: Context): List<Long>? {
            val database: AnkiDatabase = AnkiDatabase.getInstance(context)!!
            val dao: Dao = getDao(database)
            val rowIds = ArrayList<Long>()

            database.runInTransaction {
                Log.d("Benchmark: Before Room insert", SystemClock.elapsedRealtime().toString())
                param.use { cursor ->
                    var entity: Entity
                    while (cursor.moveToNext()) {
                        entity = inflateFromCursor(cursor)
                        rowIds.add(insertWithDao(entity, dao))
                    }
                }

                Log.d("Benchmark: After Room insert", SystemClock.elapsedRealtime().toString())
                Log.d("Benchmark: Records count: ", rowIds.size.toString())
            }

            return rowIds
        }

        abstract fun getDao(ankiDatabase: AnkiDatabase): Dao
        abstract fun inflateFromCursor(cursor: Cursor): Entity
        abstract fun insertWithDao(entity: Entity, dao: Dao): Long
    }

    class InsertCursorTaskB<V, Entity>(context: Context, callback: Callback<List<Long>>? = null): BaseTaskContext<Cursor, List<Long>>(context, callback) {
        override fun executeOnBackground(param: Cursor, context: Context): List<Long>? {
            val database: AnkiDatabase = AnkiDatabase.getInstance(context)!!
            val rowIds = ArrayList<Long>()

            database.runInTransaction {
                Log.d("Benchmark: Before Room insert, new approach", SystemClock.elapsedRealtime().toString())
                param.forEach {
                    rowIds.add()
                }


            }
        }

    }

    // default result value (if param == null),
    class DatabaseCursorTask<T>(context: Context, callback: Callback<T>? = null, private val action: (Cursor) -> T?): BaseTaskContext<Cursor, T>(context, callback) {

        override fun executeOnBackground(param: Cursor, context: Context): T? {
            val database: AnkiDatabase = AnkiDatabase.getInstance(context)!!
            var t: T? = null

            database.runInTransaction {
                t = action(param)
            }

            return t
        }
    }

    fun insertCards(context: Context) {
        val task = DatabaseCursorTask<List<Long>>(context, null) {
            inflateCard(it)
        }
    }

    class InsertCursorTaskC<T, D>(context: Context, callback: Callback<List<Long>>?,
                                  private val getDao: AnkiDatabase.() -> D,
                                  private val inflate: (Cursor) -> T,
                                  private val daoAction: D.(T) -> Long) : BaseTaskContext<Cursor,
            List<Long>>(context, callback) {

        override fun executeOnBackground(param: Cursor, context: Context): List<Long> {
            val database: AnkiDatabase = AnkiDatabase.getInstance(context)!!
            val dao = getDao(database)
            val result = ArrayList<Long>()


            database.runInTransaction {
                param.use {
                    while (param.moveToNext()) {
                        result.add(dao.daoAction(inflate(param)))
                    }
                }
            }

            return result
        }
    }

    val nDao: AnkiDatabase.() -> NoteDao = { noteDao() }
    val inf: (Cursor) -> Note = { inflateNote(it) }
    val dA: NoteDao.(Note) -> Long = { this.insert(it) }

    fun insertCardsB(context: Context) {
        val task = InsertCursorTaskC<Note, NoteDao>(context, null, AnkiDatabase::noteDao, { inflateNote(it) }) {
            insert(it)
        }
    }

    fun insertCardsC(context: Context) = InsertCursorTaskC(context, null, AnkiDatabase::cardDao, { inflateCard(it) }) {
            insert(it)
    }.execute()

    class InsertNotesTask(context: Context, callback: Callback<List<Long>>? = null): InsertCursorTask<Note, NoteDao>(context, callback) {
        override fun getDao(ankiDatabase: AnkiDatabase): NoteDao {
            return ankiDatabase.noteDao()
        }

        override fun inflateFromCursor(cursor: Cursor): Note {
            return Note(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getLong(8),
                    cursor.getLong(9),
                    cursor.getString(10))
        }

        override fun insertWithDao(entity: Note, dao: NoteDao): Long {
            return dao.insert(entity)
        }
    }

    class InsertCardsTask(context: Context, callback: Callback<List<Long>>? = null): InsertCursorTask<Card, CardDao>(context, callback) {
        override fun getDao(ankiDatabase: AnkiDatabase): CardDao {
            return ankiDatabase.cardDao()
        }

        override fun inflateFromCursor(cursor: Cursor): Card {
            return Card(cursor.getLong(0),
                    cursor.getLong(1),
                    cursor.getLong(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getLong(5),
                    cursor.getLong(6),
                    cursor.getLong(7),
                    cursor.getLong(8),
                    cursor.getLong(9),
                    cursor.getLong(10),
                    cursor.getLong(11),
                    cursor.getLong(12),
                    cursor.getLong(13),
                    cursor.getLong(14),
                    cursor.getLong(15),
                    cursor.getLong(16),
                    cursor.getString(17))
        }

        override fun insertWithDao(entity: Card, dao: CardDao): Long {
            return dao.insert(entity)
        }
    }

    class QueryRoomNotes(context: Context, callback: Callback<List<Note>?>): BaseTaskContext<Unit, List<Note>?>(context, callback) {
        override fun executeOnBackground(param: Unit, context: Context): List<Note>? {
            Log.d("Benchmark: Before Room query", SystemClock.elapsedRealtime().toString())
            val notes: List<Note> = AnkiDatabase.getInstance(context)?.let { it.noteDao().getAll() } ?: emptyList()
            Log.d("Benchmark: After Room query", SystemClock.elapsedRealtime().toString())
            return notes
        }
    }

    class QueryAnkiTask(context: Context, callback: Callback<Cursor?>): BaseTaskContext<Uri, Cursor?>(context, callback) {

        override fun executeOnBackground(param: Uri, context: Context): Cursor? {
            val contentProvider: ContentProviderClient = context.contentResolver
                    .acquireContentProviderClient(Uri.parse("content://"
                            + FlashCardsContract.AUTHORITY)) ?: return null

            var result: Cursor? = null
            try {
                Log.d("Benchmark: Before query", SystemClock.elapsedRealtime().toString())
                result = contentProvider.query(param, null, null, null, null)
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }

            contentProvider.close()
            Log.d("Benchmark: After query", SystemClock.elapsedRealtime().toString())

            return result
        }
    }
}