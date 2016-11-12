package dv606.my222au.assignment2.mp3player;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dv606.my222au.assignment2.R;

public class Mp3Player extends AppCompatActivity {
    private static final String TAG = Mp3Player.class.getSimpleName();
    private ImageView mPlayButton;
    private ImageView mNextButton;
    private ImageView mPreviousButton;
    private ImageView mPausebutton;
    private Mp3PlayerService mMp3Service;
    private ListView mListView;
    private Intent mIntent;
    private SongListData mSongListData;
    private ArrayList<Track> mTracks;


    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName cName, IBinder binder) {
            mMp3Service = ((Mp3PlayerService.Mp3Binder) binder).getService();

        }

        //@Override   //
        public void onServiceDisconnected(ComponentName cName) {
            mMp3Service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3player);


        mPlayButton = (ImageView) findViewById(R.id.PlayButtonImage);
        mNextButton = (ImageView) findViewById(R.id.skipNextButton);
        mPreviousButton = (ImageView) findViewById(R.id.skipPreviousButton);
        mPausebutton   = (ImageView)  findViewById(R.id.pauseButtonImage);
        mPausebutton.setVisibility(View.INVISIBLE);


        mListView = (ListView) findViewById(R.id.listView);
        AddTrackData addTrackData = new AddTrackData();
        addTrackData.execute(0);
        mSongListData = new SongListData(this.getContentResolver());
        mTracks = mSongListData.tracks;
        bindToservice();

        if(mMp3Service!=null){
        Log.d(TAG, "onCreate: " + mMp3Service.mState.toString());}{

        }


        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMp3Service.mState == Mp3PlayerService.State.STOPPED) {
                    mMp3Service.playTracks(mTracks, 0);
                    uppatePlayPauseButton();
                } else if (mMp3Service.mState == Mp3PlayerService.State.PAUSED) {
                    mMp3Service.resume();
                    uppatePlayPauseButton();
                }else if(mMp3Service.mState==Mp3PlayerService.State.PLAYING){
                    mMp3Service.pause();
                    uppatePlayPauseButton();
                }
            }
        });

        mPausebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMp3Service.mState== Mp3PlayerService.State.PLAYING){
                    mMp3Service.pause();
                    uppatePlayPauseButton();
                }
            }
        });


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMp3Service.playNextTrack();
                uppatePlayPauseButton();

            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMp3Service.playPrevoiusTrack();
                uppatePlayPauseButton();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    private void bindToservice() {
        mIntent = new Intent(getApplicationContext(), Mp3PlayerService.class);
        startService(mIntent);
        this.bindService(mIntent, connection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "Binding started");
    }


    public void uppatePlayPauseButton() {
        if (mMp3Service != null) {
            if(mMp3Service.mState == Mp3PlayerService.State.STOPPED){

            }
            if (mMp3Service.mState == Mp3PlayerService.State.PLAYING) {
              mPlayButton.setVisibility(View.INVISIBLE);
                mPausebutton.setVisibility(View.VISIBLE);
            } else if (mMp3Service.mState== Mp3PlayerService.State.PAUSED) {
                mPlayButton.setVisibility(View.VISIBLE);
                mPausebutton.setVisibility(View.INVISIBLE);
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mMp3Service!=null) {
            Log.d(TAG, "onResume: " + mMp3Service.mState.toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       unbindService(connection);

    }


    public class TackListAdpater extends ArrayAdapter<Track> {
        List<Track> tracks;

        public TackListAdpater(Context context, ArrayList<Track> objects) {
            super(context, 0, objects);
            tracks = objects;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Track track1 = getItem(position);
            convertView = getLayoutInflater().inflate(R.layout.track_list_item, parent, false);
            TextView trackName = (TextView) convertView.findViewById(R.id.trackName);
            trackName.setText(String.valueOf(track1));
            convertView.setTag(track1);

            return convertView;

        }


    }


    public class AddTrackData extends AsyncTask<Integer, Integer, SongListData> {


        @Override
        protected SongListData doInBackground(Integer... integers) {

            SongListData data = new SongListData(getApplicationContext().getContentResolver());
            if(data.isStorageAvailable()){
            data.addData();}
            return data;
        }

        @Override
        protected void onPostExecute(SongListData songListData) {
            mTracks = songListData.trackList();
            final ListAdapter adapter = new TackListAdpater(getApplicationContext(), mTracks);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mMp3Service.playTracks(mTracks, i);
                    uppatePlayPauseButton();


                }
            });


        }


    }

private  class SongListData {
        ContentResolver mContentResolver;
        ArrayList<Track> tracks = new ArrayList<Track>();


        public  SongListData(ContentResolver contentResolver){
            mContentResolver =contentResolver;
        }



        public ArrayList<Track>trackList() {
            return tracks;


        }

        public void addData() {
            Cursor cursor = mContentResolver.query( // using content resolver to read music from media storage
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.DATA},
                    MediaStore.Audio.Media.IS_MUSIC + " > 0 ",
                    null, null
            );

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Track previous = null;

                do {
                    Track track = new Track(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                    if (previous != null)
                        previous.setNext(track);
                    previous = track;
                    tracks.add(track);



                }
                while (cursor.moveToNext());
                previous.setNext(tracks.get(0));

            }
            cursor.close();
        }


        private Boolean isStorageAvailable() {

            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        }
    }


}
