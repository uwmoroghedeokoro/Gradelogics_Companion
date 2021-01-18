package com.gradelogics.roghedeokoro.myapplication2;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_profile extends AppCompatActivity {

    TextView txt_studentname,txt_school,txt_mother,txt_father,txt_email,txt_workph,txt_cellph,txt_address,txt_city,txt_userid;
    CircleImageView img_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Profile");
       // getWindow().getAttributes().windowAnimations=;
       // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_profile.super.onBackPressed();
            }
        });

        txt_studentname=(TextView)findViewById(R.id.txtFullname);
        txt_school=(TextView)findViewById(R.id.txtSchool);
        txt_mother=(TextView)findViewById(R.id.txtMother);
        txt_father=(TextView)findViewById(R.id.txtFather);
        txt_email=(TextView)findViewById(R.id.txtEmail);
        txt_workph=(TextView)findViewById(R.id.txtWorkPh);
        txt_cellph=(TextView)findViewById(R.id.txtCellPh);
        txt_address=(TextView)findViewById(R.id.txtAddress);
        txt_userid=(TextView)findViewById(R.id.txtUserid);
        txt_city=(TextView)findViewById(R.id.txtCity);

        img_profile=(CircleImageView)findViewById(R.id.imgPic);

        loadUI();
    }



    private void loadUI()
    {
        try
        {
            SharedPreferences pref=getSharedPreferences("gradelogics",MODE_PRIVATE);

            final String picUrl=pref.getString("profile_pic",null);

            loadProfilePic loadPic=new loadProfilePic();
            loadPic.execute(picUrl);

            Gson gson = new Gson();
            String json = pref.getString("student_object", "");
            student stuobj = gson.fromJson(json, student.class);
            //load profile from Shared Preferences
            txt_studentname.setText(stuobj.fullname);
            txt_school.setText(stuobj.school_name);
            txt_userid.setText(String.valueOf(stuobj.id));
            Log.e("moth",pref.getString("mother","-"));

            json=pref.getString("contact_object","");
            student.contact mContact=gson.fromJson(json,student.contact.class);
            Log.e("mother",mContact.mother);
            txt_mother.setText(mContact.mother);
            txt_father.setText(mContact.father);
            txt_email.setText(mContact.email);
            txt_workph.setText(mContact.workp);
            txt_cellph.setText(mContact.cellp);
            txt_address.setText(mContact.address);
            txt_city.setText(mContact.city);
        }catch (Exception e)
        {
            Log.e("profile error",e.toString());
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
            img_profile.setImageBitmap(pic);
        }
    }
}
