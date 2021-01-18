package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link classMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class classMessagesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected int class_discussion_unread;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView msgList;
    teacher meTeacher;

    public classMessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment classMessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static classMessagesFragment newInstance(String param1, String param2) {
        classMessagesFragment fragment = new classMessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_class_messages, container, false);

        msgList=(ListView)root.findViewById(R.id.lv);

        SharedPreferences prefs=getActivity().getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        meTeacher=gson.fromJson(json,teacher.class);

        final SwipeRefreshLayout _swly=(SwipeRefreshLayout)root.findViewById(R.id.swly);
        _swly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new get_unread_discussions().execute("");
                _swly.setRefreshing(false);
            }
        });
       //

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
        new get_unread_discussions().execute("");
        //INSERT CUSTOM CODE HERE
    }


    private class get_unread_discussions extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        // ProgressDialog progressDialog=new ProgressDialog(messageFragment.this);

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=getActivity().getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");
                Gson gson = new Gson();
               // String json = prefs.getString("student_object", "");
                //student stuobj = gson.fromJson(json, student.class);
                int active_class=prefs.getInt("active_class",-1);

                URL url=new URL("https://api2.gradelogics.com/api/student/discussion_trail_unread_count/class/" + String.valueOf(active_class) + "/" + meTeacher.id + "/" + meTeacher.domain + "/" + apikey);

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


        @Override
        protected void onPostExecute(String res)
        {

            try {
                class_discussion_unread=Integer.valueOf(res);
                Log.e("discussion_count",res);
            }catch (Exception e)
            {
                Log.e("load msg exception",e.toString());
            }finally {
                // progressDialog.dismiss();
                new loadMessages().execute("");
            }
        }
    }
    private class loadMessages extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        String active_class_name;
       // ProgressDialog progressDialog=new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Getting messages",Toast.LENGTH_SHORT).show();
           // progressDialog.setTitle("Loading");
           // progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=getContext().getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");
                active_class_name=prefs.getString("active_class_name","");
                URL url=new URL("https://api2.gradelogics.com/api/teacher/messages/" + String.valueOf(meTeacher.id) + "/" + meTeacher.domain + "/" + apikey);

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

             Log.e(" teacher msgs",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final ArrayList<msgObg> dataSet=new ArrayList<msgObg>();
            try {

                res=res.replace("<br/>","\n");
                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=getContext().getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {

                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    Log.e("msgbody",jObj.getString("mesg_body"));
                    Log.e("msgbody",String.valueOf(jObj.getInt("mesg_id")));
                    msgObg msgB=new msgObg(jObj.getString("mesg_date_string"),jObj.getString("mesg_id"),jObj.getString("mesg_body"),jObj.getString("mesg_from"),jObj.getBoolean("mesg_read"),jObj.getInt("mesg_type"));
                    //  Log.e("msgbody",jObj.getString("mesg_body"));
                    //subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    dataSet.add(msgB);
                    //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());
                dataSet.add(0,new msgObg(String.valueOf(class_discussion_unread),"-2","Parent/Teacher discussions",active_class_name +" Discussion Board",false,1));

                messageAdapter grdAdapter=new messageAdapter(dataSet,getContext().getApplicationContext());
                msgList.setAdapter(grdAdapter);


                synchronized(grdAdapter){
                    grdAdapter.notify();
                }
                //  grdAdapter.notify();
            }catch (Exception e)
            {
                Log.e("load msg exception",e.toString());
            }finally {
                // loadUI();
              //  progressDialog.dismiss();
            }
        }
    }

}
