package com.gradelogics.roghedeokoro.myapplication2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class view_teacher_message extends AppCompatActivity {

    RecyclerView msgbody;
    TextView msgdate;
    TextView msgfrom;
    teacher meTeacher;
    Button btnSend;
    int fromID;
     EditText msgb;
     gradeBook gB;
    String msgid;
    int msg_type;
    String full_body;
    String from_contact_name;
    String msgID;
    private messageListAdapter mMessageAdapter;;

    private ArrayList<msgObg>messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher_message);

        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);

        Intent intent=getIntent();
        String msgType="";
        msgID=intent.getStringExtra("msg_id");
        msgid=msgID;

        msgType=intent.getStringExtra("msgtype");
        msg_type=intent.getIntExtra("msg_type",1);
        from_contact_name=intent.getStringExtra("msg_from");

        messageList=new ArrayList<msgObg>();

        LocalBroadcastManager.getInstance(this).registerReceiver(aLBReceiver,
                new IntentFilter("in_message"));

        Gson gson=new Gson();
        String json=prefs.getString("login_object","");

         meTeacher=gson.fromJson(json,teacher.class);
         gB=gson.fromJson(prefs.getString("active_gradebook",""),gradeBook.class);
        btnSend=(Button) findViewById(R.id.btn_send);
        msgbody=(RecyclerView) findViewById(R.id.msg_body);
        mMessageAdapter = new messageListAdapter(this, messageList);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        // llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        msgbody.setLayoutManager(llm);
        msgbody.setAdapter(mMessageAdapter);

        msgb=(EditText)findViewById(R.id.edt_new_msg);


        if (Build.VERSION.SDK_INT >= 11) {
            msgbody.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        msgbody.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (msgbody.getAdapter().getItemCount()>2) {
                                msgbody.smoothScrollToPosition(
                                        msgbody.getAdapter().getItemCount() - 1);
                            }
                            }
                        }, 100);
                    }
                }
            });
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //toolbar.setTitle("Profile");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(intent.getStringExtra("msg_from"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_teacher_message.super.onBackPressed();
            }
        });

     //   ux_int();

        if (!msgType.equals("new")) {
            new mark_as_read().execute(msgID);
        }else
        {
            fromID=Integer.valueOf(intent.getStringExtra("toID"));
            getSupportActionBar().setTitle(intent.getStringExtra("toName"));
            new mark_as_read().execute(msgID);

        }

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!msgb.getText().toString().equals("")) {

                            new send_message().execute(msgb.getText().toString(), msgID);
                            msgb.setText("");
                        }
                    }
                });

    }

    private void ux_int()
    {

        //  msgbody.loadUrl("javascript:alert('')");
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd HH:mm", Locale.getDefault());
        String formattedDate = df.format(c);

        String css="<style>\n" +
                ".box {\n" +
                "  width: 270px;\n" +
                "  margin: 0px auto;\n" +
                "  background: #b2f7f4;\n" +
                "  padding: 10px;\n" +
                "  text-align: left;\n" +
                "  font-weight: 900;\n" +
                "  color: #333;\n" +
                "  font-family: arial;\n" +
                "  position:relative;\n" +
                "}\n" +
                ".sb1:before {\n" +
                "  content: \"\";\n" +
                "  width: 0px;\n" +
                "  height: 0px;\n" +
                "  position: absolute;\n" +
                "  border-left: 10px solid #b2f7f4;\n" +
                "  border-right: 10px solid transparent;\n" +
                "  border-top: 10px solid #b2f7f4;\n" +
                "  border-bottom: 10px solid transparent;\n" +
                "  right: -20px;\n" +
                "  top: 6px}\n" +

                ".box_out {\n" +
                "  width: 270px;\n" +
                "  margin: 0px auto;\n" +
                "  margin-left:30px;\n"+
                "  background: #dedfe0;\n" +
                "  padding: 10px;\n" +
                "  text-align: left;\n" +
                "  font-weight: 900;\n" +
                "  color: #333;\n" +
                "  font-family: arial;\n" +
                "  position:relative;\n" +
                "}\n" +
                ".sb2:before {\n" +
                "  content: \"\";\n" +
                "  width: 0px;\n" +
                "  height: 0px;\n" +
                "  position: absolute;\n" +
                "  border-right: 10px solid #dedfe0;\n" +
                "  border-left: 10px solid transparent;\n" +
                "  border-top: 10px solid #dedfe0;\n" +
                "  border-bottom: 10px solid transparent;\n" +
                "  left: -20px;\n" +
                "  top: 6px}\n" +
                "</style>";


        full_body = css;
        String js="<script>function push_incoming(sender_name,sender_text,msg_date){document.body.innerHTML+=\"<div class='box sb1'><span style='font-weight:normal;font-size:10pt'><b>\" +sender_name+ \"</b></span> - <span style='font-weight:normal;font-size:9pt;color:#333'>\" + msg_date + \"</span><br><span style='font-weight:normal;font-size:11pt'>\" +sender_text+ \"</span></div><div style='height:3px;background-color:#ffffff;width:100%'></div>\";window.scrollTo(0,document.body.scrollHeight); } \n" ;
        js+="function push_outgoing(sender_text,msg_date){document.body.innerHTML+=\"<div class='box_out sb2'><span style='font-weight:normal;font-size:10pt'><b>Me</b></span> - <span style='font-weight:normal;font-size:9pt;color:#333'>\" + msg_date + \"</span><br><span style='font-weight:normal;font-size:11pt'>\" +sender_text+ \"</span></div><div style='height:3px;background-color:#ffffff;width:100%'></div>\";window.scrollTo(0,document.body.scrollHeight); }</script>" ;

        full_body+=js;
        full_body +="<body>";
    }
    private BroadcastReceiver aLBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // perform action here.
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd HH:mm", Locale.getDefault());
            String formattedDate = df.format(c);

            String msg_text=intent.getStringExtra("msg_text");
            String msg_sender_id=intent.getStringExtra("msg_sender_id");
            // Toast.makeText(getApplicationContext(),msg_text,Toast.LENGTH_SHORT).show();
           // Log.e("message in",msgID + "-" + String.valueOf(msg_sender_id));
            if (msg_sender_id!=null) {
                if (String.valueOf(msg_sender_id).equals(msgID)) {
                    msgObg msg_item=new msgObg(formattedDate,"-1",msg_text,from_contact_name,true,1);

                    messageList.add(msg_item);

                    mMessageAdapter.notifyItemInserted(messageList.size() - 1);

                    msgbody.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            msgbody.smoothScrollToPosition(
                                    msgbody.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }

            //push_msg_to_list(msg_sender_id,msg_text);

            // new loadMessage().execute(msgID);
        }
    };
    private class send_message extends AsyncTask<String,String,String>
    {
        HttpURLConnection conn;
        String rootID;
        boolean success=true;
        String msg_body;
        //  ProgressDialog progressDialog=new ProgressDialog(getContext());
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            //   progressDialog.setTitle("Sending message....");
            //   progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String ret="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                 msg_body=params[0];
                 rootID=params[1];
                // rootID="140";
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");
                Gson gson=new Gson();
                String json=prefs.getString("login_object","");
                String gbjson=prefs.getString("active_gradebook","");
                gradeBook gB=gson.fromJson(gbjson,gradeBook.class);

                teacher meTeacher=gson.fromJson(json,teacher.class);

               // URL url=new URL("https://api2.gradelogics.com/api/teacher/send_message/" + msgbody + "/0/" + meTeacher.id +"/" + fromID + "/"+ rootID +"/0/" + String.valueOf(gB.ID) +"/" + meTeacher.domain +"/" + apikey);
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                File sourceFile;// = new File(file_num[0]);
                DataOutputStream dataOutputStream;

                // int bytesRead, bytesAvailable, bufferSize;
                // byte[] buffer;
                // int maxBufferSize = 1 * 1024 * 1024;
                //DataOutputStream dos = null;

               // Log.e("data to post",data);
                OutputStream out = null;
               // String ret="";
                URL url;
               // HttpURLConnection conn;// = (HttpURLConnection) url.openConnection();
                //  File sourceFile = new File(file_num[0]);


                ///
                String Tag="submit";
                try
                {


                    //  FileInputStream fileInputStream = new FileInputStream(sourceFile);

                    url = new URL("https://api2.gradelogics.com/api/teacher/send_message/");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");

                    // Allow Inputs
                    conn.setDoInput(true);

                    // Allow Outputs
                    conn.setDoOutput(true);

                    // Don't use a cached copy.
                    conn.setUseCaches(false);

                    // Use a post method.
                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Connection", "Keep-Alive");

                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"gradebookID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(gB.ID));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"teacherID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(meTeacher.id));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"domain\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(meTeacher.domain);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"messageBody\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(msg_body).replace("\"","'"));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"toID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(msgid));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"rootID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(rootID));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"message_type\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(0));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"messageID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(0));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);


                    dos.flush();

                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    InputStream is = conn.getInputStream();

                    // retrieve the response from server
                    int ch;

                    StringBuffer b =new StringBuffer();
                    while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                    String s=b.toString();
                    Log.e("Response",s);
                    ret=s;
                    dos.close();
                }
                catch (MalformedURLException ex)
                {
                    Log.e(Tag, "URL error: " + ex.getMessage(), ex);
                    success=false;
                }

                catch (IOException ioe)
                {
                    Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
                    success=false;
                }catch (Exception ex)
                {
                    Log.e(Tag, "general error: " + ex.getMessage(), ex);
                    success=false;
                }

                ///
              //  return ret;

            }catch (Exception e){

                Log.e("send error",e.getMessage());
            }finally {
                conn.disconnect();
            }

            //  Log.e("sibling json",result);
            return ret;
        }

        @Override
        protected void onPostExecute(String res)
        {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd HH:mm", Locale.getDefault());
            String formattedDate = df.format(c);

            if (success) {
               // Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();


                msgObg msg_item=new msgObg(formattedDate,"-1",msg_body,"Me",true,1);

                messageList.add(msg_item);

                mMessageAdapter.notifyItemInserted(messageList.size() - 1);

                msgbody.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        msgbody.smoothScrollToPosition(
                                msgbody.getAdapter().getItemCount() - 1);
                    }
                }, 100);
            }else
            {
                Toast.makeText(getApplicationContext(), "Error while sending. Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class mark_as_read extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                 msgid=params[0];
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");
                // studentid=(prefs.getInt("student_id",0));
                // domain=prefs.getString("domain","-");
                //  schyear=prefs.getString("reg_year","-");
                //  schterm=prefs.getString("school_term","-");

                // domain=stuobj.domain;
                // schyear=stuobj.reg_year;
                // schterm=stuobj.sch_term;

                URL url=new URL("https://api2.gradelogics.com/api/utility/message/markread/"+ meTeacher.id +"/"+ msgid +"/"+ meTeacher.domain + "/" + apikey);
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


            return msgid;
        }

        @Override
        protected void onPostExecute(String res)
        {
            new loadMessage().execute(msgid);
        }
    }

    private class loadMessage extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

       // ProgressDialog progressDialog=new ProgressDialog(view_teacher_message.this);
        @Override
        protected  void onPreExecute()
        {
            super.onPreExecute();
         //   progressDialog.setTitle("Loading. Just a sec.");
           // progressDialog.show();
            Toast.makeText(getApplicationContext(),"Getting messages",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");

                URL url=new URL("https://api2.gradelogics.com/api/teacher/get_message/" + params[0] + "/" + msg_type + "/" + meTeacher.id + "/" + meTeacher.domain + "/" + apikey);

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

            try {

                SharedPreferences.Editor editor=getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                // msgfrom.setText(jObject.getString("msgSender"));
                // msgdate.setText(jObject.getString("msgDate"));
                // fromID=jObject.getInt("cfromID");
//                 msgbody.setText(jObject.getString("cComment"));
                messageList=new ArrayList<msgObg>();

                JSONArray thread=new JSONArray(res);
                for (int x=0;x<thread.length();x++) {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject sObj = thread.getJSONObject(x);
                    //  Log.e("array el",sObj.getString("LoggedByName"));

                    msgObg msg_item=new msgObg(sObj.getString("msgDate"),"-1", URLDecoder.decode(sObj.getString("cComment"), "UTF-8"),sObj.getString("msgSender"),true,1);

                    if (sObj.getString("msgSender").equals("Me"))
                        full_body += "<div class='box_out sb2'>";
                    else
                        full_body += "<div class='box sb1'>";
                    full_body += "<span style='font-weight:normal;font-size:10pt'><b>" + sObj.getString("msgSender") + "</b> </span> <span style='font-weight:normal;font-size:9pt;color:#333'> - " + sObj.getString("msgDate") + "</span><br>";
                    //full_body +=  sObj.getString("msgDate") + "<br><br>";
                    full_body += "<span style='font-weight:normal;font-size:11pt'>" + sObj.getString("cComment") + "</span>";
                    full_body += "</div><div style='height:5px;background-color:#ffffff;width:100%'></div>";
                    messageList.add(msg_item);
                }
                full_body+="</body>";
                mMessageAdapter = new messageListAdapter(getApplicationContext(), messageList);
                mMessageAdapter.notifyDataSetChanged();
                msgbody.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                msgbody.setAdapter(mMessageAdapter);
                //msgbody.loadData(full_body, "text/html; charset=utf-8", "UTF-8");
                //  msgbody.setText(Html.fromHtml(full_body, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

            }catch (Exception e)
            {
                Log.e("load msg exception",e.toString());
            }finally {

                msgbody.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (msgbody.getAdapter().getItemCount()>2) {
                        msgbody.smoothScrollToPosition(
                                msgbody.getAdapter().getItemCount() - 1);
                    }
                    }
                }, 100);
             /*  msgbody.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageCommitVisible(WebView view, String url) {
                        msgbody.pageDown(true);

                       // String javaScript ="javascript:function test(){alert('test');}";
                       // msgbody.loadUrl(javaScript);
                    }
                });;
                */

            }
        }
    }

}
