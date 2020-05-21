package com.o.spotifireo;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class Broker extends Node implements Runnable {
    private static List<Publisher> registeredpublishers = new ArrayList<Publisher>();
    private static List<Consumer> registeredConsumers = new ArrayList<Consumer>();
    public static List<Consumer> GetConsumers(){
        return registeredConsumers;
    }
    public static List<Publisher> GetPublishers(){
        return registeredpublishers;
    }
    private boolean transfer=true;
    public String Name;
    public Integer port;
    public Broker(Integer port,String name,String ip){
        this.port=port;
        this.Name=name;
        try {
            this.ip=InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    ServerSocket providerSocket;
    Socket connection = null;
    InetAddress ip;

    BigInteger myKeys;

    public void run(){
        calculateKeys();
        Node.getBrokers().add(this);
        Node.setMinMax();

            openServer();


    }
    void calculateKeys(){
        String g =ip+ (port != null ? port.toString() : null);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(g.getBytes());
        byte[] digest = m.digest();
        myKeys = new BigInteger(1,digest);
        BigInteger a=new BigInteger("35");
        myKeys=myKeys.mod(a);
        System.out.println(Name+" Server Hashkey is "+myKeys);

    }
    void openServer()throws NullPointerException {
        try {
            providerSocket = new ServerSocket(this.port, 10,this.ip);

            while (true) {
                acceptConnection();
                new BrokerHandler(this).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

     void  acceptConnection()throws NullPointerException {

         try {
             connection = providerSocket.accept();


         } catch (IOException e) {
             e.printStackTrace();
         }
         System.out.println("client connected.");
         {


         }
     }
     Socket getConnection(){
     return this.connection;
     }


    public static void main(String args[]) {

        new Thread(new Broker(7654,"First","192.168.1.9")).start();
        new Thread(new Broker(8760,"Second","192.168.1.9")).start();
        new Thread(new Broker(9876,"Third","192.168.1.9")).start();


    }





































}