package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
 * Activities that contain this fragment must implement the
 * {@link subjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link subjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class subjectFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView sbjList;


    private OnFragmentInteractionListener mListener;

    public subjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment subjectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static subjectFragment newInstance(String param1, String param2) {
        subjectFragment fragment = new subjectFragment();
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
        View root=inflater.inflate(R.layout.fragment_subject, container, false);

        sbjList=(ListView)root.findViewById(R.id.lv);

       final SwipeRefreshLayout _swly=(SwipeRefreshLayout)root.findViewById(R.id.swly);
        _swly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new loadSubjects().execute("");
                _swly.setRefreshing(false);
            }
        });
      //  new loadSubjects().execute("");
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
          new loadSubjects().execute("");
        //INSERT CUSTOM CODE HERE
    }

    private class loadSubjects extends AsyncTask<String,String,String>
    {
        student stuobj;
        Gson gson = new Gson();

        HttpURLConnection urlConnection;
      //  ProgressDialog progressDialog=new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"This will take only a sec!",Toast.LENGTH_SHORT).show();
           // progressDialog.setTitle("This will take only a sec!");
           // progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";

                int studentid=0;
                SharedPreferences prefs=subjectFragment.this.getContext().getSharedPreferences("gradelogics",MODE_PRIVATE);

                String json = prefs.getString("student_object", "");
                stuobj = gson.fromJson(json, student.class);

                studentid=(prefs.getInt("student_id",0));
                domain=prefs.getString("domain","-");
                schyear=prefs.getString("reg_year","-");
                schterm=prefs.getString("school_term","-");
                apikey=prefs.getString("apikey","");

                domain=stuobj.domain;
                schyear=stuobj.reg_year;
                schterm=stuobj.sch_term;

                URL url=new URL("https://api2.gradelogics.com/api/utility/subjects/" + studentid + "/" + domain + "/" + schyear + "/" + schterm + "/" + apikey);
                urlConnection=(HttpURLConnection)url.openConnection();

                //Log.e("subject url",url.toString());

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

             Log.e("subject json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final ArrayList<subject> dataSet=new ArrayList<subject>();
            try {


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=subjectFragment.this.getContext().getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    dataSet.add(nSubject);
                    //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());

                subjectAdapter sbjAdapter=new subjectAdapter(dataSet,subjectFragment.this.getContext());
                sbjList.setAdapter(sbjAdapter);

                sbjList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                        subject sbj=dataSet.get(pos);
                        Log.e("Subject ", sbj.subjectName);

                        Intent i = new Intent(subjectFragment.this.getContext(), activity_grades.class);
                        i.putExtra("subjectid", sbj.id);
                        i.putExtra("subjectname", sbj.subjectName);
                        startActivity(i);
                    }
                });

                sbjAdapter.notify();
            }catch (Exception e)
            {

            }finally {
              // progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
