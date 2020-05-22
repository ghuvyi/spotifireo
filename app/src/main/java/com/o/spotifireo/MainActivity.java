package com.o.spotifireo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private int STORAGE_PERMISSION_CODE=1;

    MediaPlayer player;
    MediaPlayer player2;
    DownloadManager downloadManager;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button download=(Button)findViewById(R.id.download);
        final EditText artisttxt=(EditText)findViewById(R.id.Artist);
        final EditText songtxt=(EditText)findViewById(R.id.Song);
        Button play=(Button)findViewById(R.id.play);
        Button stop=(Button)findViewById(R.id.stop);
        context=this;
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        SeekBar bar=(SeekBar)findViewById(R.id.seekBar);

        player=new MediaPlayer();
        player2=new MediaPlayer();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

                }else{
                    requestStoragePermision();
                }
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

                }else{
                    requestStoragePermision2();
                }

                String artist=artisttxt.getText().toString();
                String song=songtxt.getText().toString();
                song=song+".mp3";
                String type="Download";
                ConsumerHandler a=new ConsumerHandler(context);
                a.execute(artist,song,type,context);

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()){
                    player.stop();
                    player.reset();
                }
                if(player2.isPlaying()){
                    player2.stop();
                    player2.reset();
                }
            }
        });




        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String artist=artisttxt.getText().toString();
                String song=songtxt.getText().toString();
                song=song+".mp3";
                String type="Play";





                    player.stop();
                    player.reset();


                    player2.stop();
                    player2.reset();

                ConsumerHandler a=new ConsumerHandler(context);
                a.execute(artist,song,type,player,player2,downloadManager);




            }
        });
    }
    private void requestStoragePermision(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }

    }
    private void requestStoragePermision2(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }
        }
    }
}
