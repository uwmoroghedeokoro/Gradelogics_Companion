package com.gradelogics.roghedeokoro.myapplication2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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

public class activity_view_assignment extends AppCompatActivity {

    grade this_assignment;
    teacher meTeacher;
    ListView gradeList;
    ArrayList<student> class_grades;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment);

        Intent intent=getIntent();
        Gson gson=new Gson();
        String json=intent.getStringExtra("assg_model");

         this_assignment=gson.fromJson(json,grade.class);
         gradeList=(ListView)findViewById(R.id.listGrades);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(this_assignment.grade_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_view_assignment.super.onBackPressed();
            }
        });

        TextView asubject,acategory,adate,aclass,aavg;
        asubject=findViewById(R.id.txtSubject);
        asubject.setText(this_assignment.subject);
        acategory=findViewById(R.id.txtCategory);
        acategory.setText(this_assignment.category);
        adate=findViewById(R.id.txtdate);
        adate.setText(this_assignment.grade_date);
        aavg=findViewById(R.id.txtAvg);
        aavg.setText(this_assignment.score);
        aclass=findViewById(R.id.txtClass);
        aclass.setText(this_assignment.className);

        new get_grades().execute("");
    }

    protected class get_grades extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {

            String result="";
            try
            {
                SharedPreferences prefs=getSharedPreferences("gradelogics",0);
                String json=prefs.getString("login_object","");
                Gson gson=new Gson();

                meTeacher=gson.fromJson(json,teacher.class);

                URL url=new URL ("https://api2.gradelogics.com/api/assignment/get/" + this_assignment.grade_id +  "/" + meTeacher.domain +"/" +meTeacher.api_key);
                Log.e("grades url",url.toString());
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
            Log.e("classroom grades",result);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
              class_assignment_view_adapter  class_assignment_adapter;
                class_grades=new ArrayList<>();

                JSONObject jObjz=new JSONObject(result);
                JSONArray jArray=new JSONArray(jObjz.getString("Grades"));

                for (int x=0;x<jArray.length();x++)
                {
                    JSONObject jObj=jArray.getJSONObject(x);
                    student std=new student();
                    std.id=jObj.getInt("student_id");
                    std.fullname=jObj.getString("student_name");
                   // std.grade_date=jObj.getString("cDueDate");
                    std.score=jObj.getString("score");
                   // std.className=jObj.getString("className");
                    class_grades.add(std);
                }

                class_assignment_adapter=new class_assignment_view_adapter(class_grades,getApplicationContext());
                class_assignment_adapter.notifyDataSetChanged();
                gradeList.setAdapter(class_assignment_adapter);

            }catch (Exception ex)
            {
                Log.e("classstudents error",ex.getMessage());
            }
        }
    }
}
