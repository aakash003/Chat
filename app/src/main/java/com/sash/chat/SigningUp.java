package com.sash.chat;

/**
 * Created by Aakash on 21-06-2016.
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Handler;


import com.sash.chat.interfacer.Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;
import android.content.ComponentName;
import android.content.ServiceConnection;
import com.sash.chat.serve.MessagingService;
import com.sash.chat.serve.MessagingService.IMBinder;


public class SigningUp extends Activity{

    public static final int FILL_ALL_FIELD = 0;
    public static final int TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS = 1;
    public static final int SIGNUP_FAILED = 9;
    public static final int SIGNUP_SUCCESSFUL = 4;
    public static final int USERNAME_PASSWORD_LENGTH_SHORT = 5;


    private static final String SERVER_SIGNUP_SUCCESSFUL = "1";
    public static final String SERVER_SIGNUP_CRASHED= "2";

    private EditText username;
    private EditText password;
    private EditText password_confirm;
    private EditText email;
    String result;
    private Manager serviceProvider;
    private Handler handler = new Handler() ;
    private Button signUpButton;
    private Button cancelButton;
    volatile boolean isConnected;

   // Context mContext;



    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            isConnected = true;
            serviceProvider = ((MessagingService.IMBinder) service).getService();


        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            serviceProvider = null;
            Toast.makeText(SigningUp.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signUpButton = (Button)findViewById(R.id.btnRegister);
        cancelButton = (Button)findViewById(R.id.btnLinkToLoginScreen);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        password_confirm = (EditText)findViewById(R.id.confirm_password);
        email = (EditText)findViewById(R.id.email);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(serviceProvider == null)
                {
                    Toast.makeText(getApplicationContext(),"serviceProvider is null",Toast.LENGTH_LONG).show();
                    return;
                }
                else if (serviceProvider.isNetworkConnected() == false)
                {
                    Toast.makeText(getApplicationContext(),"serviceProvider is networkConnected is returning false",Toast.LENGTH_LONG).show();
                }

               else if(username.length()>0 && password.length()>0 && password_confirm.length()>0 && email.length()>0)
                {
                    if(password.getText().toString().equals(password_confirm.getText().toString()))
                    {
                        if(username.length()>=1 && password.length()>=1)
                        {
                            Thread thread = new Thread() {
                                public void run(){
                                    result = serviceProvider.signUpUser(username.getText().toString(), password.getText().toString(), email.getText().toString());

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Log.d("error", "run: result");
                                            if (result!=null) {
                                                Toast.makeText(getApplicationContext(), R.string.signup_successfull, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.signup_failed, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }

                            };
                            thread.start();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),R.string.username_and_password_length_short,Toast.LENGTH_LONG).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),R.string.signup_type_same_password_in_password_fields,Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),R.string.signup_fill_all_fields,Toast.LENGTH_LONG).show();
                }


            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(SigningUp.this,LogginIn.class);
                startActivity(intent);

            }
        });
    }





    @Override
    protected Dialog onCreateDialog(int id) {
        int message = -1;
        switch (id) {
            case TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS:

                return new AlertDialog.Builder(SigningUp.this).setMessage(R.string.signup_type_same_password_in_password_fields).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();




            case FILL_ALL_FIELD:

                return new AlertDialog.Builder(SigningUp.this).setMessage(R.string.signup_fill_all_fields).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();


            case SIGNUP_FAILED:

                return new AlertDialog.Builder(SigningUp.this).setMessage(R.string.signup_failed).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();


            case SIGNUP_SUCCESSFUL:

                return new AlertDialog.Builder(SigningUp.this).setMessage(R.string.signup_successfull).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();


            case USERNAME_PASSWORD_LENGTH_SHORT:

                return new AlertDialog.Builder(SigningUp.this).setMessage(R.string.username_and_password_length_short).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();


            default:
                return null;
        }

    }

    @Override
    protected void onResume() {
        bindService(new Intent(SigningUp.this, MessagingService.class), mConnection , Context.BIND_AUTO_CREATE);

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        unbindService(mConnection);
        super.onPause();
    }


}