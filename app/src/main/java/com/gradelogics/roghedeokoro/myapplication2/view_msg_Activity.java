package com.gradelogics.roghedeokoro.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class view_msg_Activity extends AppCompatActivity {

    student stuobj;
    String current_view="new_message";
    RecyclerView msgbody;
    TextView msgdate;
    TextView msgfrom;
    teacher meTeacher;
    Button btnSend;
    Button btnattach;
    int fromID;
    String msgID;
    EditText msgb;
    String from_contact_name;
    String full_body;
    private messageListAdapter mMessageAdapter;;

    String[] file_num=new String[5] ;
    int current_file_index=0;
    ArrayList<TextView>textview_num=new ArrayList<TextView>();

    LinearLayout attach_1,attach_2,attach_3,attach_4,attach_5,btnsubmit;

    ArrayList<TextView> textview_add_num=new ArrayList<>();
    ArrayList<LinearLayout> linear_add_num=new ArrayList<>();

    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2,PICK_DOCUMENT=3;

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private ArrayList<msgObg>messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_msg);

        Intent intent=getIntent();

        from_contact_name=intent.getStringExtra("msg_from");
        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("student_object","");
       // Log.e("jsonfrmclass",json);

        stuobj = gson.fromJson(json, student.class);

        NumberFormat format=NumberFormat.getCurrencyInstance();

        messageList=new ArrayList<msgObg>();

        checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);

        checkPermission(Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE);

        String msgDate=intent.getStringExtra("msg_date");
        String msgBody=intent.getStringExtra("msg_body");
        String msgFrom=intent.getStringExtra("msg_from");
        msgID=intent.getStringExtra("msg_id");

        msgbody=(RecyclerView) findViewById(R.id.msg_body);
        mMessageAdapter = new messageListAdapter(this, messageList);

        LinearLayoutManager llm = new LinearLayoutManager(this);
       // llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        msgbody.setLayoutManager(llm);
        msgbody.setAdapter(mMessageAdapter);


       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

      /*  msgbody.getSettings().setJavaScriptEnabled(true);
        msgbody.setWebChromeClient(new WebChromeClient() {
            //Other methods for your WebChromeClient here, if needed..

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        msgbody.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url){
               // String javaScript ="javascript:(function() {alert();})()";
               // msgbody.loadUrl(javaScript);

             //   String javaScript ="javascript:function test(){alert('test');}";
              //  msgbody.loadUrl(javaScript);
            }
        });
*/
        // msgdate=(TextView)findViewById(R.id.msg_date);
        // msgfrom=(TextView)findViewById(R.id.msg_from);
        msgb=(EditText)findViewById(R.id.edt_new_msg);
        btnSend=(Button) findViewById(R.id.btn_send);
        btnattach=(Button)findViewById(R.id.btn_attach);

        btnattach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_file_index = 0;
                selectImage(0);
            }
        });
       // TextView txtletter=(TextView)findViewById(R.id.txtLetter);

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

      //  ux_int();

        if (msgID.equals("-1"))
        {
           // msgfrom.setText(msgFrom);
           // msgdate.setText(msgDate);
            fromID=Integer.valueOf(intent.getStringExtra("teacherID"));
        }else
            new mark_as_read().execute(msgID);

        LocalBroadcastManager.getInstance(this).registerReceiver(aLBReceiver,
                new IntentFilter("in_message"));

        //txtletter.setText(msgFrom.substring(0,1));

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //toolbar.setTitle("Profile");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

       getSupportActionBar().setTitle(intent.getStringExtra("msg_from"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_msg_Activity.super.onBackPressed();
            }
        });




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


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this,
                    new String[] { permission },
                    requestCode);
        }
        else {

        }
    }

    // Select image from camera and gallery
    private void selectImage(final int active_file) {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            int hasStoragePerm = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Document","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                Log.e("status","lets build popup");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intentc = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            intentc.putExtra("active_file",active_file);
                            startActivityForResult(intentc, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickPhoto.putExtra("active_file",active_file);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);

                        } else if (options[item].equals("Document")) {
                            dialog.dismiss();
                           // Intent intent=getFileChooserIntent();
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setType("application/pdf");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.putExtra("active_file",active_file);
                            startActivityForResult(Intent.createChooser(intent, "Select a PDF "), PICK_DOCUMENT);

                          //  startActivityForResult(intent, PICK_DOCUMENT);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } catch (Exception e) {
            // Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            Log.e("error",e.getMessage());
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        int active_file_index=current_file_index;
        Log.e("activeindex",String.valueOf(active_file_index));
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                // Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/", "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                file_num[active_file_index] = destination.getAbsolutePath();
                //File f = new File(imgPath);
                //textview_num.get(active_file_index).setText(destination.getName());
                //linear_add_num.get(active_file_index).setBackgroundColor(Color.parseColor("#e03865"));
                //textview_add_num.get(active_file_index).setText("REMOVE");
                // imageview.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", selectedImage.getScheme());

                imgPath = getRealPathFromURI(selectedImage);
                Log.e("attach image", imgPath);
                destination = new File(imgPath.toString());
                file_num[active_file_index] = destination.getAbsolutePath();
                // File f = new File(imgPath);
                //textview_num.get(active_file_index).setText(destination.getName());
                //linear_add_num.get(active_file_index).setBackgroundColor(Color.parseColor("#a83a32"));
                //textview_add_num.get(active_file_index).setText("REMOVE");
                Log.e("doc path",destination.getAbsolutePath());
                //imageview.setImageBitmap(bitmap);

            } catch (Exception e) {
               Log.e("attach error",e.toString());
            }
        } else if (requestCode == PICK_DOCUMENT  && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedDocument = data.getData();
            try {
                Log.e("attach path", selectedDocument.getPath());
               // imgPath=RealPathUtil.getRealPath(getApplicationContext(),selectedDocument);
                imgPath=FileUtils.getPath(getApplicationContext(),selectedDocument);
               // imgPath = getPath(getApplicationContext(),selectedDocument);

              //
                  destination = new File(imgPath.toString());
              //  file_num[active_file_index] = destination.getAbsolutePath();
                Log.e("doc name",destination.getAbsolutePath());


            } catch (Exception e) {
                Log.e("attach error",e.toString());
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take Photo")) {
                                dialog.dismiss();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PICK_IMAGE_CAMERA);
                            } else if (options[item].equals("Choose From Gallery")) {
                                dialog.dismiss();
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
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
        String js="<script>function push_incoming(sender_name,sender_text,msg_date){document.body.innerHTML+=\"<div class='box sb1'><span style='font-weight:normal;font-size:10pt'><b>\" +sender_name+ \"</b></span> - <span style='font-weight:normal;font-size:9pt;color:#333'>\" + msg_date + \"</span><br><span style='font-weight:normal;font-size:11pt'>\" +sender_text+ \"</span></div><div style='height:5px;background-color:#ffffff;width:100%'></div>\";window.scrollTo(0,document.body.scrollHeight); } \n" ;
         js+="function push_outgoing(sender_text,msg_date){document.body.innerHTML+=\"<div class='box_out sb2'><span style='font-weight:normal;font-size:10pt'><b>Me</b></span> - <span style='font-weight:normal;font-size:9pt;color:#333'>\" + msg_date + \"</span><br><span style='font-weight:normal;font-size:11pt'>\" +sender_text+ \"</span></div><div style='height:5px;background-color:#ffffff;width:100%'></div>\";window.scrollTo(0,document.body.scrollHeight); }</script>" ;

        full_body+=js;
        full_body +="<body>";
    }
    private BroadcastReceiver aLBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // perform action here.
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd HH:mm", Locale.getDefault());
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
                  //  msgbody.loadUrl("javascript:push_incoming('" + from_contact_name + "','" + msg_text + "','" + formattedDate + "')");
                  //  msgbody.pageDown(true);
                }
            }
               //push_msg_to_list(msg_sender_id,msg_text);

           // new loadMessage().execute(msgID);
        }
    };


    private void push_msg_to_list(String sender_id,String sender_msg)
    {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd HH:mm", Locale.getDefault());
        String formattedDate = df.format(c);

       ;//+ "<p>" + full_body + "</p><br><div style='height:1px;background-color:#ededed;width:100%'></div><br>";

        full_body += "<div class='box sb1'>";
        full_body += "<span style='font-weight:normal;font-size:10pt'><b>" + from_contact_name + "</b> </span> <span style='font-weight:normal;font-size:9pt;color:#333'> - " + formattedDate + "</span><br>";
        //full_body +=  sObj.getString("msgDate") + "<br><br>";
        full_body += "<span style='font-weight:normal;font-size:11pt'>" + sender_msg+ "</span>";
        full_body += "</div><div style='height:5px;background-color:#ffffff;width:100%'></div>";

      //  msgbody.loadData(full_body, "text/html; charset=utf-8", "UTF-8");
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
                String msgid=params[0];
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");
               // studentid=(prefs.getInt("student_id",0));
               // domain=prefs.getString("domain","-");
                //  schyear=prefs.getString("reg_year","-");
                //  schterm=prefs.getString("school_term","-");

                // domain=stuobj.domain;
                // schyear=stuobj.reg_year;
                // schterm=stuobj.sch_term;

                URL url=new URL("https://api2.gradelogics.com/api/utility/message/markread/"+ String.valueOf(stuobj.id) +"/"+ msgid +"/"+ stuobj.domain + "/" + apikey);
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

            Log.e("sibling json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            new loadMessage().execute(msgID);
        }


    }
    private class loadMessage extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

        //ProgressDialog progressDialog=new ProgressDialog(view_msg_Activity.this);
        @Override
        protected  void onPreExecute()
        {
            super.onPreExecute();
           // progressDialog.setTitle("Loading. Just a sec.");
          //  progressDialog.show();
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

                URL url=new URL("https://api2.gradelogics.com/api/student/get_message/" + params[0] + "/" + stuobj.id + "/" + stuobj.domain + "/" + apikey);

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
                SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
                apikey=prefs.getString("apikey","");
                Gson gson=new Gson();
                String json=prefs.getString("login_object","");
            //    String gbjson=prefs.getString("active_gradebook","");
               // gradeBook gB=gson.fromJson(gbjson,gradeBook.class);

               // teacher meTeacher=gson.fromJson(json,teacher.class);
//Log.e("send from",String.valueOf(msg_body));
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

                    url = new URL("https://api2.gradelogics.com/api/student/send_message/");
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
                    dos.writeBytes("Content-Disposition: form-data; name=\"studentID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(stuobj.id));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"domain\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(stuobj.domain);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"messageBody\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(msg_body).replace("\"","'"));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"toID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(msgID));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"rootID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(-1));
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
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
               // msgbody.loadUrl("javascript:push_outgoing('" + msg_body.replace("'", "`").replace("\"", "`") + "','" + formattedDate + "')");
              // msgbody.pageDown(true);
           }else
           {
               Toast.makeText(getApplicationContext(), "Error while sending. Please check internet connection", Toast.LENGTH_LONG).show();
           }

         //       new loadMessage().execute(msgID);
        }
    }

}
