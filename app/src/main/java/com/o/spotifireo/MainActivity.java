package com.o.spotifireo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    MediaPlayer player2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button download=(Button)findViewById(R.id.download);
        final EditText artisttxt=(EditText)findViewById(R.id.Artist);
        final EditText songtxt=(EditText)findViewById(R.id.Song);
        Button play=(Button)findViewById(R.id.play);
        Button stop=(Button)findViewById(R.id.stop);

        player=new MediaPlayer();
        player2=new MediaPlayer();

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

                ConsumerHandler a=new ConsumerHandler();
                a.execute(artist,song,type,player,player2);




            }
        });
    }
}
