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

fun insertNotes(context: Context, callback: Callback<List<Long>>?, cursor: Cursor) = InsertCursorTask(context,
        callback,
        AnkiDatabase::noteDao,
        { inflateNote(it) }) {
    insert(it)
}.execute(cursor)

fun insertCards(context: Context) = InsertCursorTask(context,
        null,
        AnkiDatabase::cardDao,
        { inflateCard(it) }) {
    insert(it)
}.execute()

fun queryAndInsertCards(context: Context, callback: Callback<List<Long>>?) = QueryAndInsertTask(context,
        callback,
        AnkiDatabase::cardDao,
        { inflateCard(it) }) {
    insert(it)
}.execute(Uri.withAppendedPath(FlashCardsContract.AUTHORITY_URI, "cards"))

fun queryAndInsertCol(context: Context, callback: Callback<List<Long>>?) = QueryAndInsertTask(context,
        callback,
        AnkiDatabase::collectionDao,
        { inflateCol(it) }) {
    insert(it)
}.execute(Uri.withAppendedPath(FlashCardsContract.AUTHORITY_URI, "col"))

fun queryAndInsertGraves(context: Context, callback: Callback<List<Long>>?) = QueryAndInsertTask(context,
        callback,
        AnkiDatabase::graveDao,
        { inflateGrave(it) }) {
    insert(it)
}.execute(Uri.withAppendedPath(FlashCardsContract.AUTHORITY_URI, "graves"))

fun queryAndInsertNotes(context: Context, callback: Callback<List<Long>>?) = QueryAndInsertTask(context,
        callback,
        AnkiDatabase::noteDao,
        { inflateNote(it) }) {
    insert(it)
}.execute(Uri.withAppendedPath(FlashCardsContract.AUTHORITY_URI, "notes"))

fun queryAndInsertRevlog(context: Context, callback: Callback<List<Long>>?) = QueryAndInsertTask(context,
        callback,
        AnkiDatabase::revlogDao,
        { inflateRevLog(it) }) {
    insert(it)
}.execute(Uri.withAppendedPath(FlashCardsContract.AUTHORITY_URI, "revlog"))


interface Callback<Result : Any?> {
    fun onComplete(result: Result)
}

abstract class BaseTaskContext<Param : Any?, Result : Any?>(context: Context, callback: Callback<Result>? = null) : AsyncTask<Param, Unit, Result>() {
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


class InsertCursorTask<T, D>(context: Context, callback: Callback<List<Long>>?,
                                  private val getDao: AnkiDatabase.() -> D,
                                  private val inflate: (Cursor) -> T,
                                  private val daoAction: D.(T) -> Long) : BaseTaskContext<Cursor,
        List<Long>>(context, callback) {
    override fun executeOnBackground(param: Cursor, context: Context): List<Long> {
        val database: AnkiDatabase = AnkiDatabase.getInstance(context)!!
        val dao = getDao(database)
        val result = ArrayList<Long>()

        database.runInTransaction {
            Log.d("Before Room insert", SystemClock.elapsedRealtime().toString())
            param.use {
                while (param.moveToNext()) {
                    result.add(dao.daoAction(inflate(it)))
                }
            }
        }

        Log.d("After Room insert", SystemClock.elapsedRealtime().toString())
        Log.d("Records count: ", result.size.toString())

        return result
    }

}

class QueryRoomNotes(context: Context, callback: Callback<List<Note>?>) : BaseTaskContext<Int, List<Note>?>(context, callback) {
    override fun executeOnBackground(param: Int, context: Context): List<Note>? {
        Log.d("Before Room query", SystemClock.elapsedRealtime().toString())
        val notes: List<Note> = AnkiDatabase.getInstance(context)?.let { it.noteDao().getAll() }
                ?: emptyList()
        Log.d("After Room query", SystemClock.elapsedRealtime().toString())
        return notes
    }
}

class QueryAnkiTask(context: Context, callback: Callback<Cursor?>) : BaseTaskContext<Uri, Cursor?>(context, callback) {

    override fun executeOnBackground(param: Uri, context: Context): Cursor? {
        val contentProvider: ContentProviderClient = context.contentResolver
                .acquireContentProviderClient(Uri.parse("content://"
                        + FlashCardsContract.AUTHORITY)) ?: return null

        var result: Cursor? = null
        try {
            Log.d("Before query", SystemClock.elapsedRealtime().toString())
            result = contentProvider.query(param, null, null, null, null)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        contentProvider.close()
        Log.d("After query", SystemClock.elapsedRealtime().toString())

        return result
    }
}

class QueryAndInsertTask<T, D>(context: Context,
                               callback: Callback<List<Long>>?,
                               private val getDao: AnkiDatabase.() -> D,
                               private val inflate: (Cursor) -> T,
                               private val daoAction: D.(T) -> Long) : BaseTaskContext<Uri, List<Long>>(context, callback) {

    override fun executeOnBackground(param: Uri, context: Context): List<Long> {
        val contentProviderClient: ContentProviderClient = context.contentResolver.acquireContentProviderClient(FlashCardsContract.AUTHORITY_URI)
        val cursor: Cursor = contentProviderClient.use {
            Log.d("Before query", SystemClock.elapsedRealtime().toString())
            contentProviderClient.query(param, null, null, null, null)
        }
        Log.d("After query", SystemClock.elapsedRealtime().toString())

        val database: AnkiDatabase = AnkiDatabase.getInstance(context)!!
        val dao = getDao(database)
        val result = ArrayList<Long>()

        database.runInTransaction {
            Log.d("Before Room insert", SystemClock.elapsedRealtime().toString())
            cursor.use {
                while (cursor.moveToNext()) {
                    result.add(dao.daoAction(inflate(it)))
                }
            }
        }

        Log.d("After Room insert", SystemClock.elapsedRealtime().toString())
        Log.d("Records count: ", result.size.toString())

        return result
    }

}