package com.example.mapion;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.mapion.dialogs.DialogMediaPlayer;
import com.example.mapion.models.MMediaContent;
import com.example.mapion.utils.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MyServicePayer extends Service   {

    private static final String CHANNEL_DEFAULT_IMPORTANCE = "assa123";
    private static final int ONGOING_NOTIFICATION_ID = 12334;
    private boolean isStart=false;
    private boolean isCommitPlay;
    private MediaPlayer mediaPlayer ;
    private MMediaContent mMediaContent;
    private boolean isAnimation;
    private BroadcastReceiver receiverPlayer=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int t= intent.getIntExtra("type",-1);
            switch (t){
                case 1:{// простое ручное воспроизведение
                    String s=intent.getStringExtra("data");
                    if(s!=null){
                        isAnimation=false;
                        mMediaContent=new Gson().fromJson(s,MMediaContent.class);
                        startPlayer(mMediaContent);
                    }
                    break;
                }

                case 1001:{
                    String s=intent.getStringExtra("data");// воспроизведение с анимации
                    if(s!=null){
                        isAnimation=true;
                        mMediaContent=new Gson().fromJson(s,MMediaContent.class);
                        startPlayer(mMediaContent);
                    }
                    break;
                }

                case 2:{ // кнопка старт стоп нажата
                    playSong();
                    break;
                }
                case 3:{ // кнопка закрытия
                    mMediaContent=null;
                    clearMediaPlayer();
                    break;
                }
                case 400:{
                    int x=intent.getIntExtra("data",-1);
                    if(x!=-1){
                        if(mediaPlayer != null){
                            mediaPlayer.seekTo(x);
                        }
                    }
                    break;
                }
                case 401:{
                    int x=intent.getIntExtra("data",-1);
                    if(x!=-1){
                        if(mediaPlayer != null && mediaPlayer.isPlaying()){
                            mediaPlayer.seekTo(x);
                        }
                    }
                    break;
                }
                case 1000:{ // selfClose
                    clearMediaPlayer();
                    unregisterReceiver(receiverPlayer);
                    stopSelf();
                    break;
                }
            }


        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isStart==false){
            this.registerReceiver(receiverPlayer,new IntentFilter(Utils.BR_SERVICE));
            isStart=true;
            String NOTIFICATION_CHANNEL_ID = "com.example.audiomap.ionson100";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_baseline_map_24)
                    .setContentTitle("Map Sound travel")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    void playSong(){

        if(isCommitPlay==true){
            if(mMediaContent!=null){
                createAndStartMedia(mMediaContent);
            }

        }else{
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                Intent intent = new Intent(Utils.BR_HOST);
                intent.putExtra("type",101); // открыть плейер
                intent.putExtra("data",android.R.drawable.ic_media_play);
                sendBroadcast(intent);

            }else{
                mediaPlayer.start();
                new TaskSeek().execute();

                Intent intent = new Intent(Utils.BR_HOST);
                intent.putExtra("type",101); // открыть плейер
                intent.putExtra("data",android.R.drawable.ic_media_pause);
                sendBroadcast(intent);
            }
        }

    }
    void startPlayer(MMediaContent mMediaContent){

        Intent intent = new Intent(Utils.BR_HOST);
        intent.putExtra("type",1); // открыть плейер
        sendBroadcast(intent);
        createAndStartMedia(mMediaContent);

    }
    void createAndStartMedia(MMediaContent mMediaContent){
        boolean pr=isAnimation;
        if(mediaPlayer!=null){
            clearMediaPlayer();
            isCommitPlay=false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isAnimation=pr;
        Intent intent = new Intent(Utils.BR_HOST);
        intent.putExtra("type",101); // открыть плейер
        intent.putExtra("data",android.R.drawable.ic_media_play);
        sendBroadcast(intent);

        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Intent intent12 = new Intent(Utils.BR_HOST);
            intent12.putExtra("type",102); // error
            intent12.putExtra("data","Error player: what-"+what+" extra-"+extra);
            sendBroadcast(intent12);
            return false;
        });
        mediaPlayer.setOnPreparedListener(mp -> {
            Intent intent2 = new Intent(Utils.BR_HOST);
            intent2.putExtra("name",mMediaContent.message);
            intent2.putExtra("type",2); // начала воспроизведения
            sendBroadcast(intent2);

            Intent intent1 = new Intent(Utils.BR_HOST);
            intent1.putExtra("type",101); // измеенние иконки
            intent1.putExtra("data",android.R.drawable.ic_media_pause);
            sendBroadcast(intent1);

            isCommitPlay=false;

            Intent intent3 = new Intent(Utils.BR_HOST);
            intent3.putExtra("type",201); // seek bar set max
            intent3.putExtra("data",mp.getDuration());
            intent3.putExtra("data2",formatDuration(mp.getDuration()));
            sendBroadcast(intent3);
            //seekBar.setMax(mp.getDuration());

            mp.start();
             new TaskSeek().execute();
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            clearMediaPlayer();
        });
        mediaPlayer.setLooping(false);
        try {
            mediaPlayer.setDataSource(mMediaContent.url);
        } catch (IOException e) {

            Intent intent3 = new Intent(Utils.BR_HOST);
            intent3.putExtra("type",102); // error
            intent3.putExtra("data",e.getMessage());
            sendBroadcast(intent3);

        }
        mediaPlayer.prepareAsync();



    }
    private String formatDuration(long duration) {
        long minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        long seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);

        return String.format("%02d:%02d", minutes, seconds);
    }

    private void clearMediaPlayer() {

        isCommitPlay=true;
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            if(isAnimation==false){
                Intent intent1 = new Intent(Utils.BR_HOST);
                intent1.putExtra("type",101); // коне воспроизведегтя
                intent1.putExtra("data",android.R.drawable.ic_media_play);
                sendBroadcast(intent1);
            }else {
                Intent intent1 = new Intent(Utils.BR_HOST);
                intent1.putExtra("type",1011); // коне воспроизведегтя для анимации
                intent1.putExtra("data",android.R.drawable.ic_media_play);
                sendBroadcast(intent1);
            }


        }
        isAnimation=false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
        isStart=false;
    }

    private class TaskSeek extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(mediaPlayer==null||isCommitPlay==true) return null;
            int currentPosition = mediaPlayer.getCurrentPosition();
            int total = mediaPlayer.getDuration();
            while ( mediaPlayer.isPlaying() && currentPosition < total) {
                try {
                    Thread.sleep(500);
                    currentPosition = mediaPlayer.getCurrentPosition();
                } catch (InterruptedException e) {
                    return null;
                } catch (Exception e) {
                    return null;
                }
                Intent intent3 = new Intent(Utils.BR_HOST);
                intent3.putExtra("type",202); // seek bar set max
                intent3.putExtra("data",currentPosition);
                sendBroadcast(intent3);
                //seekBar.setProgress(currentPosition);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }


    }
}