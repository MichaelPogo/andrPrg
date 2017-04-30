package com.example.nutel.finalproject.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.nutel.finalproject.Owner;
import com.example.nutel.finalproject.R;

import java.io.File;
import java.io.IOException;

public class CreateProfileActivity extends Activity {
    public static final String APP_ID = "3FF843F2-D3B6-BFBD-FF46-E3C8E2838A00",
            ANDROID_SECURITY_KEY = "677226BC-0760-4518-FFF9-1EC5B4A10900",
            APP_VERSION = "v1";
    private TextView gender;
    private EditText height, weight;
    private ImageView img;
    private BackendlessUser backendlessUser;
    static final int GALLERY=1 , CAMERA=2;
    static File imgFile;
    Owner owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_layout);

        gender   = (TextView)findViewById(R.id.genderTxt);
        height   = (EditText)findViewById(R.id.heightTxt);
        weight   = (EditText)findViewById(R.id.weightTxt);
        img      = (ImageView)findViewById(R.id.img);
        img.setImageResource(R.drawable.no_photo);
        Backendless.initApp(this,APP_ID,ANDROID_SECURITY_KEY,APP_VERSION);
        owner = Owner.getInstance();
        backendlessUser = owner.getBackendlessUser();

    }

    //when save profileBtn clicked
    public void finishEditProfile(View v){
        //check that user has entered all required fileds
        if(!checkUserInputs())return;
        //set property to user and update to backEndLess
        backendlessUser.setProperty("gender",gender.getText().toString());
        backendlessUser.setProperty("weight",weight.getText().toString());
        backendlessUser.setProperty("height",height.getText().toString());
        Backendless.UserService.update(backendlessUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                //getting login user instance
                Owner.getInstance().setBackendlessUser(backendlessUser);
                //set and open activity UserActivity
                Intent i = new Intent(CreateProfileActivity.this,UserActivity.class);
                startActivity(i);
                System.out.println("user has updated");
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.e("not update!!!",backendlessFault.getCode());
                        System.out.println("user not!!! updated"+backendlessFault.getMessage());
            }
        });
    }
    // boolean method that check if user fill all fields
    private boolean checkUserInputs(){
        String userWeight = weight.getText().toString();
        String userHeight = height.getText().toString();
        String userGender = gender.getText().toString();
        if(userWeight.isEmpty() || userHeight.isEmpty() || userGender.isEmpty()){
            Toast.makeText(this,"fill in everything",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    // add pictureBtn that open alert dialog to choose male\female
    public void addPicture(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateProfileActivity.this);
        dialog.setTitle("choose from where to take the picture");
        dialog.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,GALLERY);//open gallery
            }
        });
        dialog.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //implicit Intent - for image capture from camera
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    createImageFile();
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
                    startActivityForResult(i,CAMERA);//open camera
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }
    //create file for pictures that took from camera
    private void createImageFile() throws IOException {
        String imageFileName = "IMG_" + System.currentTimeMillis() + "_";
        imgFile = File.createTempFile(//Create & store temporary image file
                imageFileName,  //image file name
                ".jpg",         //extension
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)//photos directory
        );
    }
    public static File getImgFile(){

        return imgFile;
    }
    // at the end of choosing picture, set the picture for user
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if(resultCode==RESULT_OK){//good response
            if(requestCode==GALLERY)img.setImageURI(i.getData());//show chosen pic to user
            else if(requestCode==CAMERA){
                Bitmap bm;
                bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());//Bitmap full size photo
                img.setImageBitmap(bm);//show pic from camera to user

            }
        }
    }
    // dialog to choose male\female
    public void maleOrFemale(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateProfileActivity.this);
        dialog.setTitle("choose gender");
        dialog.setPositiveButton("MALE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText("male");
            }
        });
        dialog.setNegativeButton("FEMALE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText("female");
            }
        });
        dialog.show();
    }

}
