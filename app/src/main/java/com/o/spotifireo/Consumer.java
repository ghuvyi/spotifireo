package com.o.spotifireo;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
public class Consumer extends Node implements Serializable  {

    String artist;
    Random r=new Random();
    List<Integer> givenList = Arrays.asList(7654, 8760, 9876);


    int port;
    Message request;
    String song;


    public Consumer(String artist,String song){
        this.song=song;
              this.artist=artist;
        Random rand = new Random();
                this.port=givenList.get(rand.nextInt(givenList.size()));
              request= new Message(artist,this.getConsumer(),song);
          }
     public Consumer(String artist,int port){
        this.artist=artist;
        this.port=port;
         request= new Message(artist,this.getConsumer(),song);
     }
          public Consumer getConsumer(){
        return this;
    }
          public void setPort(int port){
        this.port=port;
          }

         public void Register(Broker broker,String ArtistName){
             broker.GetConsumers().add(this);
             System.out.println(ArtistName);

         }

}
