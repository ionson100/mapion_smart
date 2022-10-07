package com.example.mapion.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.mapion.IAction;
import com.example.mapion.R;
import com.example.mapion.models.MMediaContent;

import java.io.IOException;

public class DialogMediaPlayer extends BaseDialog   {
    public static final String TAG = "a15c2b78-92dc-4a78-9425-645259868d10f";
    public IAction<Object> iAction;
    public String title="Media Player";
    public MMediaContent mMediaContent;

    public Activity mActivity;

    private MediaPlayer mediaPlayer ;
    private SeekBar seekBar;
    private boolean isCommitPlay;

    ImageButton btStartStop;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayoutSpinner;
    LinearLayout linearLayoutError;
    TextView textViewError;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_media_payer, null);

        TextView textViewDes=v.findViewById(R.id.media_text_description);
        constraintLayout=v.findViewById(R.id.media_constrains);
        linearLayoutSpinner=v.findViewById(R.id.media_spinner);
        linearLayoutError=v.findViewById(R.id.media_error);
        textViewError=v.findViewById(R.id.media_text_error);
        textViewDes.setText(mMediaContent.message);
        v.findViewById(R.id.media_close_dialog).setOnClickListener(v1 -> {
            dismiss();
            if(iAction!=null){
                iAction.Action(null);
            }
        });

        builder.setView(v);
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);


        btStartStop = v.findViewById(R.id.media_start_dialog);

        btStartStop.setOnClickListener(view -> playSong());
        seekBar = v.findViewById(R.id.seekbar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
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
        createAndStartMedia();
        return dialog;
    }

    public void playSong() {

        if(isCommitPlay==true){
            createAndStartMedia();
        }else{
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                btStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, android.R.drawable.ic_media_play));
            }else{
                mediaPlayer.start();
                new TaskSeek().execute();
                btStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, android.R.drawable.ic_media_pause));
            }
        }


    }
    void createAndStartMedia(){
        if(mediaPlayer!=null){
            clearMediaPlayer();
            isCommitPlay=false;
        }
        btStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, android.R.drawable.ic_media_play));
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                constraintLayout.setVisibility(View.GONE);
                linearLayoutSpinner.setVisibility(View.GONE);
                linearLayoutError.setVisibility(View.VISIBLE);
                textViewError.setText("Error media file");
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(mp -> {
            linearLayoutSpinner.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.VISIBLE);
            isCommitPlay=false;
            seekBar.setMax(mp.getDuration());
            btStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, android.R.drawable.ic_media_pause));
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
            constraintLayout.setVisibility(View.GONE);
            linearLayoutSpinner.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
            textViewError.setText(e.getMessage());
           // DialogFactory.dialogInfo((AppCompatActivity) mActivity,"Error Media",e.getMessage(),null);
        }
        mediaPlayer.prepareAsync();



    }









    @Override
    public void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();

    }



    private void clearMediaPlayer() {
        isCommitPlay=true;
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btStartStop.setImageDrawable(ContextCompat.getDrawable(mActivity, android.R.drawable.ic_media_play));
        }

    }

    private class TaskSeek extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(mediaPlayer==null) return null;
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
