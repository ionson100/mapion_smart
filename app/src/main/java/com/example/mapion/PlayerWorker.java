package com.example.mapion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mapion.dialogs.DialogFactory;
import com.example.mapion.utils.Utils;

public class PlayerWorker extends BroadcastReceiver {

    ImageButton btClose;
    ImageButton btStartStop;
    private MainActivity mainActivity;
    View host;
    View mViewPlayerCore;
    View mViewSpinner;
    View mViewError;
    View textError;
    View media_text_description;
    View duration_text;
    private SeekBar seekBar;


    public PlayerWorker(DrawerLayout root, MainActivity mainActivity){


        btClose =root.findViewById(R.id.media_close_dialog);
        btStartStop = root.findViewById(R.id.media_start_dialog);
        mViewPlayerCore =root.findViewById(R.id.media_constrains);
        mViewSpinner =root.findViewById(R.id.media_spinner);
        mViewError = root.findViewById(R.id.media_error);
        textError=root.findViewById(R.id.media_error);
        duration_text=root.findViewById(R.id.duration_text);
        media_text_description=root.findViewById(R.id.media_text_description);
        seekBar = root.findViewById(R.id.seekbar);
        this.mainActivity = mainActivity;
        if(btClose !=null){
            host=root.findViewById(R.id.player_host);
            host.setVisibility(View.GONE);
            btClose.setOnClickListener(v -> {
                host.setVisibility(View.GONE);
                Intent intent = new Intent(Utils.BR_SERVICE);
                intent.putExtra("type",3); // закрыть плейер
                mainActivity.sendBroadcast(intent);
            });
        }
        this.mainActivity.startService(new Intent(this.mainActivity.getBaseContext(),MyServicePayer.class));
        this.mainActivity.registerReceiver(this,new IntentFilter(Utils.BR_HOST));
        btStartStop.setOnClickListener(v -> {
            Intent intent = new Intent(Utils.BR_SERVICE);
            intent.putExtra("type",2); // нажатие кнопки плейера
            mainActivity.sendBroadcast(intent);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                int x = (int) Math.ceil(progress / 1000f);
                if( fromTouch){
                    Intent intent = new Intent(Utils.BR_SERVICE);
                    intent.putExtra("type",400); // перемещение ползунка
                    intent.putExtra("data",x);
                    mainActivity.sendBroadcast(intent);
                    //mediaPlayer.seekTo(x);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Intent intent = new Intent(Utils.BR_SERVICE);
                intent.putExtra("type",401); // перемещение ползунка
                intent.putExtra("data",seekBar.getProgress());
                mainActivity.sendBroadcast(intent);
            }
        });




    }
    public void unregister(){
        this.mainActivity.unregisterReceiver(this);
        Intent intent = new Intent(Utils.BR_SERVICE);
        intent.putExtra("type",1000); // selfClose
        mainActivity.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int t= intent.getIntExtra("type",-1);
        switch (t){
            case 1:{ // открыть плейер
                host.setVisibility(View.VISIBLE);
                seekBar.setProgress(0);
                break;
            }
            case 2:{ // начала воспроизведения
                String name=intent.getStringExtra("name");
                ((TextView)media_text_description).setText(name);

                mViewPlayerCore.setVisibility(View.VISIBLE);
                mViewSpinner.setVisibility(View.GONE);
                mViewError.setVisibility(View.GONE);
                break;
            }
            case 101:{ // измененеие картинки кнопки
                int img= intent.getIntExtra("data",-1);
                if(img!=-1){
                    btStartStop.setImageDrawable(ContextCompat.getDrawable(mainActivity, img));
                }

                break;
            }
            case 102:{ // error
                mViewPlayerCore.setVisibility(View.GONE);
                mViewSpinner.setVisibility(View.GONE);
                mViewError.setVisibility(View.VISIBLE);
                String error=intent.getStringExtra("data");
                DialogFactory.dialogInfo(mainActivity,"Error Palyer",error,null);
                break;
            }
            case 201:{
                int max=intent.getIntExtra("data",-1);
                String durat=intent.getStringExtra("data2");
                ((TextView)duration_text).setText(durat);
                if(max!=-1){
                    seekBar.setMax(max);
                }

                break;
            }
            case 202:{
                int position =intent.getIntExtra("data",-1);
                if(position !=-1){
                    seekBar.setProgress(position);
                }
                break;
            }
        }
    }

}
