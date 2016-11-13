package com.sash.chat;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.sash.chat.interfacer.Manager;
import com.sash.chat.serve.MessagingService;
import com.sash.chat.toolbox.StorageManipulater;
import com.sash.chat.typo.InfoOfFriends;
import com.sash.chat.typo.InfoOfMessage;

import java.io.UnsupportedEncodingException;

/**
 * Created by Aakash on 21-06-2016.
 */
public class PerformingMessaging extends AppCompatActivity {

    public static final int MESSAGE_NOT_SENT = 0;

    public TextView messageBox;
    public EditText sendMessage;
    public Button send;

    public Manager serviceProvider;

    public InfoOfFriends friends = new InfoOfFriends();

    public StorageManipulater localDataStorage;                 //SQLite used for storing messages

    public Cursor cursor;                                       //use to retrieve from database


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceProvider = ((MessagingService.IMBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceProvider = null;
            Toast.makeText(PerformingMessaging.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.message);

        messageBox = (EditText) findViewById(R.id.messagehistorybox);

        sendMessage = (EditText) findViewById(R.id.sendmessagebox);

        sendMessage.requestFocus();

        send = (Button) findViewById(R.id.sendMessageButton);

        Bundle extras = this.getIntent().getExtras();

        friends.userName = extras.getString(InfoOfFriends.Username);
        friends.ip = extras.getString(InfoOfFriends.Ip);
        friends.port = extras.getString(InfoOfFriends.Port);

        String msg = extras.getString(InfoOfMessage.MessageText);

        setTitle("Messaging with " + friends.userName);

        localDataStorage = new StorageManipulater(this);
        cursor = localDataStorage.get(friends.userName, MessagingService.USERNAME);

        if (cursor.getCount() > 0) {
            int noOfScorer = 0;
            cursor.moveToFirst();
            while ((!cursor.isAfterLast()) && noOfScorer < cursor.getCount()) {
                noOfScorer++;
                this.appendToMessageHistory(cursor.getString(2), cursor.getString(3));
                cursor.moveToNext();
            }
        }
        localDataStorage.close();

        if (msg != null) {
            this.appendToMessageHistory(friends.userName, msg);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friends.userName+msg).hashCode());
        }
        send.setOnClickListener(new View.OnClickListener() {


            CharSequence message;

            public void onClick(View v) {

                message = sendMessage.getText();
                if (message.length() > 0) {
                    appendToMessageHistory(serviceProvider.getUsername(), message.toString());
                    localDataStorage.insert(serviceProvider.getUsername(), friends.toString(), message.toString());
                    sendMessage.setText("");
                    Thread thread = new Thread() {
                        private Handler handler = new Handler();
                        public void run() {

                            try {
                                if (serviceProvider.sendMessage(serviceProvider.getUsername(), friends.userName, message.toString()) == null) {
                                    handler.post(new Runnable(){

                                        public void run()
                                        {
                                            Toast.makeText(getApplicationContext(),"message can't be sent",Toast.LENGTH_LONG).show();
                                        }

                                    });
                                }
                            } catch (UnsupportedEncodingException e) {

                                Toast.makeText(getApplicationContext(),"message can't be sent",Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                        }
                    };
                    thread.start();
                }
            }


        });

    }


//braoadcastreceiver to alert thre other user in case message is send
public class MessageReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        String username = extras.getString(InfoOfMessage.User_id);
        String message = extras.getString(InfoOfMessage.MessageText);

        if(username != null && message != null)
        {
            if(friends.userName.equals(username))
            {
                appendToMessageHistory(username,message);
                localDataStorage.insert(username,InfoOfFriends.Username ,message);
            }
        }

    }


    public MessageReceiver messageReceiver = new MessageReceiver();


}



    public void appendToMessageHistory(String username, String message) {
        if (username != null && message != null) {
            messageBox.append(username + ":\n");
            messageBox.append(message + "\n");
        }
    }
}
