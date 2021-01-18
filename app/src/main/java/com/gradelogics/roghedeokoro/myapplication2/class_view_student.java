package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class class_view_student extends AppCompatActivity {
    TextView txt_fullname,txt_school,txt_subject,txt_schyear,txt_schterm,txt_avg,txt_class,txt_excel,txt_struggle,txt_term,txt_ovr;
    CircleImageView img_pic,stupic;
    ListView gradesList;
    student stuobj;
    gradeBook gB;
    int active_subject;
    teacher meTeacher;
    String current_view="view_student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_view_student);

        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson = new Gson();
        String json=prefs.getString("active_gradebook","");
        gB=gson.fromJson(json,gradeBook.class);

        Intent thisIntent=getIntent();
        String student_json = thisIntent.getStringExtra("student_object");
        stuobj=gson.fromJson(student_json,student.class);
        active_subject= prefs.getInt("active_subject",-1);

        meTeacher=gson.fromJson(prefs.getString("login_object",""),teacher.class);
//        Log.e("jsonfrmclass",json);

        //ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, sibling_pics);

        //  viewPager.setAdapter(viewPagerAdapter);

        NumberFormat format=NumberFormat.getCurrencyInstance();
        gradesList=(ListView)findViewById(R.id.listGrades);
        txt_subject=(TextView)findViewById(R.id.txtSubject);
        txt_schyear=(TextView)findViewById(R.id.txtSchYear);
        txt_avg=(TextView)findViewById(R.id.txtAvg);
        txt_class=(TextView)findViewById(R.id.txtClass) ;
        stupic=(CircleImageView) findViewById(R.id.stu_pic);
       // txt_term=(TextView)findViewById(R.id.txtSchTerm);
        txt_ovr=(TextView)findViewById(R.id.txt_overall);

        final SwipeRefreshLayout swly=(SwipeRefreshLayout)findViewById(R.id._swly);
        swly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new loadgrades().execute("");
                swly.setRefreshing(false);
            }
        });

        txt_subject.setText(stuobj.subject);
        txt_schyear.setText(gB.gradebookName);
        //txt_term.setText(gB.gradebookTerm);
        txt_avg.setText(stuobj.term_avg);
        txt_class.setText(stuobj._classroom.className);
        txt_ovr.setText(stuobj._OverallAvg);
        Picasso.get()
                .load(stuobj.img_url)
                .fit()
                .centerInside()
                .into(stupic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(stuobj.fullname);
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        // getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class_view_student.super.onBackPressed();
            }
        });


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
                //String json = prefs.getString("student_object", "");
                apikey=prefs.getString("apikey","");
                //student stuobj = gson.fromJson(json, student.class);


                URL url=new URL("https://api2.gradelogics.com/api/utility/subjects/grades/"+active_subject +"/"+stuobj.id +"/"+meTeacher.domain+"/"+gB.gradebookYear+"/"+gB.gradebookTerm+"/"+apikey);
                 Log.e("student gardes url",url.toString());
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
                gradesList.setAdapter(grdAdapter);



                grdAdapter.notify();
            }catch (Exception e)
            {

            }finally {
                // loadUI();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new loadgrades().execute("");
    }

}
