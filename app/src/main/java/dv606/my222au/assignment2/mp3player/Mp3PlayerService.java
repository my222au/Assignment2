package dv606.my222au.assignment2.mp3player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import dv606.my222au.assignment2.R;

public class Mp3PlayerService extends Service implements   MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {



    public  final int  STOPPED =3;
  public static final int NOTIFICATION_ID =12;
    private static final String TAG = Mp3PlayerService.class.getSimpleName() ;
    private Mp3Playertask mPlayertask;
    private final IBinder binder = new Mp3Binder();
    public  State  mState;

    private Notification mNotification;
    private  NotificationManager notifManager;






    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mState = State.STOPPED;
    }


    public enum State {
        PLAYING,
        STOPPED,
        PAUSED

    }

    public  void playTracks(ArrayList<Track> tracklist , int postion){
        if(mPlayertask==null){
            mState = State.PLAYING;
        mPlayertask = new Mp3Playertask(tracklist,postion);
        mPlayertask.execute();
        }
        else {
            mPlayertask.stopPlay();
            mPlayertask = new Mp3Playertask(tracklist,postion);
            mPlayertask.execute();
        }


    }
    public void playNextTrack(){
        mPlayertask.nextTrack();


    }



    public  void playPrevoiusTrack(){
        mPlayertask.PrevousTrack();
    }


    public void setNotification(Context context){

        /* 1. Setup Notification Builder */
        Notification.Builder builder = new Notification.Builder(context);

/* 2. Configure Notification Alarm */
        builder.setSmallIcon(R.drawable.ic_music_video_white_36dp)
                .setWhen(System.currentTimeMillis()).setTicker("music");

/* 3. Configure Drop-down Action */


        builder.setContentTitle(getResources().getString(R.string.mp3_status))
                .setContentText(getcurrentTrack().getName() ).setContentInfo("Click!");

        Intent intent = new Intent(context,Mp3Player.class);
        PendingIntent notifIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(notifIntent);
/* 4. Create Notification and use Manager to launch it */

      mNotification = builder.build();
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(NOTIFICATION_ID, mNotification);
        startForeground(NOTIFICATION_ID,mNotification);
         // ID is my integer ID
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
       playNextTrack();
    }

    public void pause() {
        Log.i(TAG, "Service Paused");
        mPlayertask.pause();



    }


    public Track getcurrentTrack() {
        if(mPlayertask != null){
      return  mPlayertask.getCurrentTrack();}
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }



    public void resume() {
        if(mState== State.PAUSED){
            mPlayertask.resume();
            Log.d(TAG, "resume:  " + mState.toString());
        }
    }



    public class Mp3Binder extends Binder {
        Mp3PlayerService getService() {
            return Mp3PlayerService.this;
        }
    }


    // background asynktask to handle the music play
    public class Mp3Playertask extends AsyncTask<Void,Void,Void>{
        private MediaPlayer mMediaPlayer;
         private  ArrayList<Track> mTracklist;
         private  int mPostion;
         Track currentTrack;



        public Mp3Playertask( ArrayList<Track> tracklist, int postion) {
            mTracklist = tracklist;
            currentTrack = mTracklist.get(postion);
            mMediaPlayer = new MediaPlayer();
        }


        public  void  play(Track track ) {

            try {
                if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();

            }
                mMediaPlayer.reset();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // sream type music
                mMediaPlayer.setOnPreparedListener(Mp3PlayerService.this);
                mMediaPlayer.setOnCompletionListener(Mp3PlayerService.this);
                mMediaPlayer.setDataSource(Mp3PlayerService.this, Uri.parse(track.getPath()));

                mMediaPlayer.prepare();
                mMediaPlayer.start();
                currentTrack = track;
                setCurrentTrack(track);
                mState = State.PLAYING;
               setNotification(getApplicationContext());

            } catch (IOException e) {
               Log.d(TAG,"Problem occured in with prepare");
            }
        }

        public void nextTrack(){
            if(mPostion== mTracklist.size()-1) {   // if it is last track on the tracklist starts form the begning
                mPostion = 0;
                play(mTracklist.get(mPostion));
            }else  {
                 mPostion++;
                play(mTracklist.get(mPostion));
            }

        }

        public void PrevousTrack() {
            if(mPostion==0){   // if it is last track on the tracklist starts form the end
            mPostion = mTracklist.size()-1;
            play(mTracklist.get(mPostion));
            } else
                mPostion--;
              play(mTracklist.get(mPostion));

        }


        @Override
        protected Void doInBackground(Void... voids) {
            play(currentTrack);
            Log.d(TAG, "doInBackground: "+mState.toString());
            return null;
        }

        public void stopPlay(){
           if(mMediaPlayer.isPlaying()) {
               mMediaPlayer.stop();
           }
            mMediaPlayer.release();
            mMediaPlayer=null;
            stopNotificaton();
           mState = State.STOPPED;
        }

        public  void pause(){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
                Log.i(TAG, "media Player is Paused");
                mState= State.PAUSED;
            }

        }

        public  void resume() {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                mState = State.PLAYING;
            }
        }
            public Track getCurrentTrack() {
                return currentTrack;
            }

            public void setCurrentTrack(Track currentTrack) {
                this.currentTrack = currentTrack;
            }


        }

    private void stopNotificaton() {
       stopForeground(true);
    }




}






















