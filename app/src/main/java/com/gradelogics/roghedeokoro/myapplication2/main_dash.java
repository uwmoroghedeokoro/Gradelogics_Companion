package com.gradelogics.roghedeokoro.myapplication2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class main_dash extends AppCompatActivity {


    RecyclerView lv;
    siblingAdapter sibling_Adapter;
    ArrayList<student>sblings;
    RecyclerView.LayoutManager mLayoutManager;

    TextView schName,schYear,schTerm,txt_msg_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dash);

        sblings=new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        lv=(RecyclerView) findViewById(R.id.recy_view);


        txt_msg_count=(TextView)findViewById(R.id.txt_msg_badge_count);
        schName=(TextView)findViewById(R.id.txt_sch_name);
        schYear=(TextView)findViewById(R.id.txt_sch_year);
        schTerm=(TextView)findViewById(R.id.txt_sch_term);
        final SwipeRefreshLayout _swly=(SwipeRefreshLayout)findViewById(R.id.swly);

        _swly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getSiblings().execute("");
                _swly.setRefreshing(false);
            }
        });

        lv.setLayoutManager(mLayoutManager);

        ImageView icologout=(ImageView)findViewById(R.id.ico_logout);
        icologout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent=new Intent(getApplicationContext(),activity_login.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("gradelogics", MODE_PRIVATE);
        Gson gson = new Gson();
        student stud; //  = gson.fromJson(sharedPreferences.getString("student_object",""));

        String json = sharedPreferences.getString("student_object","");
       // Log.e("jsonfrmclass",json);

        stud = gson.fromJson(json, student.class);

        schName.setText(sharedPreferences.getString("schName",""));
        schYear.setText(stud.reg_year);
        schTerm.setText("Term " + stud.sch_term);
     //   editor.putString("student_object", json);


        //set alarm
        Intent alarmIntent=new Intent(getApplicationContext(),alarmReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(getApplicationContext(),0,alarmIntent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

      //  alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10 * 1000, pendingIntent);
        ///

        new getSiblings().execute("");
    }


    private class getSiblings extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

       // ProgressDialog progressDialog=new ProgressDialog(main_dash.this);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Loading Students",Toast.LENGTH_LONG).show();
        }
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                studentid=(prefs.getInt("student_id",0));
                domain=prefs.getString("domain","-");
                apikey=prefs.getString("apikey","");
                //  schyear=prefs.getString("reg_year","-");
                //  schterm=prefs.getString("school_term","-");

                // domain=stuobj.domain;
                // schyear=stuobj.reg_year;
                // schterm=stuobj.sch_term;

                URL url=new URL("https://api2.gradelogics.com/api/student/siblings/"+ String.valueOf(studentid) +"/"+ domain+"/"+apikey);
                urlConnection=(HttpURLConnection)url.openConnection();
    Log.e("get sibling",url.toString());
                int code = urlConnection.getResponseCode();

                Log.e("statusCode",String.valueOf(code));

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

            Log.e("sibling json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final StringBuilder sibling_pics=new StringBuilder();
            final StringBuilder sibling_ids=new StringBuilder();
            final StringBuilder sibling_pwd=new StringBuilder();
            int msg_total_count=0;
            // final Arra
            try {

                //clear array
                sblings.clear();
                //ArrayList<student> sblings=new ArrayList<>();
                DecimalFormat value = new DecimalFormat("#.#");
                value.format(2.16851);


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=getSharedPreferences("gradelogics",MODE_PRIVATE).edit();
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);

                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    // subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    // dataSet.add(nSubject);
                   // String.format("%.2f", d);

                   student stu=new student();
                   stu.fullname=jObj.getString("cFullName");
                   stu.domain=jObj.getString("ConnString");
                   stu.department=jObj.getString("cDepartmentName");
                   stu.term_avg=String.format("%.1f",jObj.getDouble("termAvg"));
                   stu.excel=(jObj.getInt("excel"));
                    stu.struggle=(jObj.getInt("struggle"));
                    stu.id=(jObj.getInt("cStudentID"));

                    //lets register device ID for each student

                    String device_id=prefs.getString("student_device_id_"+String.valueOf(stu.id),"-");
                    if (device_id.equals("-"))
                    {
                        device_id= FirebaseInstanceId.getInstance().getToken();
                        editor.putString("student_device_id_"+String.valueOf(stu.id),device_id);
                        editor.apply();

                        //senddevice
                        new send_device_id().execute(String.valueOf(stu.id),device_id);
                    }

                    //

                    stu.img_url=jObj.getString("cPicFile");
                    stu.reg_year=jObj.getString("cRegYear");
                    stu.sch_term=jObj.getString("currentTerm");
                    stu.balance=Float.valueOf(String.format("%.2f",(jObj.getDouble("Balance"))));
                    double ovr=(jObj.getDouble("_OverallAvg"));
                    stu._OverallAvg=String.valueOf(value.format(ovr));
                    stu.grade_level=jObj.getString("gradeLevel");
                    stu.homework_count=Integer.valueOf(jObj.getInt("homework_count"));
                    stu.message_count=Integer.valueOf(jObj.getInt("message_count"));

                    msg_total_count+=stu.message_count;

                    JSONArray classroom_array=new JSONArray(jObj.getString("Classrooms"));
                    if (classroom_array.length()>0){
                        JSONObject classR=classroom_array.getJSONObject(0);
                        stu._classroom.className=classR.getString("cClassroomName");
                        stu._classroom.classID=classR.getInt("cClassroomID");

                        //subscribe to each classroom discussion as Topic;
                        boolean all_subscribed= prefs.getBoolean("student_all_subscribed",false);
                        if (!all_subscribed) {
                            Log.e("student_subscribe",String.valueOf(all_subscribed));
                            _utility.subscribe_topic(stu.domain + "-class-" + String.valueOf(stu._classroom.classID));
                            editor.putBoolean("student_all_subscribed",true);
                            editor.commit();
                            editor.apply();
                        }
                     }

                   // Log.e("jon student",stu.fullname);

                    sblings.add(stu);
                }

                sibling_Adapter=new siblingAdapter(sblings,getApplicationContext());



                sibling_Adapter.notifyDataSetChanged();
                lv.setAdapter(sibling_Adapter);


                //   view.notify();
            }catch (Exception e)
            {
                Log.e("error",e.getMessage());
            }finally {
              // progressDialog.dismiss();
                txt_msg_count.setText(String.valueOf(msg_total_count));
            }
        }
    }

    private class send_device_id extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        String domain,schyear,apikey,schterm="";

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {


                int studentid=0;
                String device_id=params[1];
                String object_id=params[0];

                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                domain=prefs.getString("domain","-");
                apikey=prefs.getString("apikey","");
                URL url=new URL("https://api2.gradelogics.com/api/device/register/" +object_id + "/" + device_id + "/" + domain + "/" + apikey);

                // URL url=new URL("https://api2.gradelogics.com/api/utility/broadcasts/2012279/stjoseph/");
                Log.e("url",url.toString());
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
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String res)
        {
            //subscribe to ALLStudents Topic;
            _utility.subscribe_topic(domain+"-all-students");

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        sblings=new ArrayList<>();
      //  sibling_Adapter.notifyDataSetChanged();
        new getSiblings().execute("");
    }
}
