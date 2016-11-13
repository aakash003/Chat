package com.sash.chat.interfacer;

import com.sash.chat.typo.InfoOfFriends;
import com.sash.chat.typo.InfoOfMessage;

/**
 * Created by Aakash on 21-06-2016.
 */
public interface Updater {
    public void updateData(InfoOfMessage[] message, InfoOfFriends[] friends,InfoOfFriends[] unApprovedFriends,String userKey);
}
