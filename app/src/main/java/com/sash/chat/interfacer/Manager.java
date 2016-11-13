package com.sash.chat.interfacer;
import java.io.UnsupportedEncodingException;

/**
 * Created by Aakash on 21-06-2016.
 */
public interface Manager {

    public String getUsername();
    public String sendMessage(String username,String toUsername,String message) throws UnsupportedEncodingException;
    public String authenticateUser(String usernameText,String passwordText) throws UnsupportedEncodingException;
    public void messageRecieved(String username,String message);
    public boolean isNetworkConnected();
    public boolean isUserAuthenticated();
    public String getLastRawFriendList();
    public void exit();
    public String signUpUser( String usernameText, String PasswordText, String email);
    public String addNewFriendRequest(String friendUsername);
    public String sendFriendsReqsResponse(String approvedFriendsNames,String discardFriendUsers);
}
