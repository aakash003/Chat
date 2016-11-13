package com.sash.chat.toolbox;

import com.sash.chat.typo.InfoOfFriends;

/**
 * Created by Aakash on 26-06-2016.
 */
public class ControllerOfFriend {

        public static InfoOfFriends[] friendsInfo=null;
        public static InfoOfFriends[] unapprovedFriendsInfo=null;
        private static String activeFriends;


        public static void setFriendsInfo(InfoOfFriends[] friends){
            ControllerOfFriend.friendsInfo=friends;
        }


    public static InfoOfFriends checkFriends(String username,String userKey){
        InfoOfFriends result=null;
        if(friendsInfo!=null){
            for(int i=0;i<friendsInfo.length;i++){
                if((friendsInfo[i].Username.equals(username))&&friendsInfo[i].UserKey.equals(userKey)){
                    result=friendsInfo[i];
                    break;
                }
            }
        }
        return result;
    }


    public static void setActiveFriend(String friendName){
        activeFriends = friendName;
    }

    public static String getActiveFriend()
    {
        return activeFriends;
    }

    public static InfoOfFriends getFriendsInfo(String username){
        InfoOfFriends result=null;
        if(friendsInfo!=null){
            for(int i=0;i<friendsInfo.length;i++){
                if(friendsInfo[i].Username.equals(username)){
                    result=friendsInfo[i];
                    break;
                }
            }
        }
        return result;
    }


    public static void setUnapprovedFriendsInfo(InfoOfFriends[] unapprovedFriends) {
        unapprovedFriendsInfo = unapprovedFriends;
    }



    public static InfoOfFriends[] getFriendsInfo() {
        return friendsInfo;
    }



    public static InfoOfFriends[] getUnapprovedFriendsInfo() {
        return unapprovedFriendsInfo;
    }


}
