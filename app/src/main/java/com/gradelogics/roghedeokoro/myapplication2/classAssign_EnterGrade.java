package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class classAssign_EnterGrade extends AppCompatActivity {

    TextView txtTitle,txtSubject,txtStandard,txtCategory,txtGradebook,txtClass,txtImpact,txtDate;
    teacher meTeacher;
    RecyclerView studentList;
    Intent intent;
    SharedPreferences prefs;
    gradeBook gB;
    int maxS;
    int assignmentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_assign__enter_grade);

        prefs=getSharedPreferences("gradelogics",0);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        meTeacher=gson.fromJson(json,teacher.class);
        String gbjson=prefs.getString("active_gradebook","");
        gB=gson.fromJson(gbjson,gradeBook.class);

         intent=getIntent();

        txtTitle=(TextView)findViewById(R.id.txtGradeTitle);
        txtSubject=(TextView)findViewById(R.id.txtSubject);
        txtCategory=(TextView)findViewById(R.id.txtCategory);
        txtGradebook=(TextView)findViewById(R.id.txtSchYear);
        txtDate=(TextView)findViewById(R.id.txtGradeDate);
        studentList=(RecyclerView) findViewById(R.id.studentList);

        txtTitle.setText(intent.getStringExtra("assign_title"));
        txtSubject.setText(intent.getStringExtra("assign_subject_name"));
        txtCategory.setText( intent.getStringExtra("assign_category_name"));
        txtDate.setText(intent.getStringExtra("assign_date"));
        txtGradebook.setText(gB.gradebookName);

        maxS=Integer.valueOf(intent.getStringExtra("assign_max"));
Log.e("maxSc",String.valueOf(maxS));
        new get_students().execute(String.valueOf(intent.getStringExtra("assign_subject_id")),String.valueOf(intent.getStringExtra("assign_class_id")));

        FloatingActionButton fa_save=(FloatingActionButton)findViewById(R.id.as_save);
        fa_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new saveAssignment().execute("");
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Assign Grades - Step 2 of 2");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classAssign_EnterGrade.super.onBackPressed();
            }
        });

    }


    protected class saveAssignment extends AsyncTask<String,Void,String>
    {
        String gradecategory="";
        String gradetitle="";
        String duedate="";
        String classid="";
        ProgressDialog progress;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
             progress = new ProgressDialog(classAssign_EnterGrade.this);
            progress.setTitle("Creating");
            progress.setMessage("Grade Assignment");
            progress.show();


        }

        @Override
        protected String doInBackground(String... params) {

            String str="https://api2.gradelogics.com/api/assignment/save/"+ gB.ID +"/0/" + intent.getStringExtra("assign_title") + "/" + intent.getStringExtra("assign_class_id") +"/" + intent.getStringExtra("assign_subject_id") + "/" + intent.getStringExtra("assign_standard_id")+ "/" + intent.getStringExtra("assign_category_name") + "/" + intent.getStringExtra("assign_date").replace("/","-") + "/" + intent.getBooleanExtra("assign_impact",false) + "/" + meTeacher.domain + "/" + meTeacher.api_key;
           // str=str.replace(" ","%20");
            Log.e("Post string",str);
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }

                return (stringBuffer.toString());
            }
            catch(Exception ex)
            {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        protected void onPostExecute(String result) {
            Log.e("Return ID",result);

            JSONObject json;
            try {
                json = new JSONObject(result);
                SharedPreferences.Editor editor=prefs.edit();
              //  editor.putString("assignmentid",json.getString("Value"));
               // editor.commit();
                assignmentID=Integer.valueOf(json.getString("response"));
               // Log.e("AssignmentID",json.getString("Value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ////traverse list and post grades

           RecyclerView listv=(RecyclerView) findViewById(R.id.studentList);

            for (int childCount = listv.getChildCount(), i = 0; i < childCount; ++i) {
                final RecyclerView.ViewHolder holder = listv.getChildViewHolder(listv.getChildAt(i));
                TextView fs=holder.itemView.findViewById(R.id.stu_final_score);

                enter_grades_adapter rc_adap=(enter_grades_adapter) listv.getAdapter();
                student std=rc_adap.get_student(i);
                //Log.e(String.valueOf(std.id),fs.getText().toString());

                if (!std.score.equals("-"))
                {
                    Log.e("save this grade",std.id + " == "  + String.valueOf(std.score));
                    new post_grades().execute(String.valueOf(std.id),std.score);
                }
             }

          /*  for (int x=0;x<listv.getChildCount();x++)
            {
                View v=listv.getAdapter().getView(x,null,null);
                studentAssignAdapter.ViewHolder r=(studentAssignAdapter.ViewHolder)v.getTag();
                student t= (student) listv.getAdapter().getItem(x);

                Log.e("stu score",t.score);


                if (!t.score.equals("-"))
                {
                    Log.e("save this grade",t.id + " == "  + String.valueOf(t.score));
                    new post_grades().execute(String.valueOf(t.id),t.score);
                }
            }

           */

            progress.dismiss();
            Intent teacher_main=new Intent(getApplicationContext(),Teacher_dash.class);
            teacher_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            teacher_main.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(teacher_main);
            finish();

        }
    }

    protected class post_grades extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

       // ProgressDialog progressBar=new ProgressDialog(classAssign_EnterGrade.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // progressBar.setMessage("Please wait...");
           // progressBar.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String result="";
            try
            {
                String studentID=params[0];
                String student_score =params[1];

                URL url=new URL ("https://api2.gradelogics.com/api/assignment/grade/save/" + assignmentID + "/" + studentID + "/" + student_score + "/"  + intent.getStringExtra("assign_subject_id") +"/" + intent.getBooleanExtra("assign_impact",false) +"/" + gB.ID + "/" + meTeacher.id + "/" + meTeacher.domain +"/" + meTeacher.api_key);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream is=new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader=new BufferedReader(new InputStreamReader(is));

                String line;
                while((line=reader.readLine()) != null){
                    result=line;
                }


            }catch (Exception ex)
            {
                Log.e("assign er",ex.getMessage());
            }finally {
                urlConnection.disconnect();
            }
            Log.e("post_grade",result);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                JSONObject jObj=new JSONObject(result);
                String response=jObj.getString("response");

            }catch (Exception ex)
            {
                try {
                    throw ex;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("assign students error",ex.getMessage());
            }finally {
               // progressBar.dismiss();
            }
        }
    }

    protected class get_students extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        ProgressDialog progressBar=new ProgressDialog(classAssign_EnterGrade.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setMessage("Please wait...");
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
                Log.e("assign er",ex.getMessage());
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
               ArrayList<student> class_students=new ArrayList<>();

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

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                studentList.setLayoutManager(mLayoutManager);
               // studentList.setItemAnimator(new DefaultItemAnimator());

              enter_grades_adapter  classStudentAdapter=new enter_grades_adapter(class_students,getApplicationContext(),maxS);
                classStudentAdapter.notifyDataSetChanged();
                studentList.setAdapter(classStudentAdapter);

            }catch (Exception ex)
            {
                try {
                    throw ex;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("assign students error",ex.getMessage());
            }finally {
                progressBar.dismiss();
            }
        }
    }
}
