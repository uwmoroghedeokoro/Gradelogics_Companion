package com.gradelogics.roghedeokoro.myapplication2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class class_studentAdapter extends ArrayAdapter<student> {

    private ArrayList<student> dataSet;
    Context mContext;
    String current_view;

    // View lookup cache
    private static class ViewHolder {
        TextView txtStudentName;
        TextView txtClass;
        TextView txtSubject;
        TextView txtAvg;
        CircleImageView cvPic;
        LinearLayout ly;

    }

    public class_studentAdapter(ArrayList<student> data, Context context,String currentView) {
        super(context, R.layout.student_item, data);
        this.dataSet = data;
        this.mContext=context;
        current_view=currentView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final student dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.student_item, parent, false);
            viewHolder.txtStudentName = (TextView) convertView.findViewById(R.id.txt_child_name);
            viewHolder.txtSubject = (TextView) convertView.findViewById(R.id.txt_child_subject);
            viewHolder.txtClass = (TextView) convertView.findViewById(R.id.txt_child_class);
            viewHolder.txtAvg = (TextView) convertView.findViewById(R.id.txt_avg);
            viewHolder.cvPic = (CircleImageView) convertView.findViewById(R.id.img_pic);
            viewHolder.ly = (LinearLayout) convertView.findViewById(R.id.ly_parent);
            result = convertView;

            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        viewHolder.txtSubject.setText(dataModel.subject);
        viewHolder.txtClass.setText(dataModel._classroom.className);
        viewHolder.txtStudentName.setText(dataModel.fullname);
        viewHolder.txtAvg.setText(dataModel.term_avg);

        if (current_view.equals("view_student")){
            viewHolder.ly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, class_view_student.class);
                    Gson gson = new Gson();
                    intent.putExtra("student_object", gson.toJson(dataModel));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
    }else if (current_view.equals("new_message"))
    {
       viewHolder.ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,view_teacher_message.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("msgtype","new");
                intent.putExtra("toID",String.valueOf(dataModel.id));
                intent.putExtra("msg_id",String.valueOf(dataModel.id));
                intent.putExtra("toName",dataModel.fullname);
                mContext.startActivity(intent);
              /*
                final Dialog dialog = new Dialog(mContext);
               // dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
               // dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
               // dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.string.app_name );
                dialog.setContentView(R.layout.ly_new_message);
                dialog.setTitle("To: " + dataModel.fullname);
                dialog.setCancelable(true);


                final EditText msg_body = (EditText) dialog.findViewById(R.id.edit_msg_body);
                TextView title_txt=(TextView)dialog.findViewById(R.id.txt_send_to);
                title_txt.setText("To: " + dataModel.fullname);
               // text.setText(msg);



                Button dialogButton = (Button) dialog.findViewById(R.id.btn_send);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new send_message().execute(msg_body.getText().toString(),String.valueOf(dataModel.id));
                        dialog.dismiss();
                    }
                });

                dialog.show();

               */
            }
        });

    }
        Picasso.get()
                .load(dataModel.img_url)
                .fit()
                .centerInside()
                .into(viewHolder.cvPic);

        // Return the completed view to render on screen
        return convertView;
    }

    private class send_message extends AsyncTask<String,String,String>
    {
        HttpURLConnection conn;
        String toID;
        boolean success=true;
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
                String msgbody=params[0];
                toID=params[1];
                SharedPreferences prefs=mContext.getSharedPreferences("gradelogics",MODE_PRIVATE);
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
                    Log.e(Tag,"Starting Http File Sending to URL");

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
                    dos.writeBytes(String.valueOf(msgbody));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"toID\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(toID));
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
                    Log.e(Tag, "IO error: " + ioe.getMessage().toString());
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
            //Toast.makeText(mContext.getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();
          //  msgb.setText("");
           // new view_teacher_message.loadMessage().execute(rootID);
        }
    }


}
