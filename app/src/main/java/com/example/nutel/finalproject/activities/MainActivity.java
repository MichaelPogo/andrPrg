package com.example.nutel.finalproject.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.example.nutel.finalproject.Owner;
import com.example.nutel.finalproject.R;

public class MainActivity extends Activity {
    public static final String APP_ID = "3FF843F2-D3B6-BFBD-FF46-E3C8E2838A00",
                 ANDROID_SECURITY_KEY = "677226BC-0760-4518-FFF9-1EC5B4A10900",
                          APP_VERSION = "v1";
    private Button loginBtn, registerBtn;
    private EditText userName , userEmail, userPass , userRePass;
    private BackendlessUser owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Backendless.initApp(this,APP_ID,ANDROID_SECURITY_KEY,APP_VERSION);

        userName    = (EditText)findViewById(R.id.userNameTxt);
        userEmail   = (EditText)findViewById(R.id.emailTxt);
        userPass    = (EditText)findViewById(R.id.passTxt);
        userRePass  = (EditText)findViewById(R.id.repassTxt);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        loginBtn    = (Button)findViewById(R.id.loginBtn);

        owner = Owner.getInstance().getBackendlessUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("page started");
        String ownerToken = UserTokenStorageFactory.instance().getStorage().get();
        // if user is already logged in open UserActivity
        System.out.println("ownerToken: " +  ownerToken);
        if( ownerToken != null && !ownerToken.equals( "" ) ) {
            String ownerId = UserIdStorageFactory.instance().getStorage().get();
            Backendless.Data.of( BackendlessUser.class ).findById( ownerId, new AsyncCallback<BackendlessUser>(){
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {
                    Owner.getInstance().setBackendlessUser(backendlessUser);
                    Intent i = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(i);
                }
                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });
        }
    }
    //when registerBtn click
    public void register(View v) {
        if (loginBtn.getVisibility()==View.VISIBLE){   //turn to register mode
            toRegisterMode();
            ((TextView)this.findViewById(R.id.loginHeader)).setText("Create User:");
            return;
        }
        //when register mode open
        if(!checkUserInputsRegister())return;
        final String uName = userName.getText().toString(), uPass = userPass.getText().toString(), uMail = userEmail.getText().toString();
        BackendlessUser user = new BackendlessUser();
        user.setEmail(uMail);
        user.setPassword(uPass);
        user.setProperty("name",uName);
        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                Log.i("user saved", "user name: "+uName);
                logToBackendless(uMail, uPass, CreateProfileActivity.class);// reuse method
                toast("Register completed, welcome");

            }
            @Override
            public void handleFault(BackendlessFault error) {
                Log.e("something worng",error.getCode());
            }
        });


    }
    // reuse method for login to backEndLed, needed two, one from login,two from register that login when registered
    private void logToBackendless(final String uMail, final  String uPass, final Class cls){
        Backendless.UserService.login( uMail, uPass, new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse( BackendlessUser user )
            {
                Owner.getInstance().setBackendlessUser(user);
                owner = Owner.getInstance().getBackendlessUser();
                System.out.println("user login "+owner.getEmail());
                Intent i = new Intent(MainActivity.this, cls);
                startActivity(i);

            }

            public void handleFault( BackendlessFault fault )
            {
                System.out.println("cannot login");
                Log.e("eror code ",""+fault.getCode());

            }
        },true);
    }

    //when loginBtn click
    public void login(View v){
        if(!checkUserInputsLogin())return;//check that user all filleds inputs
        final String uMail = userEmail.getText().toString(), uPass = userPass.getText().toString();
        logToBackendless(uMail, uPass, UserActivity.class);// reuse method
    }
    //cancel back press button after logout, for not re-login , close APP
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Are you sure you want to exit?")
                .setNegativeButton("Stay",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}})
                .setPositiveButton("Yes, leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).setCancelable(true).show();
    }

    //check if user filled in all the fields in login mode
    private boolean checkUserInputsLogin(){
        String userEmailTxtString = userEmail.getText().toString();
        String userPasswordTxtString = userPass.getText().toString();
        if(userEmailTxtString.isEmpty() || userPasswordTxtString.isEmpty())return false;
        return true;
    }
    //checks if user filled in all the fields in register mode
    private boolean checkUserInputsRegister(){
        String userNameTxtString = userName.getText().toString();
        String userEmailTxtString = userEmail.getText().toString();
        String userPasswordTxtString = userPass.getText().toString();
        String userRePasswordTxtString = userRePass.getText().toString();
        //check if pass and repass are equals
        if(!userPasswordTxtString.equals(userRePasswordTxtString)) {
            toast("PASSWORD NOT MATCH");
            return false;
        }
        if(userNameTxtString.isEmpty() || userEmailTxtString.isEmpty() || userPasswordTxtString.isEmpty() || userRePasswordTxtString.isEmpty()) {
            toast("Please fill in all the fields");
            return false;

        }
        if(!userNameTxtString.matches("^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")||!userEmailTxtString.matches("[a-zA-Z0-9\\+\\.\\_\\%]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+")){
            toast("invalid user");
            return false;
        }
        else return true;
    }
    public void toast(String msg){//re - used toast msg
        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
    }

    //show all hide edittext and ready to register
    private void toRegisterMode() {
        loginBtn.setVisibility(View.GONE);
        userRePass.setVisibility(View.VISIBLE);
        userName.setVisibility(View.VISIBLE);
    }

    public void alertDialog(String title){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }); dialog.show();   }
}
