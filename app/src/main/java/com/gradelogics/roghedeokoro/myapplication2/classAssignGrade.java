package com.gradelogics.roghedeokoro.myapplication2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class classAssignGrade extends AppCompatActivity {
    Spinner subjectS,classS,standardS,categoryS;
    int active_subject,active_class=-1;
    StringWithTag selected_subject,selected_class,selected_standard,selected_category;
    teacher meTeacher;
    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_assign_grade);

        SharedPreferences prefs=getSharedPreferences("gradelogics",0);
        final SharedPreferences.Editor editor=prefs.edit();
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        active_subject= prefs.getInt("active_subject",-1);
        active_class=prefs.getInt("active_class",-1);
        meTeacher=gson.fromJson(json,teacher.class);

        subjectS=(Spinner)findViewById(R.id.spin_assign_subject);
        classS=(Spinner)findViewById(R.id.spin_assign_class);
        standardS=(Spinner)findViewById(R.id.spin_assign_standard);
        categoryS=(Spinner)findViewById(R.id.spin_assign_category);

        final EditText assign_title=(EditText)findViewById(R.id.edt_assign_title);
        final EditText assign_max=(EditText)findViewById(R.id.edt_assign_max);
        final Switch assign_impact=(Switch) findViewById(R.id.switch_assign_impact);

        final EditText assign_date=(EditText)findViewById(R.id.edt_assign_date);

        Button btn_next=(Button)findViewById(R.id.btn_continue);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean all_good=true;
                if(TextUtils.isEmpty(assign_date.getText()))
                {
                    all_good=false;
                    assign_date.setError("Date is required");
                    assign_date.setHint("Required");
                }
                if(TextUtils.isEmpty(assign_title.getText()))
                {
                    all_good=false;
                    assign_title.setError("Title is required");
                    assign_title.setHint("Required");
                }
                if(subjectS.getSelectedItemPosition()==0)
                {
                    all_good=false;
                    Toast.makeText(getApplicationContext(),"Please select a Subject",Toast.LENGTH_SHORT).show();
                    //subjectS.setErr("Date is required");
                  //  assign_date.setHint("Required");
                }

                if (all_good) {
                    Intent intent = new Intent(getApplicationContext(), classAssign_EnterGrade.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("assign_title", assign_title.getText().toString());
                    intent.putExtra("assign_subject_name", selected_subject.string);
                    intent.putExtra("assign_subject_id", String.valueOf(selected_subject.tag));
                    intent.putExtra("assign_class_name", selected_class.string);
                    intent.putExtra("assign_class_id", String.valueOf(selected_class.tag));
                    intent.putExtra("assign_max", assign_max.getText().toString());
                    intent.putExtra("assign_date", assign_date.getText().toString());
                    intent.putExtra("assign_impact", assign_impact.isChecked());
                    intent.putExtra("assign_standard_name", selected_standard.string);
                    intent.putExtra("assign_standard_id", String.valueOf(selected_standard.tag));
                    intent.putExtra("assign_category_name", selected_category.string);
                    intent.putExtra("assign_category_id", selected_category.tag);

                    startActivity(intent);
                }
            }
        });
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
            }

        };

        assign_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(classAssignGrade.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        subjectS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               selected_subject= (StringWithTag) parent.getItemAtPosition(position);
                {
                    editor.putInt("active_subject", selected_subject.tag);
                    active_subject=selected_subject.tag;
                    editor.apply();

                    new get_standards().execute(String.valueOf(active_subject));
                  //  Log.e("subject", "selected " + String.valueOf(s.tag));
                  //  new activity_teacher_new_msg.get_students().execute(String.valueOf(active_subject),String.valueOf(active_class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                   selected_class = (StringWithTag) parent.getItemAtPosition(position);
                    active_class=selected_class.tag;
                    editor.putInt("active_class", selected_class.tag);
                    editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        standardS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_standard = (StringWithTag) parent.getItemAtPosition(position);
               // active_class=selected_class.tag;
               // editor.putInt("active_class", selected_class.tag);
               // editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categoryS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_category = (StringWithTag) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Assign Grades - Step 1 of 2");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classAssignGrade.super.onBackPressed();
            }
        });

        loadGUI();


    }

    private void loadGUI()
    {
        ArrayList<StringWithTag> subjectlist=new ArrayList<>();
        ArrayList<StringWithTag>classroomlist=new ArrayList<>();

       // StringWithTag blank_subject = new StringWithTag();

        subjectlist.add(new StringWithTag("",-1));

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


        ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_black, subjectlist);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectS.setAdapter(adap);
//set selection
        for (int s=0;s<adap.getCount();s++){
            StringWithTag swT=adap.getItem(s);
            if (swT.tag==active_subject){
              //  subjectS.setSelection(s);
            }
        }

        ArrayAdapter<StringWithTag> adap2 = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_black, classroomlist);
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
       selected_subject=adap.getItem(subjectS.getSelectedItemPosition());
        selected_class=adap2.getItem(classS.getSelectedItemPosition());

        Gson gson=new Gson();

        editor.putInt("active_subject",selected_subject.tag);
        editor.putInt("active_class",selected_class.tag);
        editor.apply();


       // new activity_teacher_new_msg.get_students().execute(String.valueOf(selected_subject.tag),String.valueOf(selected_class.tag));
    }

    protected class get_standards extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        ProgressDialog progressBar=new ProgressDialog(classAssignGrade.this);

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


                URL url=new URL ("https://api2.gradelogics.com/api/subject/standards/" + params[0]  + "/" + meTeacher.domain +"/" +meTeacher.api_key);
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
            Log.e("subject standards",result);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                ArrayList<StringWithTag>standardsList=new ArrayList<>();
                ArrayList<StringWithTag>categoryList=new ArrayList<>();
                standardsList.add(new StringWithTag("None",-1));

                JSONObject jObj=new JSONObject(result);

                JSONArray standardsJsonArray=new JSONArray(jObj.getString("standards"));
                JSONArray categoryJsonArray=new JSONArray(jObj.getString("categories"));
                for (int x=0;x<standardsJsonArray.length();x++)
                {
                    JSONObject standObj=standardsJsonArray.getJSONObject(x);
                    StringWithTag swt=new StringWithTag(standObj.getString("Name"),standObj.getInt("ID"));
                      standardsList.add(swt);
                }

                ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_black, standardsList);
                adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                standardS.setAdapter(adap);

                //load categories
                for (int x=0;x<categoryJsonArray.length();x++)
                {
                    JSONObject catObj=categoryJsonArray.getJSONObject(x);
                    StringWithTag swt=new StringWithTag(catObj.getString("cCategory"),catObj.getInt("CategoryID"));
                    categoryList.add(swt);
                }

                ArrayAdapter<StringWithTag> adap2 = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_black, categoryList);
                adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoryS.setAdapter(adap2);

            }catch (Exception ex)
            {
                Log.e("standards error",ex.getMessage());
            }finally {
                progressBar.dismiss();
            }
        }
    }
}
