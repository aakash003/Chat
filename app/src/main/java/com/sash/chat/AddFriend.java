package com.sash.chat;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sash.chat.interfacer.Manager;
import com.sash.chat.serve.MessagingService;

import java.io.File;

/**
 * Created by Aakash on 21-06-2016.
 */
public class AddFriend extends AppCompatActivity implements View.OnClickListener {

    public EditText username;
    public Button Add;
    public Button Cancel;

    private static final int TYPE_FRIEND_USERNAME = 0;

    public Manager serviceProvider;


    private static final String LOG_TAG = "AddFriend";

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);

        setContentView(R.layout.friendfindadd);

        setTitle(getString(R.string.add_new_friend));

        Add = (Button)findViewById(R.id.btnAddFriend);
        Cancel = (Button)findViewById(R.id.btnCancel);
        username = (EditText)findViewById(R.id.et_addfriendName);

        if(Add != null)
        {
            Add.setOnClickListener(this);
        }
        else {
            Log.e(LOG_TAG, "onCreate: Add is null");
            throw new NullPointerException("onCreate: Add is null");
        }
        if(Cancel != null)
        {
            Cancel.setOnClickListener(this);
        }
        else {
            Log.e(LOG_TAG, "onCreate: Cancel is null");
            throw new NullPointerException("onCreate: Cancel is null");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MessagingService.class);
        if (mConnection != null) {
            bindService(intent, mConnection , Context.BIND_AUTO_CREATE);
        } else {
            Log.e(LOG_TAG, "onResume: mConnection is null");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mConnection != null) {
            unbindService(mConnection);
        } else {
            Log.e(LOG_TAG, "onResume: mConnection is null");
        }
    }

    public void onClick(View v)
    {
        if(v == Cancel)
        {
            Intent i =new Intent(this,ListOfFriends.class);
            startActivity(i);
        }
        else if(v==Add)
        {
            addNewFriend();
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            serviceProvider = ((MessagingService.IMBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className)
        {
            if(serviceProvider != null)
            {
                serviceProvider = null;
            }
            Toast.makeText(AddFriend.this,R.string.local_service_stopped,Toast.LENGTH_SHORT).show();
        }
    };


    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFriend.this);
        {
            if (id == TYPE_FRIEND_USERNAME)
            {
                builder.setTitle(R.string.add_new_friend).setMessage("Friend Username");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
            return builder.create();
        }
    }

        private void addNewFriend()
        {
            if(username.length()>0)
            {
                Thread thread = new Thread()
                {
                    public void run()
                    {
                        serviceProvider.addNewFriendRequest(username.getText().toString());
                    }
                };
                thread.start();
                Toast.makeText(AddFriend.this,"Request Sent",Toast.LENGTH_SHORT).show();
                //finish();
            }
            else {
                Log.e(LOG_TAG, "addNewFriend: username length (" + username.length() + ") is < 0");
                Toast.makeText(AddFriend.this, R.string.type_friend_username, Toast.LENGTH_LONG).show();
            }
        }


}
