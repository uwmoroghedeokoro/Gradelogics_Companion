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
 * {@link financeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link financeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class financeFragment extends Fragment {

    ListView finList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public financeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment financeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static financeFragment newInstance(String param1, String param2) {
        financeFragment fragment = new financeFragment();
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
        View root=inflater.inflate(R.layout.fragment_finance, container, false);

        finList=(ListView)root.findViewById(R.id.lv);

       // new loadtransactions().execute("");
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
        new loadtransactions().execute("");
        //INSERT CUSTOM CODE HERE
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    private class loadtransactions extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
       // ProgressDialog progressDialog=new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
           // progressDialog.setTitle("Loading");
           // progressDialog.show();
            Toast.makeText(getContext(),"Loading Records",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=financeFragment.this.getContext().getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                student stuobj = gson.fromJson(json, student.class);
                apikey=prefs.getString("apikey","");

                URL url=new URL("https://api2.gradelogics.com/api/finance/student/" + stuobj.id + "/" + stuobj.domain + "/" + stuobj.reg_year + "/" + apikey);
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

            Log.e(" transac json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final ArrayList<transaction> dataSet=new ArrayList<transaction>();
            try {


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=financeFragment.this.getContext().getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    transaction tran=new transaction();
                    tran.amt=jObj.getLong("TransAmt");
                    tran.desc=jObj.getString("TransDecr");
                    tran.type=jObj.getString("TransType");
                    tran.date=jObj.getString("TransDateString");

                    //subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    dataSet.add(tran);
                    //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());

                transacAdapter grdAdapter=new transacAdapter(financeFragment.this.getContext().getApplicationContext(),dataSet);
                finList.setAdapter(grdAdapter);



                grdAdapter.notify();
            }catch (Exception e)
            {

            }finally {
                // loadUI();
              //  progressDialog.dismiss();
            }
        }
    }
}
