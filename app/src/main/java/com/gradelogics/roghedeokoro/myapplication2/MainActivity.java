package com.gradelogics.roghedeokoro.myapplication2;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    TextView txt_fullname,txt_school,txt_department,txt_schyear,txt_schterm,txt_avg,txt_bal,txt_excel,txt_struggle,txt_term,txt_ovr;
    CircleImageView img_pic,stupic;
    ListView sbjList;
    student stuobj;
    ViewPager viewPager;
    ViewPager viewPager_2;
    TabLayout tabLayout;
    Toolbar toolbar;
    LinearLayout sliderDotspanel;
    private int dotscount;
   ViewPagerAdapterz adapter;
    private ImageView[] dots;
    FloatingActionButton fab1, fab2, fab3, fab4;
    LinearLayout ly_fab_3;
    LinearLayout ly_fab_2;
    LinearLayout ly_fab_1;
    RelativeLayout transPanel;
    boolean flag = true;
private String[] sibling_pics=new String[]{};
    private String[] sibling_ids=new String[]{};
    private String[] sibling_pwd=new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // Log.e("location","in main");
       // viewPager = (ViewPager) findViewById(R.id.viewPager);


        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson = new Gson();

        String json = getIntent().getStringExtra("student_object");
       // Log.e("jsonfrmclass",json);

        //setupViewPager(viewPager_2);


        stuobj = gson.fromJson(json, student.class);

        sibling_pics = prefs.getString("sibling_pics", "").split(",");
        sibling_ids = prefs.getString("sibling_ids", "").split(",");
        sibling_pwd = prefs.getString("sibling_pwd", "").split(",");

        transPanel=(RelativeLayout)findViewById(R.id.trans_panel);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        ly_fab_3=(LinearLayout)findViewById(R.id.ly_fab_3);


        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),student_new_msg.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    transPanel.setVisibility(View.VISIBLE);
                    fab3.show();ly_fab_3.setVisibility(View.VISIBLE);
                    ly_fab_3.animate().translationY(-(fab4.getCustomSize()));

                    // fab4.setImageResource(R.drawable.plus_sign_80);
                    flag = false;

                }else {
                    transPanel.setVisibility(View.INVISIBLE);

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



        // Gson gson = new Gson();
        Intent thisIntent=getIntent();
  //      String json = thisIntent.getStringExtra("student_object");
//        Log.e("jsonfrmclass",json);

        //ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, sibling_pics);

      //  viewPager.setAdapter(viewPagerAdapter);

        NumberFormat format=NumberFormat.getCurrencyInstance();
        sbjList=(ListView)findViewById(R.id.listSubjects);
      //  txt_fullname=(TextView) findViewById(R.id.txtFullname);
       // txt_school = (TextView)findViewById(R.id.txtSchool);
        txt_department=(TextView)findViewById(R.id.txtDepartment);
        txt_schyear=(TextView)findViewById(R.id.txtSchYear);
       // txt_schterm=(TextView)findViewById(R.id.txtSchTerm);
        txt_avg=(TextView)findViewById(R.id.txtAvg);
        txt_bal=(TextView)findViewById(R.id.txtBal) ;
      //  txt_excel=(TextView)findViewById(R.id.txtExcel);
       // txt_struggle=(TextView)findViewById(R.id.txtStruggle);
       // img_pic=(CircleImageView) findViewById(R.id.imgPic);
        stupic=(CircleImageView) findViewById(R.id.stu_pic);
        txt_term=(TextView)findViewById(R.id.txtSchTerm);
        txt_ovr=(TextView)findViewById(R.id.txt_overall);





       // sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

      //  ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

      //  viewPager.setAdapter(viewPagerAdapter);

        dotscount = 0;//viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];



       // dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));


        //loginTask loginT=new loginTask();
        //loginT.execute("2011958","cFbD");


        viewPager_2 = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tblayout);
        tabLayout.setTabTextColors((Color.parseColor("#cccccc")),
                Color.parseColor("#333333"));


        tabLayout.setupWithViewPager(viewPager_2);
        adapter = new ViewPagerAdapterz(getSupportFragmentManager());
        // adapter.addFragment(new alertsFragment(),"Alerts");
        viewPager_2.setAdapter(adapter);

        tabLayout.getTabAt(1).setCustomView(R.layout.notification_badge);
       // tabLayout.getTabAt(1).setIcon(R.drawable.icon_notification);
        TextView textView = (TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.text);

        if (stuobj.message_count<1)
            textView.setVisibility(View.INVISIBLE);
        else {
            textView.setText(String.valueOf(stuobj.message_count));
            textView.setVisibility(View.VISIBLE);
        }

        tabLayout.getTabAt(2).setCustomView(R.layout.homework_notification_badge);
       // tabLayout.getTabAt(2).
        // tabLayout.getTabAt(1).setIcon(R.drawable.icon_notification);
        TextView textView_hwrk = (TextView) tabLayout.getTabAt(2).getCustomView().findViewById(R.id.text);

        if (stuobj.homework_count<1)
            textView_hwrk.setVisibility(View.INVISIBLE);
        else {
            textView_hwrk.setText(String.valueOf(stuobj.homework_count));
            textView_hwrk.setVisibility(View.VISIBLE);
        }

        tabLayout.getTabAt(3).setCustomView(R.layout.finance_badge);
        // tabLayout.getTabAt(1).setIcon(R.drawable.icon_notification);
        TextView textView_fin = (TextView) tabLayout.getTabAt(3).getCustomView().findViewById(R.id.text);
        textView_fin.setVisibility(View.INVISIBLE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==1){
                    TextView textView = (TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.lbl);
                    textView.setTextColor(Color.parseColor("#000000"));
                }else  if (tab.getPosition()==2){
                    TextView textView = (TextView) tabLayout.getTabAt(2).getCustomView().findViewById(R.id.lbl);
                    textView.setTextColor(Color.parseColor("#000000"));
                }
                    // tabLayout.getTabAt(1).setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition()==1){
                    TextView textView = (TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.lbl);
                    textView.setTextColor(Color.parseColor("#bfbdbd"));
                }else  if (tab.getPosition()==2){
                    TextView textView = (TextView) tabLayout.getTabAt(2).getCustomView().findViewById(R.id.lbl);
                    textView.setTextColor(Color.parseColor("#bfbdbd"));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        // getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.super.onBackPressed();
            }
        });

        loadUI();


    }

    private class loadSubjects extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,schterm="";
                int studentid=0;
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                studentid=(prefs.getInt("student_id",0));
                domain=prefs.getString("domain","-");
                schyear=prefs.getString("reg_year","-");
                schterm=prefs.getString("school_term","-");

                domain=stuobj.domain;
                schyear=stuobj.reg_year;
                schterm=stuobj.sch_term;

                URL url=new URL("http://gradelogics.com/api/api.asmx/subjects?studentid="+ String.valueOf(studentid) +"&domain="+ domain + "&schyear="+schyear+"&schterm="+schterm);
                urlConnection=(HttpURLConnection)url.openConnection();

                Log.e("sbj url",url.toString());

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

           // Log.e("json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            final ArrayList<subject> dataSet=new ArrayList<subject>();
            try {


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {
                   // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    dataSet.add(nSubject);
                  //  Log.e("sbj json",jObj.getString("subjectName"));
                }
                // Log.e("sch json",schObject.toString());

                subjectAdapter sbjAdapter=new subjectAdapter(dataSet,getApplicationContext());
                sbjList.setAdapter(sbjAdapter);

                sbjList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                        subject sbj=dataSet.get(pos);
                        Log.e("Subject ", sbj.subjectName);

                        Intent i = new Intent(getApplicationContext(), activity_grades.class);
                        i.putExtra("subjectid", sbj.id);
                        i.putExtra("subjectname", sbj.subjectName);
                        startActivity(i);
                    }
                });

                sbjAdapter.notify();
            }catch (Exception e)
            {

            }finally {
               // loadUI();
            }
        }
    }




    private class loginTask extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            String result="";
            String userid=params[0];
            String pwd=params[1];
            try {
                URL url=new URL("http://gradelogics.com/api/api.asmx/auth?userid="+userid+"&pwd="+ pwd);
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

            Log.e("json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            try {
                JSONObject jObject = new JSONObject(res);
                SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
                if (jObject.getInt("cStudentID") > 0) {
                    student Student = new student();
                    Student.id = jObject.getInt("cStudentID");
                    Student.reg_year = jObject.getString("cRegYear");
                    Student.sch_term = jObject.getString("currentTerm");
                    Student.fullname = jObject.getString("cFirstname") + ' ' + jObject.getString("cLastname");
                    Student.department = jObject.getString("cDepartmentName");
                    Student.term_avg = String.valueOf(jObject.getLong("termAvg"));
                    Student.balance = jObject.getLong("Balance");
                    Student.excel = jObject.getInt("excel");
                    Student.struggle = jObject.getInt("struggle");
                    Student.domain = jObject.getString("ConnString");

                    //   Log.e("domain",jObject.getString("ConnString"));
                    editor.putInt("student_id", jObject.getInt("cStudentID"));
                    editor.putString("domain",jObject.getString("ConnString"));
                    //load profile data
                    JSONObject tmpObj = new JSONObject(jObject.getString("Parent"));

                    Student.Contact.mother = tmpObj.getString("Mother");
                    Student.Contact.father = tmpObj.getString("Father");

                    // editor.putString("mother",(tmpObj.getString("Mother")));
                    // editor.putString("father",tmpObj.getString("Father"));

                    tmpObj = new JSONObject(jObject.getString("ParentContact"));

                    Student.Contact.email = tmpObj.getString("Email");
                    Student.Contact.workp = tmpObj.getString("PhoneW");
                    Student.Contact.homep = tmpObj.getString("PhoneM");

                    // editor.putString("email",tmpObj.getString("Email"));
                    // editor.putString("workph",tmpObj.getString("PhoneW"));
                    // editor.putString("cellph",tmpObj.getString("PhoneM"));

                    tmpObj = new JSONObject(jObject.getString("Address"));

                    Student.Contact.address = tmpObj.getString("StreetAddress");
                    Student.Contact.city = tmpObj.getString("City");

                    // editor.putString("address",tmpObj.getString("StreetAddress"));
                    // editor.putString("city",tmpObj.getString("City"));
                    ///

                    // editor.putString("classes",jObject.getString("Classes"));
                    // Log.e("stuID",String.valueOf(jObject.getInt("cStudentID")));
                    //get school info
                    JSONObject schObject = (jObject.getJSONObject("schoolInfo"));
                    // Log.e("sch json",schObject.toString());

                    Student.school_name = schObject.getString("SchoolName");
                    student.contact mContact = Student.Contact;
                    //Log.e("reading contact mother",mContact.mother);
                    // editor.putString("school_name",schObject.getString("SchoolName"));
                    // editor.putString("balance",jObject.getString("Balance"));


                    Gson gson = new Gson();
                    String json = gson.toJson(Student);
                    editor.putString("student_object", json);
                    json = gson.toJson(mContact);
                    editor.putString("contact_object", json);

                    editor.commit();

                    editor.putString("profile_pic", jObject.getString("cPicFile"));
                    editor.apply();

                    // call siblings
                   loadUI();

                } else
                {
                    //login has failed
                   // txtErr.setText("Login has failed. Please confirm GradelogicsID and Password");
                    Toast.makeText(getApplicationContext(),"Login failed.",Toast.LENGTH_LONG).show();
                }
            }catch(Exception e)
            {
                Log.e("login error", e.toString());
            }finally{
                //loadUI();
            }

        }
    }



    protected void loadUI()  {
        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = getIntent().getStringExtra("student_object");
        Log.e("jsonfrmclass",json);

        //setupViewPager(viewPager_2);


        stuobj = gson.fromJson(json, student.class);
        NumberFormat format=NumberFormat.getCurrencyInstance();


        SharedPreferences.Editor editor = getSharedPreferences("gradelogics", MODE_PRIVATE).edit();
        editor.putInt("selected_studentid",stuobj.id);
       // Log.e("main stud",String.valueOf(stuobj.id));
        editor.putString("student_object",json);
       // editor.putString("domain",stuobj.domain);
        editor.commit();
        editor.apply();

        toolbar.setTitle(stuobj.fullname);
       // txt_fullname.setText(stuobj.fullname);
        txt_department.setText(stuobj.department + ": " + stuobj._classroom.className);
        txt_schyear.setText(stuobj.reg_year + " | ");
        txt_term.setText("Term " + stuobj.sch_term);
        txt_ovr.setText(stuobj._OverallAvg);
        //txt_school.setText(stuobj.school_name);
        txt_avg.setText(stuobj.term_avg);
        txt_bal.setText(format.format(stuobj.balance));
        //txt_excel.setText(String.valueOf(stuobj.excel));
       // txt_struggle.setText(String.valueOf(stuobj.struggle));
       // txt_schterm.setText("Term " + prefs.getString("school_term",null));
        final String picUrl=prefs.getString("profile_pic",null);

        Picasso.get()
                .load(stuobj.img_url)
                .fit()
                .centerInside()
                .into(stupic);
      // loadProfilePic loadPic=new loadProfilePic();
       // loadPic.execute(picUrl);

        new loadSubjects().execute();
    }


    private void setupViewPager(ViewPager viewPager) {

        viewPager_2.setAdapter(adapter);
        //   tabLayout.getTabAt(4).setIcon(R.drawable.icons8_alarm_24);

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
                fragment = new subjectFragment();
            }
            else if (position == 1)
            {
                fragment = new messageFragment();
            }
            else if (position == 2)
            {
                fragment = new homeworkFragment();
            }
            else if (position == 3)
            {
                fragment = new financeFragment();
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
                title = "Grades";
            }
            else if (position == 1)
            {
                title = "Messages";
            }
            else if (position == 2)
            {
                title = "Homework";
            } else if (position == 3)
            {
                title = "Finance";
            }
            return title;
        }
    }

    private class loadProfilePic extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url = null;

            try {
                url = new URL(params[0]);
                Log.e("pic url",url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                Log.e("image load error",e.toString());
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap pic)
        {
            img_pic.setImageBitmap(pic);
        }
    }

@Override
   protected void onResume() {

    super.onResume();
    Log.e("where","main");
    loadUI();
}

}
