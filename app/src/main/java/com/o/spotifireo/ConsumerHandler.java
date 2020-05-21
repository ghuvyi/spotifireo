package com.o.spotifireo;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;

import org.apache.poi.hwpf.model.FIBFieldHandler;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ConsumerHandler extends AsyncTask<Object,Void,Void> {
    Message answer;
    Boolean check;
    Consumer consumer;
    String artist;
    String song;
    String type;
    MediaPlayer player;
    MediaPlayer player2;
    View view;
    ArrayList<File> file=new ArrayList<>();


    @Override
    protected Void doInBackground(Object... params) {
         artist=(String)params[0];
         song=(String)params[1];
         type=(String)params[2];
         player=(MediaPlayer) params[3];
         player2=(MediaPlayer)params[4];
         file.clear();



        consumer=new Consumer(artist,song);
        connect(consumer.port);
        return null;
    }


    void connect(int port){
        boolean flog=true;
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {

            requestSocket = new Socket("192.168.1.9", port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());

            System.out.println("com.o.spotifire.Message created.");
            out.writeObject(consumer.request);
            in = new ObjectInputStream(requestSocket.getInputStream());
            try {
                answer = (Message) in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            check = answer.check;

            if (check == false) {
                System.out.println("I change server");

                this.consumer.setPort(answer.port);
                new ConsumerHandler().execute(artist,song,type,player,player2);
                System.out.println("Server Changed");
                this.cancel(true);


            } else {
                System.out.println(answer.song);
                MusicFile tempMusicFile=new MusicFile();
                int i=0;
                File tempf;
                while (true){
                    try {
                        Message message = (Message) in.readObject();
                        if(message.getTransfer()==true) {
                            tempMusicFile.setData(message.getByteChunk());
                            tempMusicFile.setTrackNAme(this.consumer.song + " " + i);
                            tempMusicFile.saveFileLocally();
                            i++;

                            if (type=="Play") {
                                File temp = tempMusicFile.makeFile();
                                file.add(temp);
                                if(!player.isPlaying()&&flog) {
                                    flog=false;


                                    tempf = file.get(0);
                                    FileInputStream fis = new FileInputStream(tempf);
                                    player.setDataSource(fis.getFD());
                                    player.prepare();
                                    player.start();
                                }



                            }
                        }

                        if(message.getTransfer()==false){
                            if(message.song.equals("File not found")){
                                System.err.println("Song does not exist");
                                break;
                            }
                            else if(message.song.equals("artist not found")){
                                System.out.println("Artist not found");
                                break;

                            }
                            else if(message.song.equals("Song not found")){
                                System.out.println("Song not found");
                                break;

                            }
                            else {
                                System.out.println("Song Received");

                                break;
                            }

                        }

                    }catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }catch (EOFException e){
                        e.printStackTrace();
                    }
                }

               if(type=="Play"){
                    int k=1;
                    boolean playing=true;
                    boolean flag=false;
                    boolean lol=true;

                        /*tempf = file.get(k);
                        FileInputStream fis = new FileInputStream(tempf);
                        player2.setDataSource(fis.getFD());
                        player2.prepare();
                        player.setNextMediaPlayer(player2);
                        k++;*/
                         if(player.isPlaying()||player2.isPlaying()) {
                             while (playing && (player.isPlaying() || player2.isPlaying())) {
                                 if (player2.isPlaying() && flag) {
                                     tempf = file.get(k);
                                     FileInputStream fis = new FileInputStream(tempf);
                                     player.reset();
                                     player.setDataSource(fis.getFD());
                                     player.prepare();
                                     player2.setNextMediaPlayer(player);
                                     flag = false;
                                     k++;
                                 }
                                 if (player.isPlaying() && flag == false) {
                                     tempf = file.get(k);
                                     FileInputStream fis = new FileInputStream(tempf);
                                     player2.reset();
                                     player2.setDataSource(fis.getFD());
                                     player2.prepare();
                                     player.setNextMediaPlayer(player2);
                                     flag = true;
                                     k++;


                                 }
                                 if (k == file.size()) {
                                     playing = false;
                                 }


                             }
                         }












                }





            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {


                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }



    }
}
