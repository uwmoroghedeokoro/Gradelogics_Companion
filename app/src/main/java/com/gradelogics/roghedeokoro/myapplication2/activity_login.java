package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class activity_login extends AppCompatActivity {

    EditText edt_student_id,edt_password;
    Button signme_btn;
    TextView txtErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");


        Log.e("stud Id",json);

        if (!json.equals("")) {
            try {
                JSONObject jObject = new JSONObject(json);
                Toast.makeText(getApplicationContext(),"Welcome!",Toast.LENGTH_LONG).show();
                if (jObject.getString("object_type").equals("student")) {
                    SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
                    editor.putString("domain", jObject.getString("domain"));
                    editor.commit();
                    editor.apply();
                    // Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    Intent intent = new Intent(getApplicationContext(), main_dash.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                   
                }else if(jObject.getString("object_type").equals("teacher")){
                    Log.e("login_object",jObject.getString("object_type"));
                    Intent intent = new Intent(getApplicationContext(), Teacher_dash.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);

                   // ActivityCompat.finishAffinity(activity_login.this);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        edt_student_id=(EditText)findViewById(R.id.edt_studentid);
        edt_password=(EditText)findViewById(R.id.edt_pwd);
        signme_btn=(Button)findViewById(R.id.btn_login);
        txtErr=(TextView)findViewById(R.id.txt_error);

        signme_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stuid=edt_student_id.getText().toString();
                String stuPwd=edt_password.getText().toString();

                new loginTask().execute(stuid,stuPwd);
            }
        });
    }


    private class getSiblings extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,schterm="";
                int studentid=0;
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                studentid=(prefs.getInt("student_id",0));
                domain=prefs.getString("domain","-");
              //  schyear=prefs.getString("reg_year","-");
              //  schterm=prefs.getString("school_term","-");

               // domain=stuobj.domain;
               // schyear=stuobj.reg_year;
               // schterm=stuobj.sch_term;

                URL url=new URL("https://api2.gradelogics.com/api/student/siblings/"+ String.valueOf(studentid) +"/"+ domain);
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

             Log.e("sibling json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final StringBuilder sibling_pics=new StringBuilder();
            final StringBuilder sibling_ids=new StringBuilder();
            final StringBuilder sibling_pwd=new StringBuilder();
           // final Arra
            try {


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                   // subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                   // dataSet.add(nSubject);
                    sibling_pics.append(jObj.getString("ProfilePic")).append(",");
                    sibling_ids.append(jObj.getString("StudentID")).append(",");
                    sibling_pwd.append(jObj.getString("pwd")).append(",");
                    //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());

                editor.putString("sibling_pics",sibling_pics.toString());
                editor.putString("sibling_ids",sibling_ids.toString());
                editor.putString("sibling_pwd",sibling_pwd.toString());

                editor.commit();
                //call main activity
                //Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                Intent intent=new Intent(getApplicationContext(),main_dash.class);
               startActivity(intent);

             //   view.notify();
            }catch (Exception e)
            {

            }finally {
                // loadUI();
            }
        }
    }

    private class loginTask extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        ProgressDialog progressDialog=new ProgressDialog(activity_login.this);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.setTitle("Signing In");
            progressDialog.setMessage("This will take only a sec!");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            Log.e("action","login");
            String userid=params[0];
            String pwd=params[1];
            try {
                URL url=new URL("https://api2.gradelogics.com/api/student/login/" + userid + "/" + pwd);
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

            Log.e("json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            try {
                JSONObject jObject = new JSONObject(res);
                SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();

                //check if student
                if (jObject.getString("object_type").equals("student")) {
                    if (jObject.getInt("cStudentID") > 0) {
                        student Student = new student();
                        Student.id = jObject.getInt("cStudentID");
                        Student.reg_year = jObject.getString("cRegYear");
                        Student.sch_term = jObject.getString("currentTerm");
                        Student.fullname = jObject.getString("cFirstname") + ' ' + jObject.getString("cLastname");
                        Student.department = jObject.getString("cDepartmentName");
                        Student.term_avg = String.valueOf(jObject.getLong("termAvg"));
                        Student.balance = jObject.getLong("Balance");
                        Student.excel = jObject.getInt("excel");
                        Student.struggle = jObject.getInt("struggle");
                        Student.domain = jObject.getString("ConnString");


                        // Student.
                        //   Log.e("domain",jObject.getString("ConnString"));
                        editor.putInt("student_id", jObject.getInt("cStudentID"));
                        editor.putString("object_type", "student");
                        editor.putString("domain", jObject.getString("ConnString"));

                        editor.putString("apikey", jObject.getString("api_key"));

                        //load profile data
                        JSONObject tmpObj = new JSONObject(jObject.getString("Parent"));

                        Student.Contact.mother = tmpObj.getString("Mother");
                        Student.Contact.father = tmpObj.getString("Father");

                        // editor.putString("mother",(tmpObj.getString("Mother")));
                        // editor.putString("father",tmpObj.getString("Father"));

                        tmpObj = new JSONObject(jObject.getString("ParentContact"));

                        Student.Contact.email = tmpObj.getString("Email");
                        Student.Contact.workp = tmpObj.getString("PhoneW");
                        Student.Contact.homep = tmpObj.getString("PhoneM");

                        // editor.putString("email",tmpObj.getString("Email"));
                        // editor.putString("workph",tmpObj.getString("PhoneW"));
                        // editor.putString("cellph",tmpObj.getString("PhoneM"));

                        tmpObj = new JSONObject(jObject.getString("Address"));

                        Student.Contact.address = tmpObj.getString("StreetAddress");
                        Student.Contact.city = tmpObj.getString("City");

                        // editor.putString("address",tmpObj.getString("StreetAddress"));
                        // editor.putString("city",tmpObj.getString("City"));
                        ///

                        // editor.putString("classes",jObject.getString("Classes"));
                        // Log.e("stuID",String.valueOf(jObject.getInt("cStudentID")));
                        //get school info
                        JSONObject schObject = (jObject.getJSONObject("schoolInfo"));
                        // Log.e("sch json",schObject.toString());

                        Student.school_name = schObject.getString("SchoolName");
                        Student.school_logo="http://www.gradelogics.com/files/" + schObject.getString( "Logo" );
                        student.contact mContact = Student.Contact;
                        //Log.e("reading contact mother",mContact.mother);
                        // editor.putString("school_name",schObject.getString("SchoolName"));
                        // editor.putString("balance",jObject.getString("Balance"));


                        Gson gson = new Gson();
                        String json = gson.toJson(Student);
                        editor.putString("student_object", json);
                        editor.putString("login_object", json);
                        json = gson.toJson(mContact);
                        editor.putString("contact_object", json);

                        JSONArray classroom_array=new JSONArray(jObject.getString("Classrooms"));
                        if (classroom_array.length()>0){
                            JSONObject classR=classroom_array.getJSONObject(0);
                            classroom active_class=new classroom();
                            active_class.className=classR.getString("cClassroomName");
                            active_class.classID=classR.getInt("cClassroomID");
                            json=gson.toJson(active_class);
                            editor.putString("active_class", json);

                            //subscribe to FCM topic for each class discussion


                        }

                        editor.commit();

                        editor.putString("profile_pic", jObject.getString("cPicFile"));
                        editor.apply();

                        // call siblings
                        //  new getSiblings().execute("");

                        //  SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
                        editor.putString("domain", Student.domain);
                        editor.putString("schName", Student.school_name);
                        editor.commit();
                        editor.apply();
                        // Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        Intent intent = new Intent(getApplicationContext(), main_dash.class);
                        startActivity(intent);

                    } else {
                        //login has failed
                        txtErr.setText("Login has failed. Please confirm GradelogicsID and Password");
                        Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_LONG).show();
                    }
                }else
                    if (jObject.getString("object_type").equals("teacher"))
                    {
                        if (jObject.getInt("cTeacherID") > 0) {
                            teacher Teacher = new teacher();
                            editor.putString("object_type", "teacher");
                            Teacher.id = jObject.getInt("cTeacherID");
                            Teacher.reg_year = jObject.getString("schYear");
                            Teacher.sch_term = jObject.getString("schTerm");
                            Teacher.fullname = jObject.getString("cFullname");
                            Teacher.department = jObject.getString("cDepartmentName");
                            Teacher.img_url = jObject.getString("cPicFile");
                            Teacher.domain = jObject.getString("ConnString");
                            Teacher.api_key=jObject.getString("api_key");
                            Teacher.message_count=jObject.getInt("msgCount");
                            // Student.
                            //   Log.e("domain",jObject.getString("ConnString"));
                            editor.putInt("teacher_id", jObject.getInt("cTeacherID"));
                            editor.putString("domain", jObject.getString("ConnString"));
                         //   editor.putString("object_type", "student");
                            editor.putString("apikey", jObject.getString("api_key"));

                            //load classrooms data
                            JSONArray classroom_array=new JSONArray(jObject.getString("cMyClasses"));
                            for (int x=0;x<classroom_array.length();x++) {
                                // Log.e("array el",jArray.getJSONObject(x).toString());
                                JSONObject cObj = classroom_array.getJSONObject(x);
                                classroom classR=new classroom();
                                classR.classID=cObj.getInt("ClassroomID");
                                classR.classDepartment=cObj.getString("DepartmentName");
                                classR.className=cObj.getString("ClassroomName");
                                Teacher.classrooms.add(classR);
                            }

                            //load gradeScale data
                            JSONArray gradescale_array=new JSONArray(jObject.getString("gradeScale"));
                            for (int x=0;x<gradescale_array.length();x++) {
                                // Log.e("array el",jArray.getJSONObject(x).toString());
                                JSONObject cObj = gradescale_array.getJSONObject(x);
                                GradeScale gradeS=new GradeScale();
                                gradeS.gradeChar=cObj.getString("cGrade");
                                gradeS.gradeMin=Float.valueOf(cObj.getString("cMinimum"));

                                Teacher.gradeScale.add(gradeS);
                            }


                            //load gradebook data
                            JSONArray gradebook_array=new JSONArray(jObject.getString("Gradebooks"));
                            for (int x=0;x<gradebook_array.length();x++) {
                                // Log.e("array el",jArray.getJSONObject(x).toString());
                                JSONObject cObj = gradebook_array.getJSONObject(x);
                                gradeBook grade_book=new gradeBook();
                                grade_book.ID=cObj.getInt("cGradebookID");
                                grade_book.gradebookName=cObj.getString("cGradebookName");
                                grade_book.gradebookYear=cObj.getString("cYear");
                                grade_book.gradebookTerm=cObj.getInt("cTermID");
                                Teacher.gradeBooks.add(grade_book);
                            }

                            //load subjects data
                            JSONArray subjects_array=new JSONArray(jObject.getString("cMySubjects"));
                            for (int x=0;x<subjects_array.length();x++) {
                                // Log.e("array el",jArray.getJSONObject(x).toString());
                                JSONObject sObj = subjects_array.getJSONObject(x);
                                subject classS=new subject();
                                classS.id=String.valueOf(sObj.getInt("cSubjectID"));
                                classS.subjectName=sObj.getString("cSubjectName");

                                Teacher.subjects.add(classS);
                            }

                            JSONObject schObject = (jObject.getJSONObject("schoolInfo"));
                            // Log.e("sch json",schObject.toString());

                            Teacher.school_name = schObject.getString("SchoolName");
                            Teacher.school_logo="http://www.gradelogics.com/files/" + schObject.getString( "Logo" );

                            Gson gson = new Gson();
                            String json = gson.toJson(Teacher);
                            editor.putString("teacher_object", json);
                            editor.putString("login_object", json);

                            editor.commit();

                            editor.putString("profile_pic", jObject.getString("cPicFile"));
                            editor.apply();

                            // call siblings
                            //  new getSiblings().execute("");

                            //  SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
                            editor.putString("domain", Teacher.domain);
                            editor.putString("schName", Teacher.school_name);
                            editor.commit();
                            editor.apply();
                            // Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            Intent intent = new Intent(getApplicationContext(), Teacher_dash.class);
                            startActivity(intent);

                        } else {
                            //login has failed
                            txtErr.setText("Login has failed. Please confirm GradelogicsID and Password");
                            Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                }catch(Exception e)
                {
                    Log.e("login error",e.getMessage());
                    Toast.makeText(getApplicationContext(),"Login failed. Please confirm ID and Password",Toast.LENGTH_LONG).show();
                }finally{
                    progressDialog.dismiss();
                }

        }
    }

}
