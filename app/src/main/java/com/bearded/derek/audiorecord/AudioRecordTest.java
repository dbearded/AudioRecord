package com.bearded.derek.audiorecord;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.bearded.derek.audiorecord.data.AnkiRepository;
import com.bearded.derek.audiorecord.model.ankidb.Note;
import com.ichi2.anki.FlashCardsContract;
import com.ichi2.anki.api.AddContentApi;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AudioRecordTest extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final String AUTHORITY = "com.bearded.derek.audiorecord.fileprovider";
    private static final String MIME_TYPE = "audiorecord/front-back";
    private static final String ACTION_SAVE = "com.bearded.derek.audiosave.SAVE_FILE";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFilePathFront = null;
    private static String mFilePathBack = null;

    boolean mStartPlaying = true;

    boolean mStartRecording = true;

    private AppCompatButton mRecordButton, mShareButton, mPlayButton, mCreateNoteButton;

    private MediaRecorder mRecorder = null;

    private MediaPlayer mPlayer = null;

    private File recordingsDir;

    private CardView frontCard, backCard;

    private TextView frontName, backName;

    private boolean cardSelected = false;

    private AnkiHelper ankiHelper;

    private AnkiHelper.Deck deck;

    private AnkiHelper.NoteType noteType;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;

    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private List<Pair<Integer, RequestPermissionsResultListener>> requestPermissionsResultListeners;

    interface RequestPermissionsResultListener {
        void onRequestPermissionResult(String[] permissions, int[] grantResults);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (Pair<Integer, RequestPermissionsResultListener> pair:
        requestPermissionsResultListeners){
            if (pair.first == requestCode) {
                pair.second.onRequestPermissionResult(permissions, grantResults);
            }
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void onShare() {
        Uri frontUri = FileProvider.getUriForFile(this, AUTHORITY, new File(mFilePathFront));
        Uri backUri = FileProvider.getUriForFile(this, AUTHORITY, new File(mFilePathBack));
        ClipData clipData = ClipData.newUri(getContentResolver(), "Front and Back Card audio", frontUri);
        clipData.addItem( new ClipData.Item(backUri));
        Intent intent = new Intent(ACTION_SAVE);
        intent.setClipData(clipData);
        intent.setType(Intent.normalizeMimeType(MIME_TYPE));
        intent.setFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                mPlayButton.setText("Start playing");
                mStartPlaying = true;
            }
        });
        try {
            mPlayer.setDataSource(getMediaFilePath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            mPlayButton.setText("Stop playing");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(getMediaFilePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private String getMediaFilePath() {
        File file;
        if (cardSelected) {
            file = getNewFile("Front ");
            mFilePathFront = file.getAbsolutePath();
            return mFilePathFront;
        } else {
            file = getNewFile("Back ");
            mFilePathBack = file.getAbsolutePath();
            return mFilePathBack;
        }
    }

    private String getMediaFileName() {
        File path;
        if (cardSelected) {
            path = new File(mFilePathFront);
        } else {
            path = new File(mFilePathBack);
        }
        return path.getName();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        updateText();
    }

    private void updateText() {
        if (cardSelected) {
            frontName.setText(getMediaFileName());
        } else {
            backName.setText(getMediaFileName());
        }
    }

    private void setupDirs() {
        recordingsDir = new File(getFilesDir(), getString(R.string.recordings_dir));
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs();
        }
    }

    private File getNewFile(String name) {
        if (name == null) {
            return new File(recordingsDir, getFilename());
        } else {
            return new File(recordingsDir, name + getFilename());
        }
    }

    private String getFilename() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        return formatter.format(now) + ".3gp";
    }

    private void setCardColor(boolean cardSelected) {
        if (cardSelected) {
            frontCard.setCardBackgroundColor(getResources().getColor(R.color.cardSelected));
            backCard.setCardBackgroundColor(getResources().getColor(R.color.cardNotSelected));
        } else {
            frontCard.setCardBackgroundColor(getResources().getColor(R.color.cardNotSelected));
            backCard.setCardBackgroundColor(getResources().getColor(R.color.cardSelected));
        }
    }

    void setRequestPermissionsResultListener(int requestCode, RequestPermissionsResultListener listener) {
        requestPermissionsResultListeners.add(new Pair<>(requestCode, listener));
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_audio_record_test);

        setupDirs();
        deck = createDeck();
        noteType = createNoteType();
        ankiHelper = new AnkiHelper(new WeakReference<Context>(AudioRecordTest.this));
        requestPermissionsResultListeners = new ArrayList<>();

        mFilePathFront = getNewFile("Front ").getAbsolutePath();
        mFilePathBack = getNewFile("Back ").getAbsolutePath();

        frontCard = findViewById(R.id.frontCard);
        backCard = findViewById(R.id.backCard);

        frontCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSelected = true;
                setCardColor(cardSelected);
                (new AnkiRepository.QueryAnkiTask(AudioRecordTest.this, new AnkiRepository.Callback<Cursor>() {
                    @Override
                    public void onComplete(Cursor cursor) {
                        setCards(cursor);
                    }
                })).execute(FlashCardsContract.Note.CONTENT_URI);
            }
        });

        backCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSelected = false;
                setCardColor(cardSelected);
            }
        });

        frontName = findViewById(R.id.frontContent);
        backName = findViewById(R.id.backContent);

        mRecordButton = findViewById(R.id.record_button);
        mPlayButton = findViewById(R.id.play_button);
        mShareButton = findViewById(R.id.share_button);
        mCreateNoteButton = findViewById(R.id.create_note_button);

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    mRecordButton.setText("Stop recording");
                } else {
                    mRecordButton.setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlayButton.setText("Stop playing");
                } else {
                    mPlayButton.setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        mShareButton.setText("Share");
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShare();
            }
        });

        mCreateNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteToAnki();
            }
        });
    }

    private void setCards(Cursor cursor) {
        AnkiRepository.InsertNotesTask insertNotesTask = new AnkiRepository.InsertNotesTask(this, new AnkiRepository.Callback<List<Long>>() {
            @Override
            public void onComplete(List<Long> longs) {
                setNotes(longs);
            }
        });

        insertNotesTask.execute(cursor);
    }

    private void setNotes(List<Long> longs) {
        int i = 0;
        for (Long longg :
                longs) {
            i++;
        }

        (new AnkiRepository.QueryRoomNotes(this, new AnkiRepository.Callback<List<Note>>() {
            @Override
            public void onComplete(List<Note> notes) {
                // Do nothing
            }
        })).execute();
    }

    private void addNoteToAnki() {
        ankiHelper.addNote(createNote());
    }

    private AnkiHelper.Note createNote() {
        AnkiHelper.Note note = new AnkiHelper.Note();
        File front = new File(mFilePathFront);
        File back = new File(mFilePathBack);
        note.appendField(front.getName());
        note.appendField(back.getName());
        note.appendTag("AudioRecord");
        note.setDeck(deck);
        note.setNoteType(noteType);
        return note;
    }

    private AnkiHelper.Deck createDeck() {
        AnkiHelper.Deck deck = new AnkiHelper.Deck();
        deck.setName(AnkiHelper.DECK_NAME);
        return deck;
    }

    private AnkiHelper.NoteType createNoteType() {
        AnkiHelper.NoteType noteType = new AnkiHelper.NoteType();
        noteType.setName(AnkiHelper.NOTE_TYPE);
        return noteType;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void experimentFiles() {
        // Record to the external cache directory for visibility
//        mFilePath = getFilesDir().getAbsolutePath();
        File file = getDir("recordings", MODE_PRIVATE);
        File recordingsPath = new File(getFilesDir(), "audio");
        recordingsPath.mkdirs();
        File recording = new File(recordingsPath, "audiorecordtest.3gp");
        Uri contentUri = FileProvider.getUriForFile(this,
                "com.bearded.derek.audiorecord.fileprovider", recording);
        String relative = getFilesDir().getPath();
        File recording2 = new File(recordingsPath, "filetest.3gp");
//        String canonical = getFilesDir().getCanonicalPath();
//        mFilePath += "/audiorecordtest.3gp";
//        String[] datas = getDataDir().list();
//        String[] files = getFilesDir().list();
    }

    private static class AnkiHelper {
        private static final String DECK_NAME = "AudioRecord Test";
        private static final String NOTE_TYPE = "Basic";
        private static final int ADD_PERM_REQUEST = 10;

        private WeakReference<Context> contextWeakReference;

        private abstract class AnkiJob<T> {
            static final int REQUEST_CODE = 10;
            T result;

            void execute() {
                if (shouldRequestPermission(AddContentApi.READ_WRITE_PERMISSION)) {
                    checkForPermission(new String[]{AddContentApi.READ_WRITE_PERMISSION}, REQUEST_CODE,
                            new RequestPermissionsResultListener() {
                                @Override
                                public void onRequestPermissionResult(String[] permissions, int[] grantResults) {
                                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                        Context context = contextWeakReference.get();
                                        if (context != null) {
                                            result = AnkiJob.this.performAction(new AddContentApi(context));
                                        }
                                    }
                                }
                            });
                } else {
                    Context context = contextWeakReference.get();
                    if (context != null) {
                        result = AnkiJob.this.performAction(new AddContentApi(context));
                    }
                }
            }

            abstract T performAction(AddContentApi ankiApi);

            T getResult() {
                return result;
            }

            private boolean shouldRequestPermission(String permission) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return false;
                }

                Context context = contextWeakReference.get();
                if (context == null) {
                    return false;
                }
                return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
            }

            private void checkForPermission(String[] permissions, int requestCode, RequestPermissionsResultListener listener) {
                Context context = contextWeakReference.get();
                if (context == null) {
                    return;
                }
                AudioRecordTest activity = (AudioRecordTest) context;
                activity.setRequestPermissionsResultListener(requestCode, listener);
                ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
            }
        }

        interface Identifiable {
            void setId(Long id);
            Long getId();
            void setName(String name);
            String getName();
        }

        private static class Note {
            private List<String> fields;
            private Set<String> tags;
            private Long noteId;
            private Deck deck;
            private NoteType noteType;

            Note() {
                fields = new LinkedList<>();
                tags = new HashSet<>();
            }

            void appendField(String field) {
                fields.add(field);
            }

            String[] fieldsToStringArray() {
                String[] flds = new String[fields.size()];
                for(int i = 0; i < flds.length; i++) {
                    flds[i] = fields.get(i);
                }

                return flds;
            }

            List<String> getFields() {
                return fields;
            }

            void appendTag(String tag) {
                tags.add(tag);
            }

            void setNoteId(long noteId) {
                this.noteId = noteId;
            }

            long getNoteId() {
                return noteId;
            }

            Set<String> getTags() {
                return tags;
            }

            NoteType getNoteType() {
                return noteType;
            }

            void setNoteType(NoteType noteType) {
                this.noteType = noteType;
            }

            Deck getDeck() {
                return deck;
            }

            void setDeck(Deck deck) {
                this.deck = deck;
            }
        }

        private static class NoteType implements Identifiable {
            private Long id;
            private String name;

            @Override
            public void setId(Long id) {
                this.id = id;
            }

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }
        }

        private static class Deck implements Identifiable {
            private Long id;
            private String name;

            @Override
            public void setId(Long id) {
                this.id = id;
            }

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }
        }

        AnkiHelper(WeakReference<Context> contextWeakReference) {
            this.contextWeakReference = contextWeakReference;
        }

        public Note addNote(final Note note) {

            if (TextUtils.isEmpty(note.getDeck().getName()) || note.getDeck().getId() == null) {
                Deck newDeck = getDeck(note.getDeck());
                if (newDeck == null) {
                    return null;
                }
                note.setDeck(newDeck);
            }

            if (TextUtils.isEmpty(note.getNoteType().getName()) || note.getNoteType().getId() == null) {
                NoteType newNoteType = getNoteType(note.getNoteType());
                if (newNoteType == null) {
                    return null;
                }
                note.setNoteType(newNoteType);
            }

            /*AnkiJob<Long> job = new AnkiJob<Long>() {
                @Override
                Long performAction(AddContentApi ankiApi) {
                    return ankiApi.addNote(note.getNoteType().getId(), note.getDeck().getId(),
                            note.fieldsToStringArray(), note.getTags());
                }
            };
            job.execute();*/

            AnkiJob<Long> job = new AnkiJob<Long>() {
                @Override
                Long performAction(AddContentApi ankiApi) {
                    Context context = contextWeakReference.get();
                    if (context == null) {
                        return -1l;
                    }
                    Uri frontUri = FileProvider.getUriForFile(context, AUTHORITY, new File(mFilePathFront));
                    Uri backUri = FileProvider.getUriForFile(context, AUTHORITY, new File(mFilePathBack));
                    context.grantUriPermission("com.ichi2.anki", frontUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.grantUriPermission("com.ichi2.anki", backUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String frontPath = frontUri.toString();
                    String backPath = backUri.toString();

                    String frontField = "[sound:" + frontPath + "]";
                    String backField = "[sound:" + backPath + "]";
                    String fields = TextUtils.join("\u001f", new String[]{frontField, backField});

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FlashCardsContract.Note.MID, note.getNoteType().getId());
                    contentValues.put(FlashCardsContract.Note.FLDS, fields);
                    contentValues.put(FlashCardsContract.Note.TAGS, "audiorecord");
                    Uri noteUri = Uri.withAppendedPath(FlashCardsContract.AUTHORITY_URI, "notes_multimedia");
                    ContentResolver mResolver = context.getContentResolver();
                    Uri newNoteUri = context.getContentResolver().insert(noteUri, contentValues);


                    if (newNoteUri == null) {
                        return null;
                    }
                    // Move cards to specified deck
                    String noteId = newNoteUri.getLastPathSegment();
                    Uri cardsUri = Uri.withAppendedPath(FlashCardsContract.AUTHORITY_URI, "notes/" + noteId + "/" + "cards");
                    final Cursor cardsCursor = mResolver.query(cardsUri, null, null, null, null);
                    if (cardsCursor == null) {
                        return null;
                    }
                    try {
                        while (cardsCursor.moveToNext()) {
                            String ord = cardsCursor.getString(cardsCursor.getColumnIndex(FlashCardsContract.Card.CARD_ORD));
                            ContentValues cardValues = new ContentValues();
                            cardValues.put(FlashCardsContract.Card.DECK_ID, note.getDeck().getId());
                            Uri cardUri = Uri.withAppendedPath(cardsUri, ord);
                            mResolver.update(cardUri, cardValues, null, null);
                        }
                    } finally {
                        cardsCursor.close();
                    }

                    context.revokeUriPermission(frontUri, Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    context.revokeUriPermission(backUri, Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    return Long.parseLong(newNoteUri.getLastPathSegment());

                    /*ClipData clipData = ClipData.newUri(context.getContentResolver(), "Front and Back Card audio", frontUri);
                    clipData.addItem( new ClipData.Item(backUri));
                    Intent intent = new Intent(ACTION_SAVE);
                    intent.setClipData(clipData);
                    intent.setType(Intent.normalizeMimeType(MIME_TYPE));
                    intent.setFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/

                }
            };
            job.execute();

            Long noteId = job.getResult();

            if (noteId != null) {
                note.setNoteId(noteId);
                return note;
            } else {
                return null;
            }
        }

        Deck addDeck(final Deck deck) {
            if (deck.getName() == null) {
                return null;
            }

            AnkiJob<Long> job = new AnkiJob<Long>() {
                @Override
                Long performAction(AddContentApi ankiApi) {
                    return ankiApi.addNewDeck(deck.getName());
                }
            };
            job.execute();

            Long deckId = job.getResult();
            if (deckId != null) {
                deck.setId(job.getResult());
                return deck;
            } else {
                return null;
            }
        }

        Deck getDeck(Deck deck) {
            Deck existingDeck = findDeck(deck);
            if (existingDeck != null) {
                return existingDeck;
            } else {
                return addDeck(deck);
            }
        }

        NoteType getNoteType(NoteType noteType) {
            NoteType existingNoteType = findNoteType(noteType);
            if (existingNoteType != null) {
                return existingNoteType;
            } else {
                throw new IllegalArgumentException("No notetype found: " + noteType.getName() + " " +noteType.getId());
            }
        }

        Deck findDeck(final Deck deck) {
            AnkiJob<Deck> job = new AnkiJob<Deck>() {
                @Override
                Deck performAction(AddContentApi ankiApi) {
                    return (Deck) checkForExisting(deck, ankiApi.getDeckList());
                }
            };
            job.execute();

            return job.getResult();
        }



        NoteType findNoteType(final NoteType noteType) {
            AnkiJob<NoteType> job = new AnkiJob<NoteType>() {
                @Override
                NoteType performAction(AddContentApi ankiApi) {
                    return (NoteType) checkForExisting(noteType, ankiApi.getModelList());
                }
            };
            job.execute();

            return job.getResult();
        }

        Identifiable checkForExisting(Identifiable identifiable, Map<Long, String> existing) {
            String name = identifiable.getName();
            Long id = identifiable.getId();

            if (name == null && id == null) {
                return null;
            }

            for (Map.Entry<Long, String> component : existing.entrySet()) {
                if (name != null && component.getValue().equals(name)) {
                    id = component.getKey();
                    identifiable.setId(id);
                    return identifiable;
                }

                if (id != null && component.getKey().equals(id)) {
                    name = component.getValue();
                    identifiable.setName(name);
                    return identifiable;
                }
            }

            return null;
        }
    }
}