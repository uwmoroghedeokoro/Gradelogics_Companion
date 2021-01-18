package com.gradelogics.roghedeokoro.myapplication2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class activity_teacher_attendance extends AppCompatActivity {
    Spinner subjectS,classS,standardS,categoryS;
    int active_subject,active_class=-1;
    StringWithTag selected_subject,selected_class,selected_standard,selected_category;
    teacher meTeacher;
    final Calendar myCalendar = Calendar.getInstance();
    RecyclerView studentList;
    boolean userIsInteracting;

    String attend_date="";
    int attend_period;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        SharedPreferences prefs=getSharedPreferences("gradelogics",0);
        final SharedPreferences.Editor editor=prefs.edit();
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        active_subject= prefs.getInt("active_subject",-1);
        active_class=prefs.getInt("active_class",-1);
        meTeacher=gson.fromJson(json,teacher.class);

        studentList=(RecyclerView) findViewById(R.id.studentList);

        subjectS=(Spinner)findViewById(R.id.spin_attend_period);
        classS=(Spinner)findViewById(R.id.spin_attend_class);


        final EditText assign_date=(EditText)findViewById(R.id.edt_attend_date);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                assign_date.setText(sdf.format(myCalendar.getTime()));
                attend_date=sdf.format(myCalendar.getTime());
            }

        };

        assign_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(activity_teacher_attendance.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        classS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (userIsInteracting) {
                    StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                    active_class=s.tag;
                 //   editor.putInt("active_class", s.tag);
                  //  editor.apply();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (userIsInteracting) {
                    StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                    attend_period=s.tag;
                    Log.e("period",String.valueOf(s.tag));
                    if (active_class<1 || attend_date.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"Attendance Date and Class required",Toast.LENGTH_SHORT).show();
                    }else
                        new get_students().execute(String.valueOf(active_subject),String.valueOf(active_class));
                   // new get_students().execute(String.valueOf(active_subject),String.valueOf(active_class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Record Attendance");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        // getSupportActionBar().setTitle("New Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_teacher_attendance.super.onBackPressed();
            }
        });
        loadGUI();
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
         userIsInteracting = true;
    }

    private void loadGUI()
    {
        ArrayList<StringWithTag> subjectlist=new ArrayList<>();
        ArrayList<StringWithTag>classroomlist=new ArrayList<>();

        // StringWithTag blank_subject = new StringWithTag();

        subjectlist.add(new StringWithTag("",-1));

        for (int x=1;x<=2;x++
        ) {
            StringWithTag swt=new StringWithTag("Period " + String.valueOf(x),x);
            subjectlist.add(swt);
        }

        classroomlist.add(new StringWithTag("",-1));
        for (classroom classr:meTeacher.classrooms
        ) {
            StringWithTag swt=new StringWithTag(classr.className,classr.classID);
            classroomlist.add(swt);
        }


        ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_medium_whitish, subjectlist);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectS.setAdapter(adap);

        ArrayAdapter<StringWithTag> adap2 = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_medium_whitish, classroomlist);
        adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classS.setAdapter(adap2);


        SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
        //get students
       // selected_class=adap2.getItem(classS.getSelectedItemPosition());

        Gson gson=new Gson();

       // editor.putInt("active_subject",selected_subject.tag);
       // editor.putInt("active_class",selected_class.tag);
       // editor.apply();


        // new activity_teacher_new_msg.get_students().execute(String.valueOf(selected_subject.tag),String.valueOf(selected_class.tag));
    }

    protected class get_students extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        ProgressDialog progressBar=new ProgressDialog(activity_teacher_attendance.this);

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

//api/classroom/attendance_status/{classroomID}/{attend_date}/{period_id}/{scYear}/{scTerm}/{domain}/{apikey}
                URL url=new URL ("https://api2.gradelogics.com/api/classroom/attendance_status/" + params[1] + "/" + attend_date.replace("/","-") + "/" + attend_period + "/" + meTeacher.reg_year + "/" + meTeacher.sch_term + "/" + meTeacher.domain +"/" +meTeacher.api_key);
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
                    std.attend_status=jObj.getString("attendStatus");
                    class_students.add(std);
                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                studentList.setLayoutManager(mLayoutManager);
                // studentList.setItemAnimator(new DefaultItemAnimator());

                attendance_adapter  classStudentAdapter=new attendance_adapter(class_students,getApplicationContext(),attend_date,attend_period);
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
