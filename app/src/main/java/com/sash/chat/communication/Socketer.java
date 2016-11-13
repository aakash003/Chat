package com.sash.chat.communication;

import android.util.Log;

import com.sash.chat.interfacer.Socketi;
import com.sash.chat.serve.MessagingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by Aakash on 06-07-2016.
 */
public class Socketer implements Socketi {

    private static final String AUTHENTICATION_SERVER_ADDRESS = "http://10.51.1.238/ac/index.php";

    private int listeningPort = 0;

    private static final String HTTP_REQUEST_FAILED = null;

    private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();

    private ServerSocket serverSocket = null;                                                               //hold on port no.

    private boolean listening;

    public Socketer(MessagingService messagingService) {
    }


    private class RecieveConnetion extends Thread {

        Socket clientSocket = null;

        public RecieveConnetion(Socket socket) {
            this.clientSocket = socket;
            Socketer.this.sockets.put(socket.getInetAddress(), socket);
        }


        @Override
        public void run() {
            try {
                BufferedReader in;
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equals("exit") == false) {

                    } else {
                        clientSocket.shutdownInput();
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                        Socketer.this.sockets.remove(( clientSocket).getInetAddress());
                    }
                }
            } catch (IOException e) {
                Log.d("RecieveConnection.run:rconnection", "");
            }
        }
    }

    @Override
    public String sendHttpRequest(String Params) {
        URL url;
        String result = new String();
        try {
            url = new URL(AUTHENTICATION_SERVER_ADDRESS);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);   //url connection for output setting true
            connection.setChunkedStreamingMode(0);

            PrintWriter out = new PrintWriter(connection.getOutputStream());

            out.println(Params);
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //Log.d("result","value" + Params);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {

                result = result.concat(inputLine);

                Log.d("result","value" + result);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result.length() == 0) {

            result = HTTP_REQUEST_FAILED;
        }
        return result;
    }


    @Override
    public int startListeningPort(int portNo) {
        listening = true;
        try {
            serverSocket = new ServerSocket(portNo);
            this.listeningPort = portNo;
        } catch (IOException e) {
           return 0;
        }
        while (listening) {
            try {
                new RecieveConnetion(serverSocket.accept()).start();
            } catch (IOException e) {
                return 2;
            }
        }

        try{
            serverSocket.close();
        } catch (IOException e) {
            Log.d(" Exception server socket","Exception when closing server sockcet");
            return 3;
        }
        return 1;
    }

    @Override
    public void stopListening() {
        this.listening = false;
    }



    @Override
    public void exit() {
        Iterator<Socket> iterator;
        for(iterator = (Iterator<Socket>) sockets.values(); iterator.hasNext();)
        {
            java.net.Socket socket = (java.net.Socket) iterator.next();
            try{
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }
            catch (Exception e){

            }
        }
        sockets.clear();
        this.stopListening();
    }

    @Override
    public int getListeningPort() {
        return this.listeningPort;
    }
}
