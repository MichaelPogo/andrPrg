package com.example.nutel.finalproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.example.nutel.finalproject.Exercise;
import com.example.nutel.finalproject.Owner;
import com.example.nutel.finalproject.R;
import com.example.nutel.finalproject.activities.UserActivity;
import com.example.nutel.finalproject.adapters.AddProgAdapter;
import com.example.nutel.finalproject.adapters.ProgramContentAdpater;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProgramFragment extends Fragment {
    Owner owner;
    private BackendlessUser backendlessUser;
    View root;
    private TextView titleTxt,sportTitleTxt,programWriter;
    LinearLayout exercisesLayout;
    static Button btn,finishBtn,editBtn;
    Button infoBtn,backBtn;
    EditText exrName,exrDesc;
    ListView list, exrLv;
    private static Activity activity;
    private ArrayList <Exercise> programList = new ArrayList();
    ArrayList<Exercise> exrAl=new ArrayList();
    FloatingActionButton approvedBtn;
    String EditedTitle;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root     = inflater.inflate(R.layout.program_fragment_layout,container,false);
        titleTxt = (TextView)root.findViewById(R.id.titleTxt);
        sportTitleTxt = (TextView)root.findViewById(R.id.sportTitleTxt);
        exercisesLayout = (LinearLayout)root.findViewById(R.id.exercisesLayout);
        list      = (ListView)root.findViewById(R.id.programList);
        btn       = (Button)root.findViewById(R.id.addExerciseBtn);
        exrDesc   = (EditText)root.findViewById(R.id.exerciseDesc);
        exrName   = (EditText)root.findViewById(R.id.exerciseName);
        exrLv     = (ListView) root.findViewById(R.id.exrList);
        finishBtn = (Button)root.findViewById(R.id.finishBtn);
        infoBtn   = (Button)root.findViewById(R.id.infoBtn);
        backBtn   = (Button)root.findViewById(R.id.backBtn);
        editBtn   = (Button)root.findViewById(R.id.edit_btn);
        EditedTitle=WorkoutFrargment.getProgramTtl();
        approvedBtn   = (FloatingActionButton)root.findViewById(R.id.approveBtn);
        approvedBtn.setVisibility(View.GONE);
        editBtn.setVisibility(View.GONE);
        programWriter = (TextView)root.findViewById(R.id.progWriter);
        activity=getActivity();
        owner = Owner.getInstance();
        backendlessUser = owner.getBackendlessUser();
        exrLv.setAdapter(new AddProgAdapter(activity,exrAl));
        whenApproveedBtnClick();
        //sets the mode of ProgramFragment - to addMode or showProgramMode
        if( WorkoutFrargment.checkIfCameFromHere){
            titleTxt.setText(WorkoutFrargment.getNewProgramTtl());
            lvLongPress(exrLv,exrAl);
            UserActivity.hideBtns();
            changeModeForAddProg();
        }else if(WorkoutFrargment.getProgram()!=null){
            UserActivity.showBtns();
            Backendless.UserService.findById(WorkoutFrargment.getProgram().get("ownerId").toString(), new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {
                    programWriter.append("Wrriten By:\n"+backendlessUser.getProperties().get("name"));
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Log.e("****cant find user***",""+backendlessFault);
                }
            });
            showReadyProgram();

            }else showReadyProgram();

        editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {
                    root.setBackgroundColor(Color.GRAY);
                    Toast.makeText(activity,"Entered Edit Mode",Toast.LENGTH_SHORT).show();
                   proMode();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });

            // sets long click on exercise, open dialog to Edit exercise

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finishBtn.getVisibility()==View.VISIBLE)
                    openWarningDialog("your program will be deleted\nare you sure you want to exit?");
                else backToWorkoutFragment();
            }
        });



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Exercise exercise= (Exercise)list.getAdapter().getItem(i);
                exercise.toggleIsDone();
                ((CheckBox)view.findViewById(R.id.checkbox)).setChecked(exercise.isDone());
                if(exercise.isDone()){view.setBackgroundColor(Color.GREEN);}
                else {view.setBackgroundColor(Color.TRANSPARENT);}
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = exrName.getText().toString();
                String description = exrDesc.getText().toString();
                if(name.equals("") || description.equals(""))return;
                exrAl.add(new Exercise(name,description));
                exrLv.invalidateViews();
                exrDesc.setText("");
                exrName.setText("");
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exrLv.getAdapter().getCount()==0){
                   openWarningDialog("your program is empty\n what do you want to do?");
                }else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle("are you finished?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishBtn.setClickable(false);
                            finishBtn.setText("uploading...");
                            String whereClause = "title = '" + WorkoutFrargment.getNewProgramTtl() +"'";
                            System.out.println(whereClause);
                            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                            dataQuery.setWhereClause( whereClause );
                            Backendless.Persistence.of("WorkoutPrograms").find(dataQuery,new AsyncCallback<BackendlessCollection<Map>>() {
                                @Override
                                public void handleResponse(BackendlessCollection<Map> res) {
                                    Map<String,Object> toBackendLess=new HashMap();
                                    String sportID = WorkoutFrargment.getSelectedCtgID();
                                    toBackendLess.put("approved",checkForUserPermission());
                                    toBackendLess.put("content",listToJson(exrAl).toString());
                                    toBackendLess.put("sport",sportID);
                                    toBackendLess.put("title",WorkoutFrargment.getNewProgramTtl());
                                    Backendless.Persistence.of("WorkoutPrograms").save(toBackendLess, new AsyncCallback<Map>() {
                                        @Override
                                        public void handleResponse(Map map) {
                                            System.out.println("program saved");
                                            UserActivity.showBtns();
                                            backToWorkoutFragment();
                                            finishBtn.setText("finish");
                                            finishBtn.setClickable(true);

                                        }

                                        @Override
                                        public void handleFault(BackendlessFault backendlessFault) {
                                            System.out.println("program not saved"+backendlessFault.getMessage());
                                            Log.e("save failed","error"+backendlessFault.getCode());
                                            //alertDialog("title already exist please change title name");


                                        }
                                    });

                                }
                                @Override
                                public void handleFault(BackendlessFault e) {
                                    Log.e("load failed ProF102",""+e.getCode());
                                    System.out.println("program not save");

                                }
                            });


                        }
                    }).setNegativeButton("NO",null);
                    dialog.show();

                }






            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOkDialog(infoToShow());
            }
        });

        //get some data for view

        getData(WorkoutFrargment.getProgramTtl());
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        WorkoutFrargment.setLoction();
    }


    // gets the sport name from workoutFrargment.categorySpinner
    private void getData(String name){
        System.out.println("Going to get data");
        String whereClause = "title = '" + name+ "'";
        System.out.println(whereClause);
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause( whereClause );

        Backendless.Persistence.of( "WorkoutPrograms" ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Map>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Map> foundContacts )
                    {
                        String program="";
                        // every loaded object from the "Contact" table is now an individual java.util.Map
                        System.out.println("all is good, data came");
                        for (Map item : foundContacts.getData()) {
                             program = item.get("content").toString();
                            String programttl = item.get("title").toString();
                            System.out.println(program);
                            programList.add(new Exercise(program));
                            titleTxt.setText(programttl);
                        }
                        programList=jsonToArrayList(program);
                        list.setAdapter(new ProgramContentAdpater(programList,activity));
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        // an error has occurred, the error code can be retrieved with fault.getCode()
                    }
                });


    }
    public static Button getFinishBtn(){
        return finishBtn;
    }

    private void changeModeForAddProg(){
        exercisesLayout.setVisibility(View.VISIBLE);
        sportTitleTxt.setVisibility(View.VISIBLE);
        exrLv.setVisibility(View.VISIBLE);
        finishBtn.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);

    }
    private void showReadyProgram(){
        if(checkForUserPermission())editBtn.setVisibility(View.VISIBLE);
        else{}
        infoBtn.setVisibility(View.GONE);
        exercisesLayout.setVisibility(View.GONE);
        sportTitleTxt.setVisibility(View.GONE);
        exrLv.setVisibility(View.GONE);
        finishBtn.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);

    }

    private  ArrayList<Exercise> jsonToArrayList(String program){
        ArrayList<Exercise> nameVal = new ArrayList();
        try {
            JSONObject exerciseDesc = new JSONObject(program);
            Iterator<String> keys = exerciseDesc.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                nameVal.add(new Exercise(key.toString(), exerciseDesc.getString(key.toString())));
            }
        }catch (JSONException je){}
       return nameVal;
    }
    private JSONObject listToJson(ArrayList<Exercise> arrayList){
        JSONObject pExersices=new JSONObject();
        try {
            for (int i = 0; i < arrayList.size(); i++) {
                pExersices.put(arrayList.get(i).getName(), arrayList.get(i).getDescription());
            }
        }catch (JSONException e){

        }
        return pExersices;
    }
    private boolean checkForUserPermission() {
        final String professional = "700FF817-B9A3-4437-FFF1-C7C5F4CA9E00";
        String userLevel = backendlessUser.getProperty("permissionID").toString();
        if (userLevel.equals(professional)) {
            return true;
        }
        return false;
    }

    private void openOkDialog(String title) {
        View dialogContainer =LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.dialog_layout, null)  ;   // getLayoutInflater().inflate(R.layout.dialog_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).setView(dialogContainer).create();

        TextView tv = ((TextView) dialogContainer.findViewById(R.id.infoTxt));
        Button btn = ((Button) dialogContainer.findViewById(R.id.dialogOkBtn));
        tv.setText(title);
        tv.setTextSize(24);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();//close the dialog
            }
        });

        dialog.show();
    }
    private String infoToShow(){
        String info = getResources().getString(R.string.info);
        return info;
    }
    public static void backToWorkoutFragment(){
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, new WorkoutFrargment());
        ft.commit();
        WorkoutFrargment.setNewTtlNull();
    }
    public static void openWarningDialog(String text){
        TextView tv = new TextView(activity);
        tv.setText(text);
        new AlertDialog.Builder(activity).setTitle("WARNING").setView(tv).setPositiveButton("cancel",null)
                .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserActivity.showBtns();
                        backToWorkoutFragment();
                    }
                }).show();
    }
    public void proMode() {
        infoBtn.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        exrDesc.setVisibility(View.GONE);
        exrName.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        titleTxtLong();
        lvLongPress(list,programList);
        approvedBtn.setVisibility(View.VISIBLE);
        whenApproveedBtnClick();
    }

    private void titleTxtLong(){
        titleTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final EditText changeTtl = new EditText(getActivity());
                changeTtl.setText(titleTxt.getText().toString());
                new AlertDialog.Builder(getActivity()).setView(changeTtl).setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        titleTxt.setText(changeTtl.getText().toString());
                        EditedTitle=changeTtl.getText().toString();
                    }
                }).show();
                return true;
            }
        });
    }
    private void lvLongPress(final ListView listView, final ArrayList arrayList) {
        // sets long click on exercise, open dialog to Edit exercise
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                View dialogContainer =LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.edit_exercise, null)  ;   // getLayoutInflater().inflate(R.layout.dialog_layout, null);
                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogContainer).create();
                final EditText exerTtlToEdit = (EditText)dialogContainer.findViewById(R.id.exerTtlToEdit);
                final EditText descToEdit = (EditText)dialogContainer.findViewById(R.id.descToEdit);
                dialog.show();
                final Exercise currentExercise = (Exercise) listView.getAdapter().getItem(position);
                exerTtlToEdit.setText(currentExercise.getName());
                descToEdit.setText(currentExercise.getDescription());
                Button doneBtn = (Button)dialogContainer.findViewById(R.id.doneBtn);
                Button deleteBtn = (Button)dialogContainer.findViewById(R.id.deleteBtn);
                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String editedExerTtl = exerTtlToEdit.getText().toString();
                        String editedDesc = descToEdit.getText().toString();
                        currentExercise.setNameAndDescription(editedExerTtl,editedDesc);
                        listView.invalidateViews();
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayList.remove(position);
                        listView.invalidateViews();
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });
    }
    private void whenApproveedBtnClick() {
        approvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvedBtn.setClickable(false);
                String whereClause = "title = '" + WorkoutFrargment.getProgramTtl() +"'";
                System.out.println(whereClause);
                BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                dataQuery.setWhereClause( whereClause );
                Backendless.Persistence.of("WorkoutPrograms").find(dataQuery,new AsyncCallback<BackendlessCollection<Map>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Map> res) {
                        Map<String,Object> toBackendLess=new HashMap();
                        String sportID = WorkoutFrargment.getSelectedCtgID();
                        toBackendLess.put("approved",true);
                        toBackendLess.put("content",listToJson(programList).toString());
                        toBackendLess.put("objectId",WorkoutFrargment.getProgram().get("objectId"));
                        toBackendLess.put("sport",sportID);
                        toBackendLess.put("title",EditedTitle);
                        Backendless.Persistence.of("WorkoutPrograms").save(toBackendLess, new AsyncCallback<Map>() {
                            @Override
                            public void handleResponse(Map map) {
                                System.out.println("program saved");
                                UserActivity.changeFragment(new WorkoutFrargment(),getActivity());
                                approvedBtn.setClickable(true);
                            }
                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                System.out.println("program not saved"+backendlessFault.getMessage());
                                Log.e("save failed","error"+backendlessFault.getCode());
                                //alertDialog("title already exist please change title name");
                            }
                        });
//
                    }
                    @Override
                    public void handleFault(BackendlessFault e) {
                        Log.e("load failed ProF102",""+e.getCode());
                        System.out.println("program not save");
                    }
                });
            }
        });
    }


}

