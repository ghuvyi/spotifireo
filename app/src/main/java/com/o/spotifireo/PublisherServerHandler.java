package com.o.spotifireo;

import org.apache.tika.metadata.Metadata;

import java.io.*;
import java.net.Socket;

public class PublisherServerHandler extends PublisherHandler{

    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    private static final int CHUNK_SIZE=512000;


    public void run() {
        System.out.println("Run");
        push();
    }

    public PublisherServerHandler(PublisherHandler publisherHandler){
        super(publisherHandler);
        connection=publisherHandler.getConnection();
        try {
            // in = new ObjectInputStream(connection.getInputStream());
            out =new ObjectOutputStream(connection.getOutputStream());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public  MusicFile importMusicFile(String songname,String dir) throws FileNotFoundException{
        MusicFile song=null;

        File songFile = new File(dir + "\\" + songname);
        GlobalFunctions gf = new GlobalFunctions();
        Metadata metadata = gf.getMp3Metadata(songFile);
        try {
            song = new MusicFile(
                    metadata.get("title"), metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), metadata.get("xmpDM:genre"), inputStreamToByteArray(new FileInputStream(songFile)));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return song;
    }

    public synchronized void push(){

        MusicFile song=null;
        Message tempmsg=null;
        try {
            song = importMusicFile(message.song, this.publisher.getDir());
            if (message.song.equals(song.getTrackNAme()+".mp3") && message.artist.equals(song.getArtist())) {
                try {
                    for (int i = 0; i <= song.getData().length / CHUNK_SIZE; i++) {

                        byte[] chunk = extractByteChunk(i, song.getData());

                        tempmsg = new Message(chunk);
                        out.writeObject(tempmsg);
                        Thread.sleep(1000);
                        System.out.println("CHUNK :" + tempmsg.toString() + " Sent");

                    }
                    tempmsg = new Message("END");
                    tempmsg.setTransfer(false);
                    out.writeObject(tempmsg);

                    System.out.println("\n\nSong Sent");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                System.err.println("Song not found!!!");
                tempmsg = new Message("Song not found");
                tempmsg.setTransfer(false);
                try {
                    out.writeObject(tempmsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            }catch(FileNotFoundException ex ){
                System.err.println("File Not Found!!!");
                tempmsg = new Message("File not found");
                tempmsg.setTransfer(false);
                try {
                    out.writeObject(tempmsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }







    }




    public static byte[] inputStreamToByteArray(InputStream inStream) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        try {
            while ((bytesRead = inStream.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public byte[] extractByteChunk(int i,byte[] song){
        byte [] chunk;
        if (i<song.length/CHUNK_SIZE){
            chunk=new byte[CHUNK_SIZE];
            for (int j=0;j<CHUNK_SIZE;j++){
                chunk[j]=song[(i*CHUNK_SIZE)+j];
            }
        }
        else{
            chunk=new byte[song.length-((i)*CHUNK_SIZE)];
            for (int j=0;j<song.length-((i)*CHUNK_SIZE);j++){
                chunk[j]=song[(i*CHUNK_SIZE)+j];
            }
        }

        return chunk;

    }





}
