package com.o.spotifireo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BrokerHandler extends Thread implements Serializable {


    ObjectInputStream in;
    ObjectOutputStream out;
    String artistName;
    BigInteger theirKeys;
    Broker Mybroker;
    Object e;
    Message request;
    Socket Stopcon=null;
    String Song;
    boolean changeCheck=false;
    boolean CorrectBroker=false;

    public BrokerHandler(Broker broker) throws NullPointerException{
        Stopcon=broker.getConnection();
        try {

            in = new ObjectInputStream(Stopcon.getInputStream());
            out =new ObjectOutputStream(Stopcon.getOutputStream());
            out.flush();
            try {
                this.request=(Message)in.readObject();
                this.artistName=request.artist;
                this.e =request.entity;
                this.Mybroker=broker;
                this.Song=request.song;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        if(this.e instanceof Consumer){
            calculateMessageKeys(this.request);
            checkBroker(this.Mybroker,(Consumer) e);
            if(!changeCheck) {
                pull(this.request);
            }

        }
        else if(this.e instanceof Publisher){
            checkPublisher((Publisher) e);
            System.out.println("This is a publisher");

        }


    }
    public synchronized void pull(Message request){
        Publisher correctPublisher = null;
        Consumer tempConsumer=(Consumer)e;
        if (Mybroker.GetPublishers().size()!=0) {
            boolean flag=false;
            for (Publisher publisher : Mybroker.GetPublishers()) {
                if (publisher.getArtists().contains(tempConsumer.artist)) {
                    correctPublisher = publisher;
                    flag=true;

                    Socket requestSocket = null;
                    ObjectOutputStream publisherOut = null;
                    ObjectInputStream publisherIn = null;

                    try {
                        requestSocket = new Socket("192.168.1.9", correctPublisher.port);
                        publisherOut = new ObjectOutputStream(requestSocket.getOutputStream());

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    Message songRequest=new Message(request.artist,request.song);
                    try {
                        System.out.println("Fetching song");
                        publisherOut.writeObject(songRequest);
                        publisherIn = new ObjectInputStream(requestSocket.getInputStream());
                        while(true) {
                            Message msg = (Message) publisherIn.readObject();

                            System.out.println("Pushing\n"+msg.toString()+"\nto"+tempConsumer.port+"\n");
                            if(msg.getTransfer()==false){
                                if(msg.song.equals("File not found")){
                                    System.err.println("Song does not exist");

                                }else if(msg.song.equals("Song not found")){
                                    System.err.println("Song not found");
                                }
                                else {
                                    System.out.println("Song Received");
                                    System.out.println("Whole Song Transferred!");
                                }
                                out.writeObject(msg);
                                break;
                            }

                            out.writeObject(msg);




                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    break;



                }


            }
            if(!flag){
                Message message=new Message("artist not found");
                message.setTransfer(false);
                try {
                    out.writeObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }



    public  void disconnectClient(Socket connection){
        changeCheck=true;
        try {
            in.close();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void checkPublisher(Publisher publisher) {
        boolean add = false;
        if (Node.getBrokers().get(0) == this.Mybroker) {
            for (String artist : publisher.getArtists()){
                System.out.println(artist+calculateArtistKeys(artist));
                if(calculateArtistKeys(artist)<=Mybroker.myKeys.intValue()){
                    if(!Mybroker.GetPublishers().contains(publisher)){
                        Mybroker.GetPublishers().add(publisher);
                        System.out.println(Mybroker.Name+" added a publisher");
                    }

                }
            }
        }else if(Node.getBrokers().get(1)==this.Mybroker){
            for (String artist:publisher.getArtists()){
                if(calculateArtistKeys(artist)<=Mybroker.myKeys.intValue()&&calculateArtistKeys(artist)>Node.getBrokers().get(0).myKeys.intValue()){
                   if(!Mybroker.GetPublishers().contains(publisher)){
                       Mybroker.GetPublishers().add(publisher);
                       System.out.println(Mybroker.Name+" added a publisher");
                   }

                }
            }
        }else{
            for (String artist:publisher.getArtists()){
                if(calculateArtistKeys(artist)<=Mybroker.myKeys.intValue()&&calculateArtistKeys(artist)>Node.getBrokers().get(1).myKeys.intValue()){
                    if(!Mybroker.GetPublishers().contains(publisher)){
                        Mybroker.GetPublishers().add(publisher);
                        System.out.println(Mybroker.Name+" added a publisher");
                    }

                }

            }
        }
    }

    public void checkBroker(Broker broker,Consumer consumer){
        int thePort=0;

        int theirIntKeys=theirKeys.intValue();
        System.out.println(theirIntKeys+"theirkeys");
        if(theirIntKeys>Node.MAX.intValue()){
            theirIntKeys=theirIntKeys%Node.MIN.intValue();
        }
        for(Broker broker1:Node.getBrokers()){
            if(theirIntKeys<=broker1.myKeys.intValue()){
                if(broker1==Mybroker){
                    CorrectBroker=true;
                    System.out.println("correct broker found port is"+broker.port);
                }
                thePort = broker1.port;
                break;

            }

        }
        if(!CorrectBroker){
            Message answer=new Message(this.artistName,thePort,false);
            try {
                out.writeObject(answer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(broker.Name + "Client changing server");
            disconnectClient(Stopcon);
        }else{
            consumer.Register(broker, artistName);
            System.out.println(broker.Name + "Client Connected and Registered");
            Message answer=(new Message("Searching song"));
            try {
                out.writeObject(answer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }



    }




    public int calculateArtistKeys(String Artist){
        int artistKeys;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(Artist.getBytes());
        byte[] digest = m.digest();
         BigInteger Keys = new BigInteger(1,digest);
        BigInteger mod=new BigInteger("35");
        Keys=Keys.mod(mod);
        artistKeys=Keys.intValue();
        if(artistKeys>Node.MAX.intValue()){
            artistKeys=artistKeys%Node.MAX.intValue();
        }
        return artistKeys;

    }
    public  void calculateMessageKeys(Message request)  {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.reset();
        m.update(artistName.getBytes());
        byte[] digest = m.digest();
        theirKeys = new BigInteger(1,digest);
        BigInteger mod=new BigInteger("35");
        theirKeys=theirKeys.mod(mod);
        System.out.println(theirKeys);
    }
}
