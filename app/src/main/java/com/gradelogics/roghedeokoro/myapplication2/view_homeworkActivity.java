package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class view_homeworkActivity extends AppCompatActivity {

    LinearLayout btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_homework);
        Intent intent=getIntent();
        String msgDate=intent.getStringExtra("home_date");
        String msgBody=intent.getStringExtra("home_body");
        String msgteach=intent.getStringExtra("home_teacher");
        final String msgattach=intent.getStringExtra("home_attach");
        final String msgFrom=intent.getStringExtra("home_title");
        final String home_id=intent.getStringExtra("home_id");
        String homeSubject=intent.getStringExtra("home_subject");

        Gson gson=new Gson();
        homeworkObj home_object=gson.fromJson(intent.getStringExtra("home_object"),homeworkObj.class);

        TextView msgbody=(TextView)findViewById(R.id.msg_body);
        TextView msgdate=(TextView)findViewById(R.id.msg_date);
        TextView msgfrom=(TextView)findViewById(R.id.msg_from);
        TextView msgTeacher=(TextView)findViewById(R.id.msg_teacher);
        //TextView txtletter=(TextView)findViewById(R.id.txtLetter);


        msgbody.setText(Html.fromHtml(home_object.hmeBody));
        msgfrom.setText(msgFrom);
        msgTeacher.setText("Teacher: " + msgteach);
        msgdate.setText("Due: " + msgDate);


        int h=1;
        for (final homeworkObj.doc doc:home_object.attachments)
              {
            if(h==1)
            {
                LinearLayout attachview=(LinearLayout)findViewById(R.id.attach_view);
                    attachview.setVisibility(View.VISIBLE);
                    attachview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("http://www.gradelogics.com/files/"+doc.docPath));
                            startActivity(i);
                        }
                    });


            }else if(h==2)
            {
                LinearLayout attachview=(LinearLayout)findViewById(R.id.attach_view2);
                attachview.setVisibility(View.VISIBLE);
                attachview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://www.gradelogics.com/files/"+doc.docPath));
                        startActivity(i);
                    }
                });


            }else if(h==3)
            {
                LinearLayout attachview=(LinearLayout)findViewById(R.id.attach_view3);
                attachview.setVisibility(View.VISIBLE);
                attachview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://www.gradelogics.com/files/"+doc.docPath));
                        startActivity(i);
                    }
                });


            }else if(h==4)
            {
                LinearLayout attachview=(LinearLayout)findViewById(R.id.attach_view4);
                attachview.setVisibility(View.VISIBLE);
                attachview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://www.gradelogics.com/files/"+doc.docPath));
                        startActivity(i);
                    }
                });


            }
            else if(h==5)
            {
                LinearLayout attachview=(LinearLayout)findViewById(R.id.attach_view5);
                attachview.setVisibility(View.VISIBLE);
                attachview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://www.gradelogics.com/files/"+doc.docPath));
                        startActivity(i);
                    }
                });


            }
                  h++;
        }

       // txtletter.setText(homeSubject.substring(0,1));

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(homeSubject);
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        // getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_homeworkActivity.super.onBackPressed();
            }
        });


        btn=(LinearLayout)findViewById(R.id.btn_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),activity_homework_submit.class);
                intent.putExtra("home_id",home_id);
                intent.putExtra("home_title",msgFrom);
               // intent.putExtra("home_attach",msgattach);
                startActivityForResult(intent,99);
              //  startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        Log.e("return code",String.valueOf(resultCode));
        if(resultCode==99)
        {

           btn.setBackgroundColor(Color.parseColor("#F0F0F0"));
           TextView btntext=(TextView)findViewById(R.id.btn_submit_text);
           btntext.setText("Submitted");
           btntext.setTextColor(Color.parseColor("#333333"));
           btn.setOnClickListener(null);
            //String message=data.getStringExtra("MESSAGE");
           // textView1.setText(message);
        }
    }

}
