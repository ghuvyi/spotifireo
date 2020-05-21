package com.o.spotifireo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;



public class Publisher implements Serializable{
    private ArrayList<String> artists;
    private String dir;
    private Message msg;
    public int port;
    InetAddress ip;




    public Publisher(String filepath, int port, String ip) {
        this.dir=filepath;
        try {
            artists = getListofArtist(filepath);
        }catch (FileNotFoundException e){
            System.err.println(filepath);
            e.printStackTrace();
        }
        System.out.println("\nArtists Loaded");
        this.port=port;
        this.msg=new Message(this);
        System.out.println(Node.getBrokers().size());
        try {
            this.ip=InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }










    public ArrayList<String> getListofArtist(String filepath) throws FileNotFoundException{
        ArrayList<String> tempList = new ArrayList<>();
        File dir=new File(filepath);
        System.out.println("Loading Artists");
        int i=0;
        for (File temp: dir.listFiles()){

            printProgressBar(++i);
            GlobalFunctions gf=new GlobalFunctions();
            String artist=gf.getMp3Metadata(temp).get("xmpDM:artist");
            if(!tempList.contains(artist) && artist!=null)tempList.add(artist);
        }

        return tempList;
    }

    public  String getDir() {
        return dir;
    }

    public void printProgressBar(int i) {
        if (i % 5 == 0) System.out.print("#");
    }

    public ArrayList<String> getArtists() {
        return artists;
    }

    public Message getMsg() {
        return msg;
    }
}
