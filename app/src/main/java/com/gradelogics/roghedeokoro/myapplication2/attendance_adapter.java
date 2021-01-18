package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class attendance_adapter extends RecyclerView.Adapter<attendance_adapter.MyViewHolder> {
    private ArrayList<student> dataSet;
    Context mContext;
    teacher cTeacher;
    String maxScore;
    String attend_date;
    int attend_period;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudentName;
        Button attp,atta,attt;
        Spinner spnGrade;
        TextView txtStudentFinalScore;
        CircleImageView cvImg;

        public MyViewHolder(View view) {
            super(view);
            txtStudentName = (TextView) view.findViewById(R.id.txt_student_name);
            attp = (Button) view.findViewById(R.id.att_p);
            atta = (Button) view.findViewById(R.id.att_a);
            attt = (Button) view.findViewById(R.id.att_t);

            cvImg=(CircleImageView)view.findViewById(R.id.img_pic);
            // dataSet.get(getAdapterPosition()).maxS=maxScore;

           attp.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dataSet.get(getAdapterPosition()).attend_status="P";
                   attp.setBackgroundResource(R.drawable.attend_box_green);
                   atta.setBackgroundResource(R.drawable.attend_box_white);
                   attt.setBackgroundResource(R.drawable.attend_box_white);
                   new update_attendance().execute(String.valueOf(dataSet.get(getAdapterPosition()).id),"1");
               }
           });
            atta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataSet.get(getAdapterPosition()).attend_status="A";
                    atta.setBackgroundResource(R.drawable.attend_box_green);
                    attp.setBackgroundResource(R.drawable.attend_box_white);
                    attt.setBackgroundResource(R.drawable.attend_box_white);
                    new update_attendance().execute(String.valueOf(dataSet.get(getAdapterPosition()).id),"2");
                }
            });
            attt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataSet.get(getAdapterPosition()).attend_status="T";
                    attt.setBackgroundResource(R.drawable.attend_box_green);
                    atta.setBackgroundResource(R.drawable.attend_box_white);
                    attp.setBackgroundResource(R.drawable.attend_box_white);
                    new update_attendance().execute(String.valueOf(dataSet.get(getAdapterPosition()).id),"3");
                }
            });
        }
    }


    protected class update_attendance extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... params) {

            String result="";
            try
            {
               SharedPreferences prefs=mContext.getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson=new Gson();
                String json=prefs.getString("login_object","");

                teacher meTeacher=gson.fromJson(json,teacher.class);
//api/teacher/take_attendance/{studentID}/{attendDate}/{attendStatus}/{periodID}/{sYear}/{sTerm}/{domain}/{apikey}
                URL url=new URL ("https://api2.gradelogics.com/api/teacher/take_attendance/" + params[0] + "/" + attend_date.replace("/","-") + "/" +params[1]+ "/" +attend_period + "/" + meTeacher.reg_year + "/" + meTeacher.sch_term + "/" + meTeacher.domain +"/" +meTeacher.api_key);
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
            Log.e("attendance result",result);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {


            }catch (Exception ex)
            {

                Log.e("assign students error",ex.getMessage());
            }finally {

            }
        }
    }

    public attendance_adapter(ArrayList<student> studentlist,Context cx,String attenddate,int periodid) {
        this.dataSet = studentlist;
        mContext=cx;
      attend_date=attenddate;
      attend_period=periodid;

      Log.e("atte Per",String.valueOf(attend_period));
        SharedPreferences prefs=cx.getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        cTeacher=gson.fromJson(json,teacher.class);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_enter_attendance, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        student std = dataSet.get(position);
        final int current_index=position;
        List<StringWithValue> gradeScalelist = new ArrayList<StringWithValue>();

        StringWithValue blank=new StringWithValue("-",-1);
        gradeScalelist.add(blank);

        viewHolder.txtStudentName.setText(std.fullname);
        viewHolder.attp.setBackgroundResource(R.drawable.attend_box_white);
        viewHolder.atta.setBackgroundResource(R.drawable.attend_box_white);
        viewHolder.attt.setBackgroundResource(R.drawable.attend_box_white);

        if (std.attend_status.equals("P"))
            viewHolder.attp.setBackgroundResource(R.drawable.attend_box_green);
        else if (std.attend_status.equals("A"))
            viewHolder.atta.setBackgroundResource(R.drawable.attend_box_green);
        else if (std.attend_status.equals("T"))
            viewHolder.attt.setBackgroundResource(R.drawable.attend_box_green);


        Picasso.get()
                .load(std.img_url)
                .fit()
                .centerInside()
                .into(viewHolder.cvImg);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public student get_student(int pos)
    {
        return dataSet.get(pos);
    }
}
