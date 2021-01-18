package com.gradelogics.roghedeokoro.myapplication2;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
 * Use the {@link classroomGradesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class classroomGradesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView gradesList;
    teacher meTeacher;
    ArrayList<grade> class_grades;
    int active_subject,active_class,active_gradebook=-1;
    classGradeAdapter class_grade_adapter;

    public classroomGradesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment classroomGradesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static classroomGradesFragment newInstance(String param1, String param2) {
        classroomGradesFragment fragment = new classroomGradesFragment();
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
        View root=inflater.inflate(R.layout.fragment_classroom_grades, container, false);

        gradesList=(ListView)root.findViewById(R.id.lv);
        SharedPreferences prefs=getActivity().getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        active_subject= prefs.getInt("active_subject",-1);
        active_class=prefs.getInt("active_class",-1);
        String selectedGB=prefs.getString("active_gradebook","");
        gradeBook sb=gson.fromJson(selectedGB,gradeBook.class);

        active_gradebook=sb.ID;

        meTeacher=gson.fromJson(json,teacher.class);

        class_grades=new ArrayList<>();

        final SwipeRefreshLayout _swly=(SwipeRefreshLayout)root.findViewById(R.id.swly);
        _swly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new get_students().execute(String.valueOf(active_class),String.valueOf(active_subject),String.valueOf(active_gradebook));
                _swly.setRefreshing(false);
            }
        });



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
        new get_students().execute(String.valueOf(active_class),String.valueOf(active_subject),String.valueOf(active_gradebook));
        //INSERT CUSTOM CODE HERE
    }

    protected class get_students extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {

            String result="";
            try
            {


                URL url=new URL ("https://api2.gradelogics.com/api/classroom/grades/" + params[0] + "/" + params[1]+ "/" + params[2]+ "/" + meTeacher.domain +"/" +meTeacher.api_key);
               Log.e("grades url",url.toString());
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
            Log.e("classroom grades",result);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                class_grades=new ArrayList<>();

                JSONArray jArray=new JSONArray(result);

                for (int x=0;x<jArray.length();x++)
                {
                    JSONObject jObj=jArray.getJSONObject(x);
                    grade std=new grade();
                    std.grade_title=jObj.getString("cAssignmentName");
                    std.category=jObj.getString("cAssignmentCategory");
                    std.grade_date=jObj.getString("cDueDate");
                    std.score=jObj.getString("gradeAvg");
                    std.className=jObj.getString("className");
                    std.grade_id=jObj.getInt("cAssignmentID");
                    std.subject=jObj.getString("subject_name");
                    class_grades.add(std);
                }

                class_grade_adapter=new classGradeAdapter(class_grades,getContext());
                class_grade_adapter.notifyDataSetChanged();
                gradesList.setAdapter(class_grade_adapter);

            }catch (Exception ex)
            {
                Log.e("classstudents error",ex.getMessage());
            }
        }
    }
}
