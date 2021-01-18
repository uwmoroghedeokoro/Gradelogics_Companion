package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class activity_teacher_new_msg extends AppCompatActivity {

    ListView studentList;
    teacher meTeacher;
    Spinner subjectS,classS;
    ArrayList<student> class_students;
    int active_subject,active_class=-1;
    class_studentAdapter classStudentAdapter;
    boolean userIsInteracting=false;
    String current_view="new_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_new_msg);

        SharedPreferences prefs=getSharedPreferences("gradelogics",0);
        final SharedPreferences.Editor editor=prefs.edit();
        Intent intent=getIntent();
        String msgID=intent.getStringExtra("msg_id");

        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        active_subject= prefs.getInt("active_subject",-1);
        active_class=prefs.getInt("active_class",-1);

        meTeacher=gson.fromJson(json,teacher.class);

        class_students=new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Select Contact");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("New Parent Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_teacher_new_msg.super.onBackPressed();
            }
        });

        subjectS=(Spinner)findViewById(R.id.spin_subject);
        classS=(Spinner)findViewById(R.id.spin_classroom);
        studentList=(ListView)findViewById(R.id.studentList);

        subjectS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                if (userIsInteracting==true) {
                    editor.putInt("active_subject", s.tag);
                    active_subject=s.tag;
                    editor.apply();
                    Log.e("subject", "selected " + String.valueOf(s.tag));
                    new get_students().execute(String.valueOf(active_subject),String.valueOf(active_class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (userIsInteracting) {
                    StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                    active_class=s.tag;
                    editor.putInt("active_class", s.tag);
                    editor.apply();
                    new get_students().execute(String.valueOf(active_subject),String.valueOf(active_class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       loadGUI();
    }

    protected class get_students extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        ProgressDialog progressBar=new ProgressDialog(activity_teacher_new_msg.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setMessage("Loading ...");
            progressBar.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String result="";
            try
            {


                URL url=new URL ("https://api2.gradelogics.com/api/classroom/students/" + params[1] + "/" + meTeacher.reg_year + "/" + meTeacher.sch_term + "/" + params[0] + "/" + meTeacher.domain +"/" +meTeacher.api_key);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream is=new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader=new BufferedReader(new InputStreamReader(is));

                String line;
                while((line=reader.readLine()) != null){
                    result=line;
                }


            }catch (Exception ex)
            {

            }finally {
                urlConnection.disconnect();
            }
            Log.e("classroom stud",result);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                class_students=new ArrayList<>();

                JSONArray jArray=new JSONArray(result);

                for (int x=0;x<jArray.length();x++)
                {
                    JSONObject jObj=jArray.getJSONObject(x);
                    student std=new student();
                    std.id=jObj.getInt("studentID");
                    std.fullname=jObj.getString("studentName");
                    std._classroom.className=jObj.getString("className");
                    std.subject=jObj.getString("subjectName");
                    std.term_avg=jObj.getString("studentSubjectAVG");
                    std.img_url=jObj.getString("studentPic");
                    std._OverallAvg=jObj.getString("subjectOvrAvg");
                    class_students.add(std);
                }

                classStudentAdapter=new class_studentAdapter(class_students,activity_teacher_new_msg.this,current_view);
                classStudentAdapter.notifyDataSetChanged();
                studentList.setAdapter(classStudentAdapter);

            }catch (Exception ex)
            {
                Log.e("classstudents error",ex.getMessage());
            }finally {
                progressBar.dismiss();
            }
        }
    }

    private void loadGUI()
    {
        ArrayList<StringWithTag>subjectlist=new ArrayList<>();
        ArrayList<StringWithTag>classroomlist=new ArrayList<>();


        for (subject subj:meTeacher.subjects
        ) {
            StringWithTag swt=new StringWithTag(subj.subjectName,Integer.valueOf(subj.id));
            subjectlist.add(swt);
        }

        for (classroom classr:meTeacher.classrooms
        ) {
            StringWithTag swt=new StringWithTag(classr.className,classr.classID);
            classroomlist.add(swt);
        }


        ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_text_white, subjectlist);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectS.setAdapter(adap);
//set selection
        for (int s=0;s<adap.getCount();s++){
            StringWithTag swT=adap.getItem(s);
            if (swT.tag==active_subject){
                subjectS.setSelection(s);
            }
        }

        ArrayAdapter<StringWithTag> adap2 = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_text_white, classroomlist);
        adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classS.setAdapter(adap2);
//set selection
        for (int s=0;s<adap2.getCount();s++){
            StringWithTag swT=adap2.getItem(s);
            if (swT.tag==active_class){
                classS.setSelection(s);
            }
        }


        SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
        //get students
        StringWithTag selected_subject=adap.getItem(subjectS.getSelectedItemPosition());
        StringWithTag selected_class=adap2.getItem(classS.getSelectedItemPosition());

        Gson gson=new Gson();

        editor.putInt("active_subject",selected_subject.tag);
        editor.putInt("active_class",selected_class.tag);
        editor.apply();


         new get_students().execute(String.valueOf(selected_subject.tag),String.valueOf(selected_class.tag));
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }
}
