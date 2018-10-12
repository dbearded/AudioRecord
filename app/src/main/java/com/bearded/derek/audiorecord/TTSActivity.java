package com.bearded.derek.audiorecord;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.ichi2.anki.FlashCardsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TTSActivity extends AppCompatActivity {

    TextToSpeech tts;
    boolean ttsReady;

    @Override
    protected void onResume() {
        super.onResume();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    ttsReady = true;

                    tts.speak("Hi my name is Derek and this is my Hello world for TTS", TextToSpeech.QUEUE_FLUSH, null, "unique");
                }

            }
        });

        ContentResolver cr = getContentResolver();

        Uri scheduled_cards_uri = FlashCardsContract.ReviewInfo.CONTENT_URI;
        String deckArguments[] = new String[]{"5", "123456789"};
        String deckSelector = "limit=?, deckID=?";
//        final Cursor cur = cr.query(scheduled_cards_uri,
//                null,  // projection
//                deckSelector,  // if null, default values will be used
//                deckArguments,  // if null, the deckSelector must not contain any placeholders ("?")
//                null   // sortOrder is ignored for this URI
//        );

        String deckSelect = "limit=?";
        String deckArgs[] = new String[]{"100"};

        final Cursor cur = cr.query(scheduled_cards_uri,
                null,  // projection
                deckSelect,  // if null, default values will be used
                deckArgs,  // if null, the deckSelector must not contain any placeholders ("?")
                null   // sortOrder is ignored for this URI
        );


        String[] columns = cur.getColumnNames();

        List<Long> noteIds = new ArrayList<>();
        List<Long> cardOrds = new ArrayList<>();
        List<Long> buttonCounts = new ArrayList<>();
        List<String> nextReviewTimes = new ArrayList<>();
        List<String> mediaFiles = new ArrayList<>();

        try {
            while (cur.moveToNext()) {
                noteIds.add(cur.getLong(0));
                cardOrds.add(cur.getLong(1));
                buttonCounts.add(cur.getLong(2));
                nextReviewTimes.add(cur.getString(3));
                mediaFiles.add(cur.getString(4));
            }
        } finally {
            cur.close();
        }

        double i = Math.floor(2.5);

    }
}
