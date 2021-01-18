package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
 * Use the {@link classStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class classStudentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView studentList;
    teacher meTeacher;
    TextView my_name,sch_name,sch_year,sch_term;
    Spinner subjectS,classS;
    ArrayList<student> class_students;
    int active_subject,active_class=-1;
    gradeBook active_gradebook;
    class_studentAdapter classStudentAdapter;
    String current_view="view_student";
    public classStudentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment classStudentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static classStudentsFragment newInstance(String param1, String param2) {
        classStudentsFragment fragment = new classStudentsFragment();
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
        View root=inflater.inflate(R.layout.fragment_class_students, container, false);


        studentList=(ListView)root.findViewById(R.id.lv);
        SharedPreferences prefs=getActivity().getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        active_subject= prefs.getInt("active_subject",-1);
        active_class=prefs.getInt("active_class",-1);

        active_gradebook=gson.fromJson(prefs.getString("active_gradebook",""),gradeBook.class);
        meTeacher=gson.fromJson(json,teacher.class);

        class_students=new ArrayList<>();

        final SwipeRefreshLayout _swly=(SwipeRefreshLayout)root.findViewById(R.id.swly);
        _swly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new get_students().execute(String.valueOf(active_subject),String.valueOf(active_class));
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
        new get_students().execute(String.valueOf(active_subject),String.valueOf(active_class));
        //INSERT CUSTOM CODE HERE
    }
    protected class get_students extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

       // ProgressDialog progressBar=new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // progressBar.setMessage("Please wait...");
           // progressBar.show();
            Toast.makeText(getContext(),"Loading Student Records",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            String result="";
            try
            {


                URL url=new URL ("https://api2.gradelogics.com/api/classroom/students/" + params[1] + "/" + active_gradebook.gradebookYear + "/" + active_gradebook.gradebookTerm + "/" + params[0] + "/" + meTeacher.domain +"/" +meTeacher.api_key);
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
            Log.e("classroom stud",result);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                class_students=new ArrayList<>();

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

                classStudentAdapter=new class_studentAdapter(class_students,getContext(),current_view);
                classStudentAdapter.notifyDataSetChanged();
                studentList.setAdapter(classStudentAdapter);

            }catch (Exception ex)
            {
                Log.e("classstudents error",ex.getMessage());
            }finally {
              //  progressBar.dismiss();
            }
        }
    }
}
