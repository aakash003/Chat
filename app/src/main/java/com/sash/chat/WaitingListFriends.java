package com.sash.chat;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sash.chat.interfacer.Manager;
import com.sash.chat.serve.MessagingService;
import com.sash.chat.typo.InfoOfFriends;

/**
 * Created by Aakash on 14-Aug-16.
 */
public class WaitingListFriends extends ListActivity {

    private String[] friendUsernames;
    private Manager serviceProvider;
    String approvedFriendNames = new String();
    String discardedFriendNames = new String();
    private static final int APPROVE_SELECTED_FRIENDS_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        String names = extras.getString(InfoOfFriends.Friends_list);

        friendUsernames = names.split(",");

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, friendUsernames));

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // canceling friend request notification
        NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NM.cancel(R.string.new_friend_request_exist);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(0, APPROVE_SELECTED_FRIENDS_ID, 0, R.string.approve_selected_friends);

        return result;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {

        switch(item.getItemId())
        {
            case  APPROVE_SELECTED_FRIENDS_ID :
            {
                int reqlength = getListAdapter().getCount();

                for (int i = 0; i < reqlength ; i++)
                {
                    if (getListView().isItemChecked(i)) {
                        approvedFriendNames = approvedFriendNames.concat(friendUsernames[i]).concat(",");
                    }
                    else {
                        discardedFriendNames = discardedFriendNames.concat(friendUsernames[i]).concat(",");
                    }
                }
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        if ( approvedFriendNames.length() > 0 ||
                                discardedFriendNames.length() > 0
                                )
                        {
                            serviceProvider.sendFriendsReqsResponse(approvedFriendNames, discardedFriendNames);

                        }
                    }
                };
                thread.start();

                Toast.makeText(WaitingListFriends.this, R.string.request_sent, Toast.LENGTH_SHORT).show();

                finish();
                return true;
            }

        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onPause()
    {
        unbindService(mConnection);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bindService(new Intent(WaitingListFriends.this, MessagingService.class), mConnection , Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceProvider = ((MessagingService.IMBinder)service).getService();


        }
        public void onServiceDisconnected(ComponentName className) {
            serviceProvider = null;
            Toast.makeText(WaitingListFriends.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };
}
