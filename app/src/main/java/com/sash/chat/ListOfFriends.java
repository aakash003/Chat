package com.sash.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sash.chat.interfacer.Manager;
import com.sash.chat.serve.MessagingService;
import com.sash.chat.toolbox.ControllerOfFriend;
import com.sash.chat.typo.InfoOfFriends;
import com.sash.chat.typo.InfoStatus;


public class ListOfFriends extends ListActivity {



    public static final int ADD_NEW_FRIEND = Menu.FIRST;
    public static final int EXIT = Menu.FIRST + 1;

    public Manager imService=null;

    public FriendListAdapter friendListAdapter;

    public String ownusername=new String();

    //public ListView listView;

    public class FriendListAdapter extends BaseAdapter
    {

        class ViewHolder
        {
            TextView text;
            ImageView icon;
        }

        public LayoutInflater inflater;
        public Bitmap onlineIcon;
        public Bitmap offlineIcon;
        public InfoOfFriends[] friends = null;


        public FriendListAdapter(Context context)
        {
            super();
            inflater = LayoutInflater.from(context);

            onlineIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.online);

            offlineIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.offline);
        }


        public void setFriends(InfoOfFriends[] friends)
        {
            this.friends = friends;
        }


        @Override
        public int getCount() {
            return friends.length;
        }

        @Override
        public InfoOfFriends getItem(int position) {

            return friends[position];
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.

            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.

            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.listfriendscreen, null);

                holder = new ViewHolder();
                holder.text = (TextView) findViewById(R.id.text);
                holder.icon = (ImageView) findViewById(R.id.icon);



                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.


                convertView.setTag(holder);
            }
            else
            {	// Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder)convertView.getTag();

            }

            holder.text.setText(friends[position].userName);
            holder.icon.setImageBitmap(friends[position].status == InfoStatus.ONLINE ? onlineIcon : offlineIcon);


            return convertView;
        }
    }

    public class MessageReceiver extends  BroadcastReceiver  {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("Broadcast receiver ", "received a message");
            Bundle extra = intent.getExtras();
            if (extra != null)
            {
                String action = intent.getAction();
                if (action.equals(MessagingService.FRIEND_LIST_UPDATED))
                {
                    // taking friend List from broadcast
                    //String rawFriendList = extra.getString(FriendInfo.FRIEND_LIST);
                    //FriendList.this.parseFriendInfo(rawFriendList);
                    ListOfFriends.this.updateData(ControllerOfFriend.getFriendsInfo(),
                            ControllerOfFriend.getUnapprovedFriendsInfo());

                }
            }
        }

    };
    public MessageReceiver messageReceiver = new MessageReceiver();

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            imService = ((MessagingService.IMBinder)service).getService();

            InfoOfFriends[] friends = ControllerOfFriend.getFriendsInfo(); //imService.getLastRawFriendList();
            if (friends != null) {
                ListOfFriends.this.updateData(friends, null); // parseFriendInfo(friendList);
            }

            setTitle(imService.getUsername() + "'s friend list");
            ownusername = imService.getUsername();
        }
        public void onServiceDisconnected(ComponentName className) {
            imService = null;
            Toast.makeText(ListOfFriends.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };



    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_of_friends);

        friendListAdapter = new FriendListAdapter(this);
        //listView=(ListView)findViewById(R.id.list);


    }
    public void updateData(InfoOfFriends[] friends, InfoOfFriends[] unApprovedFriends)
    {
        if (friends != null) {
            friendListAdapter.setFriends(friends);
            setListAdapter(friendListAdapter);
        }

        if (unApprovedFriends != null)
        {
            NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (unApprovedFriends.length > 0)
            {
                String tmp = new String();
                for (int j = 0; j < unApprovedFriends.length; j++) {
                    tmp = tmp.concat(unApprovedFriends[j].userName).concat(",");
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(getText(R.string.new_friend_request_exist));
				/*Notification notification = new Notification(R.drawable.stat_sample,
						getText(R.string.new_friend_request_exist),
						System.currentTimeMillis());*/

                Intent i = new Intent(this, WaitingListFriends.class);
                i.putExtra(InfoOfFriends.Friends_list, tmp);

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        i, 0);

                mBuilder.setContentText("You have new friend request(s)");
				/*notification.setLatestEventInfo(this, getText(R.string.new_friend_request_exist),
												"You have new friend request(s)",
												contentIntent);*/

                mBuilder.setContentIntent(contentIntent);


                NM.notify(R.string.new_friend_request_exist, mBuilder.build());
            }
            else
            {
                // if any request exists, then cancel it
                NM.cancel(R.string.new_friend_request_exist);
            }
        }

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        Intent i = new Intent(this, PerformingMessaging.class);
        InfoOfFriends friend = friendListAdapter.getItem(position);
        i.putExtra(InfoOfFriends.Username, friend.userName);
        i.putExtra(InfoOfFriends.Port, friend.port);
        i.putExtra(InfoOfFriends.Ip, friend.ip);
        startActivity(i);
    }




    @Override
    protected void onPause()
    {
        unregisterReceiver(messageReceiver);
        unbindService(mConnection);
        super.onPause();
    }

    @Override
    protected void onResume()
    {

        super.onResume();
        bindService(new Intent(ListOfFriends.this, MessagingService.class), mConnection , Context.BIND_AUTO_CREATE);

        IntentFilter i = new IntentFilter();
        //i.addAction(IMService.TAKE_MESSAGE);
        i.addAction(MessagingService.FRIEND_LIST_UPDATED);

        registerReceiver(messageReceiver, i);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(0, ADD_NEW_FRIEND, 0, R.string.add_new_friend);

        menu.add(0, EXIT, 0, R.string.exit_application);

        return result;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {

        switch(item.getItemId())
        {
            case ADD_NEW_FRIEND:
            {
                Intent i = new Intent(ListOfFriends.this, AddFriend.class);
                startActivity(i);
                return true;
            }
            case EXIT:
            {
                imService.exit();
                finish();
                return true;
            }
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);




    }
}




