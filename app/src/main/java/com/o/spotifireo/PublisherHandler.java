package com.o.spotifireo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PublisherHandler extends Node implements Runnable{
    Publisher publisher;
    ServerSocket socket;
    InetAddress ip;
    int port;
    ServerSocket providerSocket;
    Socket Connection;
    Socket requestSocket;
    ObjectOutputStream out ;
    ObjectInputStream in ;
    Message message;

    public PublisherHandler(Publisher publisher) {
        this.publisher = publisher;
        this.port = publisher.port;
        try {
            ip = InetAddress.getByName("192.168.1.9");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        connectAndNotifyBrokers();
        openServer();

    }

    void openServer() {
        try {
            providerSocket = new ServerSocket(publisher.port, 10,this.ip);

        } catch (IOException e) {
            e.printStackTrace();
        }
        int i=1;
        while (true) {
            try {
                Connection = providerSocket.accept();
                in=new ObjectInputStream(Connection.getInputStream());
                message=(Message) in.readObject();
                if(message!=null) {
                    System.out.println(message.toString());
                    System.out.println("com.o.spotifire.Message rcv'd");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }catch (NullPointerException e){
                System.err.println("com.o.spotifire.Message Not Found" +i);
                i++;
            }
           PublisherServerHandler psh= new PublisherServerHandler(this);

            psh.push();


        }
    }

    public static void main(String[] args) {
        Publisher p = new Publisher("dataset4", 1234,"192.168.1.9");
        new Thread(new PublisherHandler(p)).start();
        Publisher p2 = new Publisher("dataset3", 1235,"192.168.1.9");
        new Thread(new PublisherHandler(p2)).start();
    }


    void connectAndNotifyBrokers() {



            int port=7654;
            int port2=8760;
            int port3=9876;
            List<Integer> ports= new ArrayList<Integer>();
            ports.add(port);
            ports.add(port2);
            ports.add(port3);





            for(Integer thePort:ports){



            try {
                requestSocket = new Socket("192.168.1.9", thePort);
                out = new ObjectOutputStream(requestSocket.getOutputStream());

                System.out.println("Connection Established!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                out.writeObject(publisher.getMsg());
            } catch (IOException e) {
                e.printStackTrace();
            }
            }





    }

    public Socket getConnection() {
        return Connection;
    }

    public PublisherHandler(PublisherHandler p){
        this.publisher=p.publisher;
        this.port=p.port;
        this.message=p.message;
    }

}
