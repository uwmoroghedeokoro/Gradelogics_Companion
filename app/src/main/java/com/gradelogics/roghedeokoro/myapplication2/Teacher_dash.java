package com.gradelogics.roghedeokoro.myapplication2;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
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
import java.util.List;

public class Teacher_dash extends AppCompatActivity {

    teacher meTeacher;
    TextView my_name,sch_name;
    Spinner subjectS,classS,gradebookSpin;
    ArrayList<student>class_students;
    RecyclerView lv;
    class_studentAdapter classStudentAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ViewPager viewPager_2;
    int active_subject,active_class,active_gradebook=-1;
    Button btnAssign,btnNewMsg,btnHomework;
    FloatingActionButton fab1, fab2, fab3, fab4,fab5;
    boolean flag = true;

    boolean userIsInteracting=false;
    ViewPagerAdapterz adapter;
    boolean reload=true;


    LinearLayout ly_fab_5;
     LinearLayout ly_fab_3;
     LinearLayout ly_fab_2;
     LinearLayout ly_fab_1;
     RelativeLayout transPanel;
    SharedPreferences prefs;
    TabLayout tabLayout;
    //int msg_count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dash);

        prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        active_subject= prefs.getInt("active_subject",-1);
        active_class=prefs.getInt("active_class",-1);

        String selectedGB=prefs.getString("active_gradebook","-1");
        if (!selectedGB.equals("-1")) {
            gradeBook sb = gson.fromJson(selectedGB, gradeBook.class);

            active_gradebook = sb.ID;
        }

        final SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();

        meTeacher=gson.fromJson(json,teacher.class);

        class_students=new ArrayList<>();
        ImageView icologout=(ImageView)findViewById(R.id.ico_logout);
        icologout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent=new Intent(getApplicationContext(),activity_login.class);
                startActivity(intent);
            }
        });

        subjectS=(Spinner)findViewById(R.id.spin_subject);
        classS=(Spinner)findViewById(R.id.spin_classroom);
        gradebookSpin=(Spinner)findViewById(R.id.spin_gradebook);
        btnNewMsg=(Button)findViewById(R.id.btn_msg);
        btnAssign=(Button)findViewById(R.id.btn_assign);
        btnHomework=(Button)findViewById(R.id.btn_homework);

        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),classAssignGrade.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnNewMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),activity_teacher_new_msg.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),teacher_new_homework.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        my_name=(TextView)findViewById(R.id.txt_my_name);
        sch_name=(TextView)findViewById(R.id.txt_sch_name);

        tabLayout=(TabLayout)findViewById(R.id.tblayout);
       // tabLayout.addTab(tabLayout.newTab().setText("Students"));
        //tabLayout.addTab(tabLayout.newTab().setText("Grades"));
        //tabLayout.addTab(tabLayout.newTab().setText("Homework"));
        //tabLayout.addTab(tabLayout.newTab().setText("Messages"));
        // tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager_2 = (ViewPager) findViewById(R.id.view_pager);
        tabLayout.setTabTextColors((Color.parseColor("#cccccc")),
                Color.parseColor("#333333"));

        transPanel=(RelativeLayout)findViewById(R.id.trans_panel);
        ///
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);
        ly_fab_5=(LinearLayout)findViewById(R.id.ly_fab_5);
        ly_fab_3=(LinearLayout)findViewById(R.id.ly_fab_3);
        ly_fab_2=(LinearLayout)findViewById(R.id.ly_fab_2);
        ly_fab_1=(LinearLayout)findViewById(R.id.ly_fab_1);

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),activity_teacher_attendance.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),teacher_new_homework.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),classAssignGrade.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),activity_teacher_new_msg.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    transPanel.setVisibility(View.VISIBLE);
                    fab1.show();ly_fab_1.setVisibility(View.VISIBLE);
                    fab2.show();ly_fab_2.setVisibility(View.VISIBLE);
                    fab3.show();ly_fab_3.setVisibility(View.VISIBLE);
                    fab5.show();ly_fab_5.setVisibility(View.VISIBLE);
                    ly_fab_5.animate().translationY(-(fab2.getCustomSize()+fab3.getCustomSize()+fab4.getCustomSize()+fab1.getCustomSize()));
                    ly_fab_1.animate().translationY(-(fab2.getCustomSize()+fab3.getCustomSize()+fab4.getCustomSize()));
                    ly_fab_2.animate().translationY(-(fab3.getCustomSize()+fab4.getCustomSize()));
                    ly_fab_3.animate().translationY(-(fab4.getCustomSize()));

                   // fab4.setImageResource(R.drawable.plus_sign_80);
                    flag = false;

                }else {
                    transPanel.setVisibility(View.INVISIBLE);
                    ly_fab_5.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (flag)
                                ly_fab_5.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    ly_fab_1.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (flag)
                            ly_fab_1.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    ly_fab_2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(flag)
                            ly_fab_2.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    ly_fab_3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(flag)
                            ly_fab_3.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    fab4.setImageResource(R.drawable.plus_new_50);
                    flag = true;

                }
            }
        });

        ///

        tabLayout.setupWithViewPager(viewPager_2);
         adapter = new ViewPagerAdapterz(getSupportFragmentManager());
        // adapter.addFragment(new alertsFragment(),"Alerts");
        viewPager_2.setAdapter(adapter);
        loadGUI();


        subjectS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
               if (userIsInteracting==true) {
                   editor.putInt("active_subject", s.tag);
                   editor.apply();
                   Log.e("subject", "selected " + String.valueOf(s.tag));
                  // setupViewPager(viewPager_2);
                   viewPager_2.setAdapter(adapter);

                    update_msg_count();
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              if (userIsInteracting) {
                  StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);

                  editor.putInt("active_class", s.tag);
                  editor.putString("active_class_name", s.string);
                  editor.apply();

                  viewPager_2.setAdapter(adapter);
                  update_msg_count();
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gradebookSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (userIsInteracting) {
                    gradeBook s = (gradeBook) parent.getItemAtPosition(position);
                   // editor.putInt("active_gradebook", s.ID);
                    Gson gson=new Gson();
                    editor.putString("active_gradebook",gson.toJson(s));
                    editor.apply();
                    viewPager_2.setAdapter(adapter);
                    update_msg_count();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        tabLayout.getTabAt(3).setCustomView(R.layout.notification_badge);
        // tabLayout.getTabAt(1).setIcon(R.drawable.icon_notification);
        TextView textView = (TextView) tabLayout.getTabAt(3).getCustomView().findViewById(R.id.text);




        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==3){
                    TextView textView = (TextView) tabLayout.getTabAt(3).getCustomView().findViewById(R.id.lbl);
                    textView.setTextColor(Color.parseColor("#000000"));
                }
                // tabLayout.getTabAt(1).setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition()==3){
                    TextView textView = (TextView) tabLayout.getTabAt(3).getCustomView().findViewById(R.id.lbl);
                    textView.setTextColor(Color.parseColor("#bfbdbd"));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        retrieve_token();
    }

    private void retrieve_token()
    {
        startService(new Intent(this, MyFirebaseMessagingService.class));
       // Log.e("start service","myFireBase");
       // Log.e("FCMToken", "token "+ FirebaseInstanceId.getInstance().getToken());

        SharedPreferences.Editor editor= prefs.edit();

        String device_id=prefs.getString("teacher_device_id","-");
       if (device_id.equals("-"))
        {
            device_id=FirebaseInstanceId.getInstance().getToken();
            editor.putString("teacher_device_id",device_id);
            editor.apply();

            //senddevice
            new send_device_id().execute(device_id);
        }
       // Log.d("newToken", getActivity().getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));
    }

    private class send_device_id extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;


        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                String device_id=params[0];
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");
                URL url=new URL("https://api2.gradelogics.com/api/device/register/" + meTeacher.id + "/" + device_id + "/" + meTeacher.domain + "/" + apikey);

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

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String res)
        {
            //subscribe to each classroom discussion as Topic;
           boolean all_subscribed= prefs.getBoolean("all_subscribed",false);

           if (!all_subscribed) {
               for (classroom clr : meTeacher.classrooms) {
                   _utility.subscribe_topic(meTeacher.domain + "-class-" + String.valueOf(clr.classID));
               }

               //subscribe to ALLTeachers Topic;
               _utility.subscribe_topic(meTeacher.domain + "-all-teachers");
               SharedPreferences.Editor editor = prefs.edit();
               editor.putBoolean("all_subscribed",true);
               editor.commit();
               editor.apply();
           }
        }
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }


    private void update_msg_count()
    {

        tabLayout.getTabAt(3).setCustomView(R.layout.notification_badge);
        // tabLayout.getTabAt(1).setIcon(R.drawable.icon_notification);
       // TextView textView = (TextView) tabLayout.getTabAt(3).getCustomView().findViewById(R.id.text);

        new update_info().execute("");
    }
    private void loadGUI()
    {
        ArrayList<StringWithTag>subjectlist=new ArrayList<>();
        ArrayList<StringWithTag>classroomlist=new ArrayList<>();
        ArrayList<StringWithTag>gradebookList=new ArrayList<>();

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

        for (gradeBook gradeb:meTeacher.gradeBooks
        ) {
            StringWithTag swt=new StringWithTag(gradeb.gradebookName,gradeb.ID);
            gradebookList.add(swt);
        }

        ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_text_white, subjectlist);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectS.setAdapter(adap);
//set selection
       for (int s=0;s<adap.getCount();s++){
           StringWithTag swT=adap.getItem(s);
           if (swT.tag==active_subject){
                Log.e("subj_match",String.valueOf(active_subject));
              subjectS.setSelection(s);
           }
        }

        ArrayAdapter<StringWithTag> adap2 = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_text_white, classroomlist);
        adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classS.setAdapter(adap2);
//set selection
        for (int s=0;s<adap2.getCount();s++){
            StringWithTag swT=adap2.getItem(s);
            if (swT.tag==active_class){
                classS.setSelection(s);
            }
        }

        gradebookAdapter adap3 = new gradebookAdapter (meTeacher.gradeBooks,getApplicationContext());
        adap3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradebookSpin.setAdapter(adap3);
//set selection gradebook
        for (int s=0;s<adap3.getCount();s++){
            gradeBook gB=adap3.getItem(s);
            if (gB.ID==active_gradebook){
            //    Log.e("gradebookM",String.valueOf(active_gradebook));
                gradebookSpin.setSelection(s);
            }
        }
        //name
        my_name.setText(meTeacher.fullname);
        sch_name.setText((meTeacher.school_name));

        SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
        //get students
        StringWithTag selected_subject=adap.getItem(subjectS.getSelectedItemPosition());
        StringWithTag selected_class=adap2.getItem(classS.getSelectedItemPosition());
        gradeBook selected_gradebook=adap3.getItem(gradebookSpin.getSelectedItemPosition());

        Gson gson=new Gson();

        editor.putInt("active_subject",selected_subject.tag);
        editor.putInt("active_class",selected_class.tag);
        editor.putString("active_class_name", selected_class.string);
        editor.putString("active_gradebook",gson.toJson(selected_gradebook));
        editor.apply();

       // setupViewPager(viewPager_2);
      //  new get_students().execute(String.valueOf(selected_subject.tag),String.valueOf(selected_class.tag));
    }

@Override
protected void onResume(){
        super.onResume();
        new update_info().execute("");

   // setupViewPager(viewPager_2);
}


    private class ViewPagerAdapterz extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        ViewPagerAdapterz(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0)
            {
                fragment = new classStudentsFragment();
            }
            else if (position == 1)
            {
                fragment = new classroomGradesFragment();
            }
            else if (position == 2)
            {
                fragment = new classHomeworkFragment();
            }
            else if (position == 3)
            {
                fragment = new classMessagesFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
            {
                title = "Students";
            }
            else if (position == 1)
            {
                title = "Grades";
            }
            else if (position == 2)
            {
                title = "Homework";
            } else if (position == 3)
            {
                title = "Messages";
            }
            return title;
        }
    }


    private class update_info extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

      //  ProgressDialog progressDialog=new ProgressDialog(activity_login.this);
      teacher Teacher;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
           // Toast.makeText(getApplicationContext(),"Refreshing",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
           // Log.e("action","login");
           /// String userid=params[0];
           // String pwd=params[1];
            int active_class=prefs.getInt("active_class",-1);
            try {
                URL url=new URL("https://api2.gradelogics.com/api/teacher/msg_info/" + meTeacher.id + "/-1/" + meTeacher.domain+ "/" + meTeacher.api_key);
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                String line;
                while((line=reader.readLine()) != null){
                    result=line;
                }

            }catch (Exception e){
                Log.e("error",e.getMessage());
            }finally {
                urlConnection.disconnect();
            }

            Log.e("json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            Log.e("ret",res);
            int msg_count=0;
            try {
                JSONObject jObject = new JSONObject(res);
                SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();

                 msg_count=jObject.getInt("msg_count");

            }catch(Exception e)
            {
                Log.e("refresh info err",e.getMessage());

            }finally{
               // progressDialog.dismiss();
                TextView textView = (TextView) tabLayout.getTabAt(3).getCustomView().findViewById(R.id.text);


                if (msg_count<1)
                   textView.setVisibility(View.INVISIBLE);
                else {
                    textView.setText(String.valueOf(msg_count));
                    textView.setVisibility(View.VISIBLE);
                }
            }

        }
    }

}
