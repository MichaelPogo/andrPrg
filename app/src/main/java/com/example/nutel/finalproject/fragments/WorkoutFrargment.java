package com.example.nutel.finalproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.example.nutel.finalproject.FirebaseDB;
import com.example.nutel.finalproject.Owner;
import com.example.nutel.finalproject.R;
import com.example.nutel.finalproject.adapters.ProgramTitlesAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class WorkoutFrargment extends Fragment {
    public View root;
    static Spinner categorySpinner;
    static ListView programsList;
    static ArrayList <Map> ctg = new ArrayList();
    FloatingActionButton addBtn;
    static Map program;
    Owner owner;
    private BackendlessUser backendlessUser;
    private Activity activity;
    static String newProgTtl;
    static boolean checkIfCameFromHere;
    SharedPreferences spinnerPrefs;
    static final String firebaseUrl = "https://sportapp-92fec.firebaseio.com/";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.workout_fragments_layout,container,false);

        this.activity=getActivity();
        categorySpinner=(Spinner)root.findViewById(R.id.category);
        program=new HashMap();
        owner = Owner.getInstance();
        backendlessUser = owner.getBackendlessUser();
        spinnerPrefs = getActivity().getSharedPreferences("myFiles",MODE_PRIVATE);
        programsList=(ListView) root.findViewById(R.id.programs);
        addBtn = (FloatingActionButton)root.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.setBackgroundColor(Color.TRANSPARENT);
               final View dv = LayoutInflater.from(activity).inflate(R.layout.add_program_fragment,null);
               new AlertDialog.Builder(activity).setView(dv).setPositiveButton("save", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String title=((EditText)dv.findViewById(R.id.exerciseTtl)).getText().toString();
                       if(title.equals("")){
                           Toast.makeText(getActivity(),"Must Have Title",Toast.LENGTH_LONG).show();
                           return;
                       }
                       FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                       ft.replace(R.id.fragmentContainer, new ProgramFragment());
                       ft.commit();
                       checkIfCameFromHere = true;
                       newProgTtl = title;
                   }
               }).setNegativeButton("cancel",null).show();

            }
        });

        programsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                program = (Map)programsList.getAdapter().getItem(position);
              //***************************************
                FirebaseDB.instance.child(program.get("title")+"").child("counter").setValue(((ProgramTitlesAdapter)programsList.getAdapter()).getCounter()+1);
               //***************************************
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainer, new ProgramFragment());
                ft.commit();



            }
        });
        return root;
    }
    public static void setProgramAdapter(ArrayList<Map> al,Context context){
        programsList.setAdapter(new ProgramTitlesAdapter(context,al,"title"));
    }
    public static void setLoction(){
        checkIfCameFromHere = false;
    }
    public static boolean getLoction(){
        return checkIfCameFromHere;
    }
    public static boolean checkToWhichModeToChange(){
        boolean location = WorkoutFrargment.getLoction();
       return location;

    }

    public static Map getProgram(){
        return program;
    }
    public static String getProgramTtl(){
        String ttl = WorkoutFrargment.getNewProgramTtl();
        if(ttl == null)return program.get("title")+"";
        return ttl;

    }
    public static void setNewTtlNull(){
        newProgTtl = null;
    }
    public static String getNewProgramTtl(){
        return newProgTtl;
    }


    @Override
    public void onStart() {
        super.onStart();

        setCategoryListener();
        Backendless.Persistence.of("SportCategory").find(new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> res) {
                ctg=new ArrayList();
                ctg.addAll(res.getData());
                categorySpinner.setAdapter(new ProgramTitlesAdapter(activity,ctg, "sportName"));
                String lastChoice=spinnerPrefs.getString("lastCategory","");
                for(int i=0,count=ctg.size();i<count;i++){
                    if(lastChoice.equals(ctg.get(i).get("sportName"))){//found at index i
                        Log.i("found selected",""+i);
                        categorySpinner.setSelection(i);
                    }
                }
            }
            @Override
            public void handleFault(BackendlessFault e) {
                Log.e("load Categories","Categoris load error"+e.getMessage());
            }
        });
    }

    public static String getSelectedCtgID(){
       // String ctgId = ctg.get((int)categorySpinner.getSelectedItemId()).get("objectId").toString(); BEFORE
        String ctgId = ctg.get(categorySpinner.getSelectedItemPosition()).get("objectId").toString();
        return ctgId;
    }


    private void getPrograms(final String catId){
        if(ProfileFragment.isAprrovalPage){
            root.setBackgroundColor(Color.GRAY);
            addBtn.setImageResource(R.drawable.ok);
            String whereClause= "sport = "+"'"+WorkoutFrargment.getSelectedCtgID()+"'"+" and approved = "+false;
            BackendlessDataQuery query=new BackendlessDataQuery();
            query.setWhereClause(whereClause);
            Backendless.Persistence.of("WorkoutPrograms").find(query, new AsyncCallback<BackendlessCollection<Map>>() {
                @Override
                public void handleResponse(BackendlessCollection<Map> res) {
                    ArrayList <Map>al= new ArrayList();
                    for(Map program:res.getData()){
                        al.add(program);
                    }
                    setProgramAdapter(al,getActivity());
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBtn.setImageResource(R.drawable.for_fab);
                    ProfileFragment.isAprrovalPage = false;
                    getPrograms(getSelectedCtgID());
                }
            });
        }else {

            String s = " sport = '" + catId + "'";
            StringBuilder whereClauseSB = new StringBuilder();
            //whereClauseSB.append("sport = '" + catId+"'").append(" and ").append("approved = "+true);ORIGINAL
            whereClauseSB.append(s).append(" and ").append("approved = " + true).append(" or ").append(s).append(" and ").append("ownerId = '" + backendlessUser.getUserId() + "'");
            System.out.println(whereClauseSB);
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClauseSB.toString());
            Backendless.Persistence.of("WorkoutPrograms").find(dataQuery, new AsyncCallback<BackendlessCollection<Map>>() {
                @Override
                public void handleResponse(BackendlessCollection<Map> res) {
                    root.setBackgroundColor(Color.TRANSPARENT);
                    programsList.setAdapter(new ProgramTitlesAdapter(activity, res.getData(), "title"));

                }

                @Override
                public void handleFault(BackendlessFault e) {
                    Log.e("load failed", "" + e.getCode());
                    programsList.setAdapter(null);
                }
            });
        }
    }

    private void setCategoryListener(){

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                getPrograms(""+ctg.get(i).get("objectId"));
                Toast.makeText(activity,"loading programs...",Toast.LENGTH_LONG).show();
                String lastChoice = ""+ctg.get(i).get("sportName");
                spinnerPrefs.edit().putString("lastCategory",lastChoice).commit();//
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(activity,"Nothing - selected",Toast.LENGTH_LONG).show();
            }
        });
    }

}
