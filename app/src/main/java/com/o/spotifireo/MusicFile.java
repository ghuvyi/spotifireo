package com.o.spotifireo;

import java.io.*;
import java.util.Arrays;

public class MusicFile implements Serializable {

    private String trackNAme;
    private String artist;
    private String albumInfo;
    private String genre;
    private byte[] data;
    File output;

    public MusicFile(String trackNAme, String artist, String albumInfo, String genre, byte[] musicFile) {
        this.trackNAme = trackNAme;
        this.artist = artist;
        this.albumInfo = albumInfo;
        this.genre = genre;
        this.data = musicFile;
    }

    public MusicFile() {
    }

    public String getTrackNAme() {
        return trackNAme;
    }

    public void setTrackNAme(String trackNAme) {
        this.trackNAme = trackNAme;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] musicFile) {
        this.data = musicFile;
    }

    public void stitchByteArray(byte[] stitch){
        byte[] finalArray=new byte[this.data.length+stitch.length];
        System.arraycopy(this.data, 0, finalArray, 0, this.data.length);
        System.arraycopy(stitch, 0, finalArray, this.data.length, stitch.length);


        this.data=finalArray;

    }

    public File makeFile(){
        File tempmp3;


        try {

            tempmp3=File.createTempFile(trackNAme,".mp3");
            FileOutputStream fos= new FileOutputStream(tempmp3);
            fos.write(this.data);
            fos.close();
            output=tempmp3;


        } catch (IOException e) {
            e.printStackTrace();
        }


        return output;
    }

    public File saveFileLocally(){
        File a=new File("//sdcard");

        try{

            OutputStream bos=new BufferedOutputStream(new FileOutputStream( a=new File("//sdcard//"+trackNAme)));
            for(int i=0;i<this.data.length;i++){
                bos.write(this.data[i]);
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;

    }
}
