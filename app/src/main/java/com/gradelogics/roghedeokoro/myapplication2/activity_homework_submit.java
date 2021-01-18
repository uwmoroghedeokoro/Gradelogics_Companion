package com.gradelogics.roghedeokoro.myapplication2;

import android.Manifest;
import android.content.ContentValues;
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
import androidx.core.content.FileProvider;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class activity_homework_submit extends AppCompatActivity {

    String home_id;
    String home_title;
    String domain;
    EditText comment;

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
    private String student_id;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_submit);

        Intent intent=getIntent();
        home_id=intent.getStringExtra("home_id");
        home_title=intent.getStringExtra("home_title");

        SharedPreferences prefs=getSharedPreferences("gradelogics",MODE_PRIVATE);
        student_id= String.valueOf(prefs.getInt("selected_studentid",0));
        domain= prefs.getString("domain","");

        TextView hometitle=(TextView)findViewById(R.id.txt_home_title);
        hometitle.setText(home_title);

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
        TextView file_add5=(TextView)findViewById(R.id.txt_file_add5);
        textview_add_num.add(file_add5);


        TextView txt1=(TextView)findViewById(R.id.txt_file_title1);
        textview_num.add(txt1);
        TextView txt2=(TextView)findViewById(R.id.txt_file_title2);
        textview_num.add(txt2);
        TextView txt3=(TextView)findViewById(R.id.txt_file_title3);
        textview_num.add(txt3);
        TextView txt4=(TextView)findViewById(R.id.txt_file_title4);
        textview_num.add(txt4);
        TextView txt5=(TextView)findViewById(R.id.txt_file_title5);
        textview_num.add(txt5);

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
        attach_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file_num[4] =="") {
                    current_file_index = 4;
                    selectImage(4);
                }else
                {
                    linear_add_num.get(4).setBackgroundColor(Color.parseColor("#1cd453"));
                    textview_add_num.get(4).setText("ADD");
                    file_num[4]="";
                    textview_num.get(4).setText("Attachment");

                }
            }
        });

        btnsubmit=(LinearLayout)findViewById(R.id.btn_submit);
        comment=(EditText)findViewById(R.id.home_commet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Submit Your Work");
        // toolbar.setNavigationIcon(R.drawable.circle);
        setSupportActionBar(toolbar);

        // getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_homework_submit.super.onBackPressed();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TO DELETE
               // home_id="9";
               // student_id="2013917";
              //  domain="sav";
                //////

                final HashMap<String,String>params=new HashMap<>();
                params.put("homeworkid",home_id);
                params.put("domain",domain);
                params.put("studentid",student_id);
                params.put("comment",comment.getText().toString());

                final StringBuilder sbParams = new StringBuilder();
                int i = 0;
                for (String key : params.keySet()) {
                    try {
                        if (i != 0){
                            sbParams.append("&");
                        }
                        sbParams.append(key).append("=")
                                .append(URLEncoder.encode(params.get(key), "UTF-8"));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    i++;
                }

                new CallAPI2().execute("https://api2.gradelogics.com/api/student/homework/submit",sbParams.toString());
            }
        });

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

                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File

                            }
                            // Continue only if the File was successfully created
                         //   if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(activity_homework_submit.this,
                                        "com.example.roghedeokoro.fileprovider",
                                        photoFile);



                            Intent intentc = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            intentc.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
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


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        int active_file_index=current_file_index;
        Log.e("activeindex",String.valueOf(active_file_index));
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {


               /* Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

               // Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/", "IMG_" + timeStamp + ".png");
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
                }*/

                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));

                Log.e("currentPhotoPath",mCurrentPhotoPath);
                file_num[active_file_index] = mCurrentPhotoPath; // destination.getAbsolutePath();

               //file_num[active_file_index]=getRealPathFromURI(imageURI);


                //File f = new File(imgPath);
                textview_num.get(active_file_index).setText(file.getName());
                linear_add_num.get(active_file_index).setBackgroundColor(Color.parseColor("#e03865"));
                textview_add_num.get(active_file_index).setText("REMOVE");
               // imageview.setImageBitmap(bitmap);

            } catch (Exception e) {
               Log.e("capture exception",e.toString());
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
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




    public class CallAPI2 extends AsyncTask<String, Integer, String> {

        ProgressBar progressBar=(ProgressBar)findViewById(R.id.prgBar);

        boolean success=true;

        public CallAPI2(){
            //set context variables if required

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setMin(0);
            String hl;
            progressBar.setVisibility(View.VISIBLE);
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
            String data = params[1]; //data to post
            Log.e("data to post",data);
            OutputStream out = null;
            String ret="";
            URL url;
            HttpURLConnection conn;// = (HttpURLConnection) url.openConnection();
          //  File sourceFile = new File(file_num[0]);


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
                dos.writeBytes("Content-Disposition: form-data; name=\"homeworkid\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(home_id);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(20);

                dos.writeBytes("Content-Disposition: form-data; name=\"studentid\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(student_id);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(40);

                dos.writeBytes("Content-Disposition: form-data; name=\"domain\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(domain);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                publishProgress(60);

                dos.writeBytes("Content-Disposition: form-data; name=\"comment\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(comment.getText().toString());
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
                Intent intent=new Intent();
                //intent.putExtra("MESSAGE",message);
                setResult(99,intent);
                finish();//finishing activity
            }
            if (!success)
                Toast.makeText(getApplicationContext(),"An error occurred. Please try again",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            Log.e("Progress", String.valueOf(values[0]));
            progressBar.setProgress(values[0]);
        }
    }

}
