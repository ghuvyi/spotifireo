package com.o.spotifireo;

import android.os.AsyncTask;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConsumerAsync extends AsyncTask<Void,Void,Void> {
    Message answer;
    Boolean check;
    Consumer consumer = new Consumer("Rafael Krux", "After the End.mp3");

    @Override
    protected Void doInBackground(Void... voids) {

        return null;
    }


    void connect(int port){
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
                new ConsumerAsync().execute();
                System.out.println("Server Changed");
                this.cancel(true);


            } else {
                System.out.println(answer.song);
                MusicFile tempMusicFile=new MusicFile();
                int i=0;
                while (true){
                    try {
                        Message message = (Message) in.readObject();
                        if(message.getTransfer()==true){
                            tempMusicFile.setData(message.getByteChunk());
                            tempMusicFile.setTrackNAme(this.consumer.song+" "+i);
                            tempMusicFile.saveFileLocally();
                            i++;
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
