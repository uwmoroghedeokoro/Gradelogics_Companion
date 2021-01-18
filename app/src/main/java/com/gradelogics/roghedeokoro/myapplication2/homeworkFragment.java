package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
 * Activities that contain this fragment must implement the
 * {@link homeworkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link homeworkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeworkFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView homeworkList;

    private OnFragmentInteractionListener mListener;

    public homeworkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeworkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeworkFragment newInstance(String param1, String param2) {
        homeworkFragment fragment = new homeworkFragment();
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
        View root=inflater.inflate(R.layout.fragment_homework, container, false);

        homeworkList=(ListView)root.findViewById(R.id.lv);
       final SwipeRefreshLayout _swly=(SwipeRefreshLayout)root.findViewById(R.id.swly);
        _swly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new loadHomework().execute("");
                _swly.setRefreshing(false);
            }
        });
       // new loadHomework().execute("");

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
        new loadHomework().execute("");
        //INSERT CUSTOM CODE HERE
    }

    private class loadHomework extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        //ProgressDialog progressDialog=new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
          //  progressDialog.setTitle("Loading");
           // progressDialog.show();
            Toast.makeText(getContext(),"Fetching Homework",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=homeworkFragment.this.getContext().getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                student stuobj = gson.fromJson(json, student.class);
                apikey=prefs.getString("apikey","");

                URL url=new URL("https://api2.gradelogics.com/api/utility/homework/" + String.valueOf(stuobj.id) + "/" + stuobj.domain + "/" + apikey);

                // URL url=new URL("https://api2.gradelogics.com/api/utility/homework/2013917/sav/");
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
            final ArrayList<homeworkObj> dataSet=new ArrayList<homeworkObj>();
            try {

                //  res="[{\"mesg_id\":3247,\"mesg_date\":\"2020-04-02T16:03:05.303\",\"mesg_body\":\"Good afternoon Parents/Guardians,<br/><br/>Here are a five (5) websites that you can use while school is out:<br/>https://learninghub.online<br/><br/>https://studio.code.org/courses<br/><br/>https://softschools.com<br/><br/>https://www.learninggamesforkids.com<br/><br/>https://www.coolmath.com<br/><br/>Regards,\"},{\"mesg_id\":2335,\"mesg_date\":\"2020-03-31T16:09:02.54\",\"mesg_body\":\"Good afternoon Parents/Guardians,<br/><br/>Please see the attached for your perusal. Thank you.<br/><br/>Regards,\"},{\"mesg_id\":2310,\"mesg_date\":\"2020-03-26T14:33:28.75\",\"mesg_body\":\"Good afternoon Parents/Guardians,<br/><br/>The Ministry of Education, Youth and Information wishes to advise that the closure of schools has been extended effective Friday, March 27, 2020 to Wednesday, April 8, 2020 after which schools will proceed on the Easter Holiday break with effect April 9, 2020 to Friday April 17, 2020. The scheduled resumption date is Monday, April 20, 2020.<br/><br/> Home Schooling remains paramount. To ensure that learning continues during the period of school closure, the Ministry has reengage its educational partners. Therefore, all the learning resources, programmes and initiatives will continue until further advised:<br/><br/>A PEP hotline has been activated to support questions related to PEP and other areas of education.<br/>Online resources for Grade levels 1-3 & 4-6, are accessible from some of our educational partners via the following links:<br/>? Book Fusion- https://www.bookfusion.com/<br/>? Learning Hub - https://learninghub.online/<br/>? EduFoca\"},{\"mesg_id\":2263,\"mesg_date\":\"2020-03-18T17:50:53.04\",\"mesg_body\":\"Good afternoon parents/Guardians of Grade 4-6 Students,<br/><br/>Please take note of the following: <br/>www.LearningHub.online is making school happen online at 9:00 a.m. - 1:00 p.m. daily. It’s FREE school must go in despite this Pandemic. Assignments will be posted in the Grades 4 - 6 chatroom. <br/><br/>° Monday Performance Task, Social Studies and Math<br/>° Tuesday Performance Task, Science and Language Arts<br/>° Wednesday Performance Task, Social Studies and Math<br/>° Thursday Performance Task, Science and Language Arts<br/>° Friday Writing \"},{\"mesg_id\":2252,\"mesg_date\":\"2020-03-18T07:30:26.547\",\"mesg_body\":\"Good morning Parents/Guardians,<br/><br/>Please see list of Online Educational Resources to be used as revision aids during the period of school closure: <br/><br/>www.ixl.com is going to be one of your most comprehensive sites for ALL grades and they offer a 30 day free trial ??<br/><br/>??????????<br/>??www.education.com<br/>??www.brainpop.com<br/>??www.brainpopjr.com<br/>??www.ixl.com <br/>??www.prodigygame.com<br/>www.xtramath.com<br/>www.khanacademy.com<br/>www.allinonehomeschool.com<br/>www.spellingcity.com <br/>www.starfall.com<br/>www.coolmath.com<br/>??www.funbrain.com <br/>www.learninggamesforkids.com<br/>www.abcmouse.com<br/>www.adventureacademy.com<br/>??www.readingeggs.com<br/>www.adaptedmind.com<br/>www.coursera.com<br/>www.abcya.com<br/>www.turtlediary.com<br/>www.grammaropolis.com<br/><br/>??????????<br/>Youtube Channels for core learning:<br/><br/>??Peekaboo Kids:<br/>https://www.youtube.com/user/Peekaboo<br/><br/>??Mathantics:<br/>https://youtu.be/RVYwunbpMHA<br/><br/\"},{\"mesg_id\":2242,\"mesg_date\":\"2020-03-16T17:57:48.507\",\"mesg_body\":\"Good afternoon Parents/Guardians,<br/><br/>Please see the attached for you perusal. Thank you.\"},{\"mesg_id\":2241,\"mesg_date\":\"2020-03-16T17:56:41.743\",\"mesg_body\":\"Good afternoon Parents/Guardians,<br/><br/>Please see the attached for your perusal. Thank you\"},{\"mesg_id\":2240,\"mesg_date\":\"2020-03-16T17:55:31.213\",\"mesg_body\":\"Good afternoon Parents/Guardians,<br/><br/>Please see the attached for your perusal. Thank you.\"},{\"mesg_id\":2238,\"mesg_date\":\"2020-03-16T16:42:11.183\",\"mesg_body\":\"Good afternoon Parents/Guardians,<br/><br/>Please see the following:<br/><br/>Youtube Channels for core learning:<br/><br/>??Peekaboo Kids:<br/>https://www.youtube.com/user/Peekaboo<br/><br/>??Mathantics:<br/>https://youtu.be/RVYwunbpMHA<br/><br/>??NumberRocks:<br/>https://youtu.be/wwekMIqb55s<br/><br/>??Alpha Blocks: https://www.youtube.com/channel/UC_qs3c0ehDvZkbiEbOj6Drg<br/><br/>??Math & Learning Videos 4 Kids:<br/>https://www.youtube.com/user/CommonCore4Kids<br/><br/>??Story Bots:<br/>https://www.youtube.com/user/storybots<br/><br/>??Homeschool Pop:<br/>https://www.youtube.com/channel/UCfPyVJEBD7Di1YYjTdS2v8g<br/><br/>??Its AumSum Time:<br/>https://www.youtube.com/user/Smartlearningforall<br/><br/>??Scratch Garden:<br/>https://www.youtube.com/user/ScratchGarden<br/><br/>??PBS Kids:<br/>https://www.youtube.com/channel/UCrNnk0wFBnCS1awGjq_ijGQ<br/><br/>????????????<br/>Youtube channels for stories, songs and crafts (good for preschoolers and kids crafty kids that may go stir crazy; \"},{\"mesg_id\":2237,\"mesg_date\":\"2020-03-16T16:36:54.963\",\"mesg_body\":\"Dear Parents/Guardians, <br/><br/>(Sunday 15th March, 2020) Please see list of Online Educational Resources to be used as revision aids during the period of school closure: www.ixl.com is going to be one of your most comprehensive sites for ALL grades and they offer a 30 day free trial ?? ?????????? <br/><br/>??www.education.com ??www.brainpop.com ??www.brainpopjr.com ??www.ixl.com ??www.prodigygame.com www.xtramath.com www.khanacademy.com www.allinonehomeschool.com www.spellingcity.com www.starfall.com www.coolmath.com ??www.funbrain.com www.learninggamesforkids.com www.abcmouse.com www.adventureacademy.com ??www.readingeggs.com www.adaptedmind.com www.coursera.com www.abcya.com www.turtlediary.com www.grammaropolis.com ?????????? Youtube Channels for core learning: ??Peekaboo Kids: https://www.youtube.com/user/Peekaboo ??Mathantics: https://youtu.be/RVYwunbpMHA ??NumberRocks: https://youtu.be/wwekMIqb55s ??Alpha Blocks: https://www.youtube.com/channel/UC_qs3c0ehDvZkbiEbOj6Drg\"}]";
                res=res.replace("<br/>","\n");
                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=homeworkFragment.this.getContext().getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {

                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                   // Log.e("msgbody",jObj.getString("cHomeworkDueDate"));
                   // Log.e("msgbody",String.valueOf(jObj.getInt("mesg_id")));

                    JSONArray attach_array=(jObj.getJSONArray(("attach_files")));
                    ArrayList<homeworkObj.doc>docs=new ArrayList<>();
                    for (int v=0;v<attach_array.length();v++)
                    {
                        JSONObject hDoc=attach_array.getJSONObject(v);
                        homeworkObj hme=new homeworkObj();
                        homeworkObj.doc tdoc=hme.new doc(hDoc.getString("DocumentName"),hDoc.getString("DocumentFile"));
                        docs.add(tdoc);
                    }
                    homeworkObj msgB=new homeworkObj(jObj.getString("cHomeworkDueDateString"),jObj.getString("cHomeworkID"),jObj.getString("cHomeworkTitle"),jObj.getString("cSubjectName"),jObj.getString("cHomeworkBody"),jObj.getString("cTeacherName"),jObj.getBoolean("studentSubmit"),jObj.getString("FileName"),docs);
                  //  Log.e("msgbody",jObj.getString("mesg_body"));
                    //subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    dataSet.add(msgB);
                    //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());

                homeworkAdapter grdAdapter=new homeworkAdapter(dataSet,homeworkFragment.this.getContext().getApplicationContext());
                homeworkList.setAdapter(grdAdapter);


                synchronized(grdAdapter){
                    grdAdapter.notify();
                }
                //  grdAdapter.notify();
            }catch (Exception e)
            {
                Log.e("load homework exception",e.toString());
            }finally {
                // loadUI();
              //  progressDialog.dismiss();
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
