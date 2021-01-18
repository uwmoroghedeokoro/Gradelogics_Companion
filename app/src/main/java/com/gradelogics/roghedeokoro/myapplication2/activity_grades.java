package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class activity_grades extends AppCompatActivity {

    ListView gradeList;
    String subjectid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        gradeList=(ListView)findViewById(R.id.grade_list);

        Intent intent=getIntent();
        subjectid=intent.getStringExtra("subjectid");
        String subjectname=intent.getStringExtra("subjectname");

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //toolbar.setTitle("Profile");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(subjectname + " Grades");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_grades.super.onBackPressed();
            }
        });


       // Log.e("loa",subjectname);
        new loadgrades().execute("");

    }

    private class loadgrades extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                apikey=prefs.getString("apikey","");
                student stuobj = gson.fromJson(json, student.class);


                URL url=new URL("https://api2.gradelogics.com/api/utility/subjects/grades/"+subjectid +"/"+stuobj.id +"/"+stuobj.domain+"/"+stuobj.reg_year+"/"+stuobj.sch_term+"/"+apikey);
              //  Log.e("url",url.toString());
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                String line;
                while((line=reader.readLine()) != null){
                    result=line;
                }

            }catch (Exception e){
                Log.e("error",e.toString());
            }finally {
                urlConnection.disconnect();
            }

            Log.e(" grades json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final ArrayList<grade> dataSet=new ArrayList<grade>();
            try {


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    grade Grade=new grade();
                    Grade.grade_title=jObj.getString("AssignmentName");
                    Grade.score=jObj.getString("cScore");
                    Grade.impact=jObj.getBoolean("cUseGrade");
                    Grade.category=jObj.getString("Category");
                    Grade.grade_date=jObj.getString("DateCreatedString");

                    //subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    dataSet.add(Grade);
                    //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());

                gradeAdapter grdAdapter=new gradeAdapter(dataSet,getApplicationContext());
                gradeList.setAdapter(grdAdapter);



                grdAdapter.notify();
            }catch (Exception e)
            {

            }finally {
                // loadUI();
            }
        }
    }


}
