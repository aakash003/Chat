package com.sash.chat.toolbox;

import android.util.Log;

import com.sash.chat.interfacer.Updater;
import com.sash.chat.typo.InfoOfFriends;
import com.sash.chat.typo.InfoOfMessage;
import com.sash.chat.typo.InfoStatus;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Vector;

/**
 * Created by Aakash on 26-06-2016.
 */
public class ParserXML extends DefaultHandler {

    public  String userKey = new String();
    public Updater updater;

    public ParserXML(Updater updater){
        super();
        this.updater=updater;
    }

    private Vector<InfoOfFriends> mFriends= new Vector<InfoOfFriends>();
    private Vector<InfoOfFriends> mOnlineFriends= new Vector<InfoOfFriends>();
    private Vector<InfoOfFriends> mUnapprovedFriends= new Vector<InfoOfFriends>();

    private Vector<Updater> mUnreadMessages= new Vector<Updater>();


    public void endDocument() throws SAXException {
        InfoOfFriends[] friends=new InfoOfFriends[mFriends.size()+mOnlineFriends.size()];
        InfoOfMessage[] message= new InfoOfMessage[mUnreadMessages.size()];

        int onlineFriendCount= mOnlineFriends.size();
        int offlineFriendCount= mFriends.size();


        for(int i=0;i<onlineFriendCount;i++){
            friends[i]=mOnlineFriends.get(i);
        }

        for(int i=0;i<offlineFriendCount;i++){
            friends[i+onlineFriendCount]=mFriends.get(i);
        }

        int unApprovedFriendCount=mUnapprovedFriends.size();
        InfoOfFriends[] unApprovedFriends= new InfoOfFriends[unApprovedFriendCount];

        for(int i=0;i< unApprovedFriends.length;i++){
            unApprovedFriends[i]=mUnapprovedFriends.get(i);
        }

        int unReadMessageCount=mUnreadMessages.size();
        for(int i=0;i<unReadMessageCount;i++){
            message[i]= (InfoOfMessage) mUnreadMessages.get(i);
            Log.i("Message LOG","i="+i);
        }
        this.updater.updateData((InfoOfMessage[]) message,friends,unApprovedFriends,userKey);
        try {
            super.endDocument();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


    public void startElement(String uri, String localName, String name, Attributes attributes)throws SAXException{

        if(localName=="friend"){
            InfoOfFriends friend= new InfoOfFriends();
            friend.Username=attributes.getValue(InfoOfFriends.Username);

            String status= attributes.getValue(InfoOfFriends.Status);

            friend.Port=attributes.getValue(InfoOfFriends.Port);


            if(status!=null&&status.equals("online")){
                friend.status=InfoStatus.ONLINE;
                mOnlineFriends.add(friend);
            }
            else if(status.equals("unApproved")){
                friend.status=InfoStatus.UNAPPROVED;
                mUnapprovedFriends.add(friend);
            }
            else{
                friend.status=InfoStatus.OFFLINE;
                mFriends.add(friend);
            }

        }
        else if(localName == "user"){
            this.userKey=attributes.getValue(InfoOfFriends.UserKey);
        }
        else if (localName == "message") {
            InfoOfMessage message = new InfoOfMessage();
            message.userid = attributes.getValue(InfoOfMessage.User_id);
            message.sendt = attributes.getValue(InfoOfMessage.SendT);
            message.messagetext = attributes.getValue(InfoOfMessage.MessageText);
            Log.i("MessageLOG", message.userid + message.sendt + message.messagetext);
            mUnreadMessages.add((Updater) message);
        }
        super.startElement(uri,localName,name,attributes);
    }
    @Override
    public void startDocument() throws SAXException{
        this.mFriends.clear();
        this.mOnlineFriends.clear();
        this.mUnreadMessages.clear();
        super.startDocument();
    }


}
