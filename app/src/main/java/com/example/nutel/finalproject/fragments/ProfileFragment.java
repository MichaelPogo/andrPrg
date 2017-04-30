package com.example.nutel.finalproject.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.example.nutel.finalproject.activities.CreateProfileActivity;
import com.example.nutel.finalproject.Owner;
import com.example.nutel.finalproject.R;
import com.example.nutel.finalproject.activities.MainActivity;
import com.example.nutel.finalproject.activities.UserActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    public View root;
    private static String LEVEL;
    private BackendlessUser backendlessUser;
    private TextView nameOfUser,emailOfUser,levelOfUser,genderOfUser,weightOfUser,heightOfUser;
    ImageView profilePic;
    Owner owner;
    Button logOutBtn,toAprrovalBtn,addCategoryBtn;
    RelativeLayout proRL;
    public static boolean isAprrovalPage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.profile_fragment_layout,container,false);
        profilePic = (ImageView)root.findViewById(R.id.profilePic);
        nameOfUser = (TextView)root.findViewById(R.id.nameOfUser);
        emailOfUser = (TextView)root.findViewById(R.id.emailOfUser);
        levelOfUser = (TextView)root.findViewById(R.id.levelOfUser);
        genderOfUser = (TextView)root.findViewById(R.id.genderOfUser);
        weightOfUser = (TextView)root.findViewById(R.id.weightOfUser);
        heightOfUser = (TextView)root.findViewById(R.id.heightOfUser);
        logOutBtn    = (Button) root.findViewById(R.id.logoutBtn);
        proRL = (RelativeLayout)root.findViewById(R.id.proRL);
        toAprrovalBtn = (Button)root.findViewById(R.id.toAprrovalBtn);
        addCategoryBtn = (Button)root.findViewById(R.id.addCategory);
        owner = Owner.getInstance();
        backendlessUser = owner.getBackendlessUser();
        isAprrovalPage=false;
        showProOpt();
        showUserPic();
        showUserDetails();
        logOut();
        toWaitingForAprrovalPage();
        addCategory();
        return root;
    }
    //show picture from gallery\camera if there is no pic show no_photo image
    public void showUserPic(){
        File pp = CreateProfileActivity.getImgFile();
        if(pp==null)profilePic.setImageResource(R.drawable.no_photo);
        else profilePic.getDrawable();
    }
    // method for getting the details of the current login user
    private void showUserDetails(){
        Owner.getInstance().setBackendlessUser(backendlessUser);
        nameOfUser.setText(backendlessUser.getProperty("name").toString());
        emailOfUser.setText(backendlessUser.getEmail());
        levelOfUser.setText(analyzeTheLevel());
        genderOfUser.setText(backendlessUser.getProperty("gender").toString());
        weightOfUser.setText(backendlessUser.getProperty("weight").toString()+" KG");
        heightOfUser.setText(backendlessUser.getProperty("height").toString()+" CM");
    }
    // analyze the user level to show as string
    private String analyzeTheLevel(){
        final String normal = "AB663B7B-7B4C-C104-FFE1-95FF1F989E00",
                experienced = "43738297-9A9E-1A74-FFA7-2B01AEF5E400",
                professional = "700FF817-B9A3-4437-FFF1-C7C5F4CA9E00";

        String userLevel = backendlessUser.getProperty("permissionID").toString();
        if(userLevel.equals(normal)){
            LEVEL = "normal";
        }if(userLevel.equals(experienced)){
            LEVEL = "experienced";
        }if (userLevel.equals(professional)){
            LEVEL = "professional";
        }
        return LEVEL ;
    }
    //logout user
    private void logOut(){
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backendless.UserService.logout(new AsyncCallback<Void>()
                {
                    public void handleResponse( Void response )
                    {
                        Owner.getInstance().setBackendlessUser(null);
                        startActivity(new Intent(getActivity(), MainActivity.class));

                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        System.out.println("not!!! log out" + fault.getMessage());
                    }
                });
            }
        });
    }
    private void showProOpt() {
        if (analyzeTheLevel().equals("professional")) {
            proRL.setVisibility(View.VISIBLE);
        }
        else proRL.setVisibility(View.GONE);
    }
    private void toWaitingForAprrovalPage(){
        toAprrovalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                isAprrovalPage=true;
                UserActivity.changeFragment(new WorkoutFrargment(),getActivity());
            }
        });
    }
    private void addCategory(){
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final EditText et = new EditText(getActivity());
                et.setHint("Category Name");
                new AlertDialog.Builder(getActivity()).setTitle("Create Category").setView(et).setNegativeButton("cancel",null).setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map map=new HashMap();
                        map.put("sportName",et.getText().toString());
                        Backendless.Persistence.of("SportCategory").save(map, new AsyncCallback<Map>() {
                            @Override
                            public void handleResponse(Map map) {
                                Log.i("saved"," success");
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                            Log.e("category not saved"," code"+backendlessFault.getCode());
                            }
                        });
                    }
                }).show();
            }
        });
    }
}
