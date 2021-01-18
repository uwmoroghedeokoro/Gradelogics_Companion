package com.gradelogics.roghedeokoro.myapplication2;

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
import java.util.Calendar;

public class activity_transactions extends AppCompatActivity {

    ListView tranList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        tranList=(ListView)findViewById(R.id.tran_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Transactions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_transactions.super.onBackPressed();
            }
        });



        new loadtransactions().execute("");
    }


    private class loadtransactions extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,schterm="";
                int studentid=0;
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                student stuobj = gson.fromJson(json, student.class);


                URL url=new URL("http://gradelogics.com/api/api.asmx/transactions?domain=" + stuobj.domain + "&studentid="+ String.valueOf(stuobj.id) + "&schyear="+stuobj.reg_year);
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

            Log.e(" grades json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final ArrayList<transaction> dataSet=new ArrayList<transaction>();
            try {


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    transaction tran=new transaction();
                    tran.amt=jObj.getLong("TransAmt");
                    tran.desc=jObj.getString("TransDecr");
                    tran.type=jObj.getString("TransType");
                    String ackwardDate=jObj.getString("TransDate");
                    Calendar calendar = Calendar.getInstance();
                    String ackwardRipOff = ackwardDate.replace("/Date(", "").replace(")/", "");
                    Long timeInMillis = Long.valueOf(ackwardRipOff);
                    calendar.setTimeInMillis(timeInMillis);
                    System.out.println(calendar.getTime().toGMTString());
                    tran.date=calendar.getTime().toString();
Log.e("tran",jObj.getString("TransDecr"));
                    //subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    dataSet.add(tran);
                    //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());

                transacAdapter grdAdapter=new transacAdapter(getApplicationContext(),dataSet);
                tranList.setAdapter(grdAdapter);



                grdAdapter.notify();
            }catch (Exception e)
            {

            }finally {
                // loadUI();
            }
        }
    }
}
