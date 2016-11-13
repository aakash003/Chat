package com.sash.chat.toolbox;

import com.sash.chat.typo.InfoOfMessage;

/**
 * Created by Aakash on 26-06-2016.
 */
public class ControllerOfMessage {

    public static final String taken="taken";
    public static InfoOfMessage[] infoOfMessages = null;
    public static void setMessageInfo(InfoOfMessage[] infoOfMessages){
        InfoOfMessage.infoOfMessages = infoOfMessages;
    }

    public static InfoOfMessage checkMessage(String username){
        InfoOfMessage result = null;
        for(int i=0;i<infoOfMessages.length;i++){
            result= infoOfMessages[i];
            break;
        }
        return result;
    }



    public static void setMessagesInfo(InfoOfMessage[] messageInfo)
    {
        ControllerOfMessage.infoOfMessages = messageInfo;
    }


    public static InfoOfMessage getMessageInfo(String username)
    {
        InfoOfMessage result = null;
        if (infoOfMessages != null)
        {
            for (int i = 0; i < infoOfMessages.length;)
            {
                result = infoOfMessages[i];
                break;

            }
        }
        return result;
    }





    public static InfoOfMessage[] getMessagesInfo(){
        return infoOfMessages;
    }
}
