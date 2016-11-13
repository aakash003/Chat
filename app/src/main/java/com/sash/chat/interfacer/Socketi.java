package com.sash.chat.interfacer;

/**
 * Created by Aakash on 21-06-2016.
 */
public interface Socketi {
    public String sendHttpRequest(String Params);
    public int startListeningPort(int Port);
    public void stopListening();
    public void exit();
    public int getListeningPort();
}
