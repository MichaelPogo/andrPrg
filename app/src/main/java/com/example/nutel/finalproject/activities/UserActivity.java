package com.example.nutel.finalproject.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.nutel.finalproject.Owner;
import com.example.nutel.finalproject.R;
import com.example.nutel.finalproject.fragments.NotifictionsFragment;
import com.example.nutel.finalproject.fragments.ProfileFragment;
import com.example.nutel.finalproject.fragments.WorkoutFrargment;

import java.util.HashMap;


public class UserActivity extends Activity {
    private HashMap <Integer , Fragment > fragments = new HashMap();
    private BackendlessUser owner;
    static Button workoutBtn,profileBtn,notifBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_layout);
        setTitle(R.layout.user_activity_layout);
        fragments();
        changeFragment(fragments.get(R.id.workoutBtn),this);//set workout fragment default
        owner = Owner.getInstance().getBackendlessUser();//get owner instance
        System.out.println(owner.getObjectId());

    }

    @Override
    protected void onStart() {
        super.onStart();
        workoutBtn=(Button)findViewById(R.id.workoutBtn);
        profileBtn=(Button)findViewById(R.id.profileBtn);
        notifBtn=(Button)findViewById(R.id.notifictionBtn);
    }

    //sets the fragments
    private void fragments(){
        fragments.put(R.id.profileBtn,new ProfileFragment());
        fragments.put(R.id.workoutBtn,new WorkoutFrargment());
        fragments.put(R.id.notifictionBtn, new NotifictionsFragment());
    }
    // when fragmentBtn click show the fragments
    public void changeFragmentsView(View v){
        changeFragment(fragments.get(v.getId()),this);
    }

    public static void changeFragment(Fragment f,Activity activity){
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer,f);
        ft.commit();
    }


    // menu bar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutMenuBtn://case logout

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    public static void hideBtns(){
        workoutBtn.setVisibility(View.GONE);
        profileBtn.setVisibility(View.GONE);
        notifBtn.setVisibility(View.GONE);
    }
    public static void showBtns(){
        notifBtn.setVisibility(View.VISIBLE);
        workoutBtn.setVisibility(View.VISIBLE);
        profileBtn.setVisibility(View.VISIBLE);
    }


}
