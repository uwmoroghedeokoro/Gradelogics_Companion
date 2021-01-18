package com.gradelogics.roghedeokoro.myapplication2;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class teacher_new_homework extends AppCompatActivity {

    Spinner subjectS,classS,standardS,categoryS;
    int active_subject,active_class=-1;
    StringWithTag selected_subject,selected_class,selected_standard,selected_category;
    teacher meTeacher;
    gradeBook gb;
    final Calendar myCalendar = Calendar.getInstance();

    String[] file_num=new String[5] ;
    int current_file_index=0;
    ArrayList<TextView>textview_num=new ArrayList<TextView>();
    ArrayAdapter<StringWithTag> adap;
    ArrayAdapter<StringWithTag> adap2;
    LinearLayout attach_1,attach_2,attach_3,attach_4,attach_5,btnsubmit;

    ArrayList<TextView> textview_add_num=new ArrayList<>();
    ArrayList<LinearLayout> linear_add_num=new ArrayList<>();

    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private String student_id;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_new_homework);

        SharedPreferences prefs=getSharedPreferences("gradelogics",0);
        final SharedPreferences.Editor editor=prefs.edit();
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        active_subject= prefs.getInt("active_subject",-1);
        active_class=prefs.getInt("active_class",-1);
        meTeacher=gson.fromJson(json,teacher.class);
        gb=gson.fromJson(prefs.getString("active_gradebook",""),gradeBook.class);

        subjectS=(Spinner)findViewById(R.id.spin_hme_subject);
        classS=(Spinner)findViewById(R.id.spin_hme_class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Publish New Homework");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacher_new_homework.super.onBackPressed();
            }
        });

        final EditText edt_hme_title=(EditText)findViewById(R.id.edt_hme_title);
        final EditText edt_hme_due=(EditText)findViewById(R.id.edt_hme_date);
        final EditText edt_hme_body=(EditText)findViewById(R.id.edt_hme_body);

        Button btn_publish=(Button)findViewById(R.id.btn_continue);
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean all_good=true;

                if (edt_hme_due.getText().toString().equals(""))
                {
                    all_good=false;
                    edt_hme_due.setError("Due Date Required");
                }
                if (edt_hme_title.getText().toString().equals(""))
                {
                    all_good=false;
                    edt_hme_title.setError("Title Required");
                }

                if (all_good)
                new publish_homework().execute("https://api2.gradelogics.com/api/teacher/homework/publish/",edt_hme_title.getText().toString(),edt_hme_due.getText().toString(),edt_hme_body.getText().toString());
            }
        });

        file_num[0]=""; file_num[1]=""; file_num[2]=""; file_num[3]=""; file_num[4]="";

        attach_1=(LinearLayout)findViewById(R.id.attach1);
        attach_2=(LinearLayout)findViewById(R.id.attach2);
        attach_3=(LinearLayout)findViewById(R.id.attach3);
        attach_4=(LinearLayout)findViewById(R.id.attach4);
        attach_5=(LinearLayout)findViewById(R.id.attach5);

        LinearLayout ll_add1=(LinearLayout)findViewById(R.id.txt_ll_add1);
        linear_add_num.add(ll_add1);
        LinearLayout ll_add2=(LinearLayout)findViewById(R.id.txt_ll_add2);
        linear_add_num.add(ll_add2);
        LinearLayout ll_add3=(LinearLayout)findViewById(R.id.txt_ll_add3);
        linear_add_num.add(ll_add3);
        LinearLayout ll_add4=(LinearLayout)findViewById(R.id.txt_ll_add4);
        linear_add_num.add(ll_add4);
        LinearLayout ll_add5=(LinearLayout)findViewById(R.id.txt_ll_add5);
        linear_add_num.add(ll_add5);


        TextView file_add1=(TextView)findViewById(R.id.txt_file_add1);
        textview_add_num.add(file_add1);
        TextView file_add2=(TextView)findViewById(R.id.txt_file_add2);
        textview_add_num.add(file_add2);
        TextView file_add3=(TextView)findViewById(R.id.txt_file_add3);
        textview_add_num.add(file_add3);
        TextView file_add4=(TextView)findViewById(R.id.txt_file_add4);
        textview_add_num.add(file_add4);

        TextView txt1=(TextView)findViewById(R.id.txt_file_title1);
        textview_num.add(txt1);
        TextView txt2=(TextView)findViewById(R.id.txt_file_title2);
        textview_num.add(txt2);
        TextView txt3=(TextView)findViewById(R.id.txt_file_title3);
        textview_num.add(txt3);
        TextView txt4=(TextView)findViewById(R.id.txt_file_title4);
        textview_num.add(txt4);


        checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);

        checkPermission(Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE);

        attach_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("file_num",file_num[0]);
                if (file_num[0] =="") {
                    current_file_index = 0;
                    selectImage(0);
                }else
                {
                    linear_add_num.get(0).setBackgroundColor(Color.parseColor("#1cd453"));
                    textview_add_num.get(0).setText("ADD");
                    file_num[0]="";
                    textview_num.get(0).setText("Attachment");

                }
            }
        });
        attach_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file_num[1] =="") {
                    current_file_index = 1;
                    selectImage(1);
                }else
                {
                    linear_add_num.get(1).setBackgroundColor(Color.parseColor("#1cd453"));
                    textview_add_num.get(1).setText("ADD");
                    file_num[1]="";
                    textview_num.get(1).setText("Attachment");

                }
            }
        });
        attach_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file_num[2] =="") {
                    current_file_index = 2;
                    selectImage(2);
                }else
                {
                    linear_add_num.get(2).setBackgroundColor(Color.parseColor("#1cd453"));
                    textview_add_num.get(2).setText("ADD");
                    file_num[2]="";
                    textview_num.get(2).setText("Attachment");

                }
            }
        });
        attach_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file_num[3] =="") {
                    current_file_index = 3;
                    selectImage(3);
                }else
                {
                    linear_add_num.get(3).setBackgroundColor(Color.parseColor("#1cd453"));
                    textview_add_num.get(3).setText("ADD");
                    file_num[3]="";
                    textview_num.get(3).setText("Attachment");

                }
            }
        });

        loadGUI();

    }

    private void loadGUI()
    {
       final EditText assign_date=(EditText)findViewById(R.id.edt_hme_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                assign_date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        assign_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(teacher_new_homework.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        ArrayList<StringWithTag> subjectlist=new ArrayList<>();
        ArrayList<StringWithTag>classroomlist=new ArrayList<>();

        // StringWithTag blank_subject = new StringWithTag();

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


        adap = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_black, subjectlist);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectS.setAdapter(adap);


//set selection
        for (int s=0;s<adap.getCount();s++){
            StringWithTag swT=adap.getItem(s);
            if (swT.tag==active_subject){
                //  subjectS.setSelection(s);
            }
        }

        adap2 = new ArrayAdapter<StringWithTag> (getApplicationContext(), R.layout.spinner_item_large_black, classroomlist);
        adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classS.setAdapter(adap2);
    }

    // Function to check and request permission.
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
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
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
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickPhoto.putExtra("active_file",active_file);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
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
                textview_num.get(active_file_index).setText(destination.getName());
                linear_add_num.get(active_file_index).setBackgroundColor(Color.parseColor("#e03865"));
                textview_add_num.get(active_file_index).setText("REMOVE");
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
                Log.e("Activity", "Pick from Gallery::>>> ");

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                file_num[active_file_index] = destination.getAbsolutePath();
                // File f = new File(imgPath);
                textview_num.get(active_file_index).setText(destination.getName());
                linear_add_num.get(active_file_index).setBackgroundColor(Color.parseColor("#a83a32"));
                textview_add_num.get(active_file_index).setText("REMOVE");
                // Log.e("img name",f.getName());
                //imageview.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
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



    public class publish_homework extends AsyncTask<String, Integer, String> {

        ProgressDialog progressBar= new ProgressDialog(teacher_new_homework.this);


        boolean success=true;

        public publish_homework(){
            //set context variables if required

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setMin(0);
            String hl;
            progressBar.setTitle("Busy");
            progressBar.setMessage("Publishing....");
            progressBar.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            File sourceFile;// = new File(file_num[0]);
            DataOutputStream dataOutputStream;

            // int bytesRead, bytesAvailable, bufferSize;
            // byte[] buffer;
            // int maxBufferSize = 1 * 1024 * 1024;
            //DataOutputStream dos = null;

            String urlString = params[0]; // URL to call
            String hme_title = params[1]; //data to post
            String hme_due = params[2]; //data to post
            String hme_body = params[3]; //data to post
           // Log.e("data to post",data);
            OutputStream out = null;
            String ret="";
            URL url;
            HttpURLConnection conn;// = (HttpURLConnection) url.openConnection();
            //  File sourceFile = new File(file_num[0]);

            int selected_subject_id,selected_class_id;
            StringWithTag swT=adap.getItem(subjectS.getSelectedItemPosition());
            selected_subject_id=swT.tag;

            StringWithTag swT2=adap2.getItem(classS.getSelectedItemPosition());
            selected_class_id=swT2.tag;
            ///
            String Tag="submit";
            try
            {
                Log.e(Tag,"Starting Http File Sending to URL");

                //  FileInputStream fileInputStream = new FileInputStream(sourceFile);

                url = new URL(urlString);
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
                dos.writeBytes(String.valueOf(gb.ID));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(20);

                dos.writeBytes("Content-Disposition: form-data; name=\"teacherID\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(meTeacher.id));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(40);

                dos.writeBytes("Content-Disposition: form-data; name=\"domain\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(meTeacher.domain);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(60);

                dos.writeBytes("Content-Disposition: form-data; name=\"subjectID\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(selected_subject_id));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(80);

                dos.writeBytes("Content-Disposition: form-data; name=\"classID\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(selected_class_id));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(80);

                dos.writeBytes("Content-Disposition: form-data; name=\"homework_title\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(hme_title));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(80);

                dos.writeBytes("Content-Disposition: form-data; name=\"due_date\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(hme_due));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(80);

                dos.writeBytes("Content-Disposition: form-data; name=\"homework_body\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(hme_body));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(80);

                //file start
                for (int x=0;x<5;x++) {
                    if (file_num[x] !="") {

                        String filename="file"+x;
                        sourceFile=new File(file_num[x]);
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        Log.e("file to up",sourceFile.getName() + "-"+filename);

                        dos.writeBytes("Content-Disposition: form-data; name=\""+filename + "\";filename=\"" + sourceFile.getName() + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        Log.e(Tag, "Headers are written");

                        // create a buffer of maximum size
                        int bytesAvailable = fileInputStream.available();

                        int maxBufferSize = 1024;
                        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        byte[] buffer = new byte[bufferSize];

                        // read file and write it into form...
                        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);


                        // close streams
                        fileInputStream.close();

                        //file END
                    }//END IF
                }// NEXT
                dos.flush();

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));
                publishProgress(100);

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
            return ret;

        }

        @Override
        protected void onPostExecute(String ret)
        {

            if(ret.equals("success"))
            {
                progressBar.dismiss();
                Intent intent=new Intent(getApplicationContext(),Teacher_dash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                //intent.putExtra("MESSAGE",message);
               // setResult(99,intent);
               startActivity(intent);
            }
            if (!success)
                Toast.makeText(getApplicationContext(),"An error occurred. Please try again",Toast.LENGTH_LONG).show();
        }


    }

}
