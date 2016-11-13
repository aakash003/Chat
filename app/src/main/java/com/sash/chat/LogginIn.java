package com.sash.chat;

/**
 * Created by Aakash on 21-06-2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sash.chat.interfacer.Manager;
import com.sash.chat.serve.MessagingService;

import java.io.UnsupportedEncodingException;

public class LogginIn extends Activity {
    public static final int Connected_to_service=0;
    public static final int FILL_BOTH_USERNAME_PASSWORD=1;
    public static final String Authentical_failed= "failed";

    public static final String Friend_list = "friend_list";
    public static final int Make_sure_username_password=2;
    public static final int connected_to_network=3;




    protected static final int NOT_CONNECTED_TO_SERVICE = 0;
    protected static final int FILL_BOTH_USERNAME_AND_PASSWORD = 1;
    public static final String AUTHENTICATION_FAILED = "0";
    public static final String FRIEND_LIST = "FRIEND_LIST";
    protected static final int MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT = 2 ;
    protected static final int NOT_CONNECTED_TO_NETWORK = 3;


    public EditText username=null;
    public EditText password=null;
    Button loginButton;
    Button notAmember;
    public Manager serviceProvider;

    public static final int sign_up = Menu.FIRST;
    public static final int exit = Menu.FIRST+1;


    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceProvider = ((MessagingService.IMBinder) service).getService();
            if (serviceProvider.isUserAuthenticated() == true) {
                Intent i = new Intent(LogginIn.this, ListOfFriends.class);
                startActivity(i);
                LogginIn.this.finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceProvider = null;
            Toast.makeText(LogginIn.this,R.string.local_service_stopped,Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(LogginIn.this,MessagingService.class));


        setContentView(R.layout.activity_login);
        setTitle("Login");
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.btnLogin);
        notAmember = (Button)findViewById(R.id.btnLinkToRegisterScreen);
        notAmember.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogginIn.this,SigningUp.class);
                startActivity(i);
                LogginIn.this.finish();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(serviceProvider == null)
                {
                    Toast.makeText(getApplicationContext(),"serviceProvider is null",Toast.LENGTH_LONG).show();
                    return;
                }
                else if (serviceProvider.isNetworkConnected() == false)
                {
                    Toast.makeText(getApplicationContext(),R.string.not_connected_to_service,Toast.LENGTH_LONG).show();
                }
                else if(username.length()>0 && password.length()>0)
                {
                    Thread loginThread = new Thread()     //thread is used to handle two process authenticating user and authentication failed
                    {
                        private Handler handler = new Handler();     //allows to handle above threads

                        @Override
                        public void run()
                        {
                            String result = null;

                            try {
                                result = serviceProvider.authenticateUser(username.getText().toString(),password.getText().toString());
                                Log.d("ret","v" + result);
                                //from Manager.class
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }


                            if(result == null || result.equals(AUTHENTICATION_FAILED))
                            {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(getApplicationContext(),R.string.make_sure_username_and_password_correct,Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else
                            {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        Intent i = new Intent(LogginIn.this,ListOfFriends.class);
                                        startActivity(i);
                                        LogginIn.this.finish();
                                    }
                                });
                            }

                        }
                    };
                    loginThread.start();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),R.string.fill_both_username_and_password,Toast.LENGTH_LONG).show();
                }

            }

        });


    }


    @Override
    protected Dialog onCreateDialog(int id) {
        int message = -1;
        switch (id) {
            case NOT_CONNECTED_TO_SERVICE:
                message = R.string.not_connected_to_service;
                break;
            case FILL_BOTH_USERNAME_AND_PASSWORD:
                message = R.string.fill_both_username_and_password;
                break;
            case MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT:
                message = R.string.make_sure_username_and_password_correct;
                break;
            case NOT_CONNECTED_TO_NETWORK:
                message = R.string.not_connected_to_network;
                break;
            default:
                break;
        }
        if (message == -1)
        {
            return null;
        }
        else
        {
            return new AlertDialog.Builder(LogginIn.this)
                    .setMessage(message)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
    				/* User clicked OK so do some stuff */
                        }
                    })
                    .create();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(0,sign_up,0,R.string.sign_up);
        menu.add(0,exit,0,R.string.exit_application);

        return result;
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
        bindService(new Intent(LogginIn.this, MessagingService.class), mConnection , Context.BIND_AUTO_CREATE);

        super.onResume();
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
            case sign_up:
                Intent i = new Intent(LogginIn.this,SigningUp.class);
                startActivity(i);
                return true;
            case exit:
                return true;
        }
        return super.onMenuItemSelected(featureId,item);
    }



}
