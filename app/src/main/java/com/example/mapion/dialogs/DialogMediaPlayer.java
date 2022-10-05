package com.example.mapion.dialogs;

import static android.content.Context.AUDIO_SERVICE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.mapion.IAction;
import com.example.mapion.R;

import java.io.IOException;

public class DialogMediaPlayer extends BaseDialog   {
    public static final String TAG = "a15c2b78-92dc-4a78-9425-645259868d10f";
    public IAction<Object> iAction;
    public String title="Media Player";
    public String message;
    public String fileName;
    public Activity mActivity;

    MediaPlayer mediaPlayer = new MediaPlayer();
    SeekBar seekBar;
    boolean wasPlaying = false;
    ImageButton fab;
    String DATA_SD;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_media_payer, null);

        v.findViewById(R.id.media_close_dialog).setOnClickListener(v1 -> {
            dismiss();
            if(iAction!=null){
                iAction.Action(null);
            }
        });

        builder.setView(v);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);


        fab = v.findViewById(R.id.media_start_dialog);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong();
            }
        });
        seekBar = v.findViewById(R.id.seekbar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                //seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if(mediaPlayer != null && fromTouch){
                    mediaPlayer.seekTo(x);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
        //////////////
         DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+fileName;
         mediaPlayer = MediaPlayer.create(mActivity, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+fileName));
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        new DownloadFilesTask().execute();


        return dialog;
    }

    public void playSong() {

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            fab.setImageDrawable(ContextCompat.getDrawable(mActivity, android.R.drawable.ic_media_play));
        }else{
            mediaPlayer.start();
            new DownloadFilesTask().execute();
            fab.setImageDrawable(ContextCompat.getDrawable(mActivity, android.R.drawable.ic_media_pause));
        }

    }









    @Override
    public void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();

    }



    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int total = mediaPlayer.getDuration();


            while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
                try {
                    Thread.sleep(1000);
                    currentPosition = mediaPlayer.getCurrentPosition();
                } catch (InterruptedException e) {
                    return null;
                } catch (Exception e) {
                    return null;
                }

                seekBar.setProgress(currentPosition);

            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }


    }

}
