package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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

public class student_new_msg extends AppCompatActivity {
    student stuobj;
    ListView teacher_List;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_new_msg);

        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("student_object","");
        // Log.e("jsonfrmclass",json);

        stuobj = gson.fromJson(json, student.class);
        teacher_List=(ListView)findViewById(R.id.teacherList);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Select Contact");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

       // getSupportActionBar().setTitle("New Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student_new_msg.super.onBackPressed();
            }
        });

new get_teachers().execute("");
    }

    protected class get_teachers extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        ProgressDialog progressBar=new ProgressDialog(student_new_msg.this);

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
                SharedPreferences prefs=student_new_msg.this.getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                student stuobj = gson.fromJson(json, student.class);
                String apikey=prefs.getString("apikey","");

                URL url=new URL ("https://api2.gradelogics.com/api/student/teachers/" + stuobj.id + "/" + stuobj.reg_year + "/" + stuobj.sch_term + "/" + stuobj.domain +"/" + apikey);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream is=new BufferedInputStream(urlConnection.getInputStream());

                Log.e("api url",url.toString());
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
                ArrayList<teacher> my_techers=new ArrayList<>();

                JSONArray jArray=new JSONArray(result);

                for (int x=0;x<jArray.length();x++)
                {
                    JSONObject jObj=jArray.getJSONObject(x);
                    teacher teachr=new teacher();
                    teachr.id=jObj.getInt("teacherID");
                    teachr.fullname=jObj.getString("TeacherName");

                    boolean found=false;
                    for (int c=0;c<my_techers.size();c++)
                    {
                        if(my_techers.get(c).id==teachr.id)
                         found=true;
                    }
                    if(!found)
                        my_techers.add(teachr);

                }

               teacher_list_adapter classStudentAdapter=new teacher_list_adapter(my_techers,getApplicationContext(),"");
                classStudentAdapter.notifyDataSetChanged();
                teacher_List.setAdapter(classStudentAdapter);

            }catch (Exception ex)
            {
                Log.e("classstudents error",ex.getMessage());
            }finally {
                progressBar.dismiss();
            }
        }
    }
}
