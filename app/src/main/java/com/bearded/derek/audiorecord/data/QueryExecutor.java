package com.bearded.derek.audiorecord.data;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class QueryExecutor extends IntentService {

    public static final String TABLE_NAME = "tableName";

    public QueryExecutor() {
        super("QueryExecutor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String table_name = intent.getStringExtra(TABLE_NAME);
        if (table_name == null) {
            return;
        }
        switch (table_name) {
            case "cards": {
                AnkiRepositoryKt.queryAndInsertCards(this, null);
                break;
            }
            case "col": {
                AnkiRepositoryKt.queryAndInsertCol(this, null);
                break;
            }
            case "graves": {
                AnkiRepositoryKt.queryAndInsertGraves(this, null);
                break;
            }
            case "notes": {
                AnkiRepositoryKt.queryAndInsertNotes(this, null);
                break;
            }
            case "revlog": {
                AnkiRepositoryKt.queryAndInsertRevlog(this, null);
                break;
            }
        }
    }
}
