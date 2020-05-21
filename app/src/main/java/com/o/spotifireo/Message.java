package com.o.spotifireo;

import java.io.Serializable;

public class Message implements Serializable {
    private boolean transfer;
    String artist;
    Object entity;
    Boolean check = true;
    int port;
    String song;
    MusicFile extract;
    private byte[] byteChunk;
    String songPart;

    public Message( MusicFile extract) {
        this.extract = extract;
        this.byteChunk=new byte[0];
        this.transfer=true;
    }

    public Message(Object entity) {
        this.entity = entity;

        this.byteChunk=new byte[0];
        this.transfer=true;
    }

    public Message(String artist, String song) {
        this.artist = artist;
        this.byteChunk=new byte[0];
        this.song = song;
        this.transfer=true;
    }

    public Message(String song) {
        this.song = song;
        this.byteChunk=new byte[0];
        this.transfer=true;
    }

    public Message(String artist, Object entity, String song) {
        this.artist = artist;
        this.entity = entity;
        this.byteChunk=new byte[0];
        this.song = song;
        this.transfer=true;
    }

    public Message(String artist, int port, boolean check) {
        this.artist = artist;
        this.port = port;
        this.check = check;
        this.transfer=true;
    }

    public Message(byte[] chunk){
        this.byteChunk=chunk;
        this.transfer=true;
    }


    @Override
    public String toString() {
        return "com.o.spotifire.Message{" +
                "transfer=" + transfer +
                ", artist='" + artist + '\'' +
                ", entity=" + entity +
                ", check=" + check +
                ", port=" + port +
                ", song='" + song + '\'' +
                ", extract=" + extract +
                ", byteChunk=" + byteChunk.length +
                ", songPart='" + songPart + '\'' +
                '}';
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    public boolean getTransfer() {
        return transfer;
    }

    public byte[] getByteChunk() {
        return byteChunk;
    }
}
