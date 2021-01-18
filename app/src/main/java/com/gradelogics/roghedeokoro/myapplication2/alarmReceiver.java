package com.gradelogics.roghedeokoro.myapplication2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class alarmReceiver extends BroadcastReceiver {

    Context cx;
    NotificationCompat.InboxStyle inboxStyle; //new NotificationCompat.InboxStyle()
    ArrayList<student>sblings;

    @Override
    public void onReceive(Context context, Intent intent) {
      //  Toast.makeText(context,"ALRAM",Toast.LENGTH_SHORT).show();
        cx=context;
        inboxStyle= new NotificationCompat.InboxStyle();
       // new getSiblings().execute("");
    }

    private class getSiblings extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;

       // ProgressDialog progressDialog=new ProgressDialog(main_dash.this);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
         sblings=new ArrayList<student>();
        }
        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {

                String domain,schyear,apikey,schterm="";
                int studentid=0;
                SharedPreferences prefs=cx.getSharedPreferences("gradelogics",MODE_PRIVATE);
                studentid=(prefs.getInt("student_id",0));
                domain=prefs.getString("domain","-");
                apikey=prefs.getString("apikey","");
                //  schyear=prefs.getString("reg_year","-");
                //  schterm=prefs.getString("school_term","-");

                // domain=stuobj.domain;
                // schyear=stuobj.reg_year;
                // schterm=stuobj.sch_term;

                URL url=new URL("https://api2.gradelogics.com/api/student/siblings/"+ String.valueOf(studentid) +"/"+ domain+"/"+apikey);
                urlConnection=(HttpURLConnection)url.openConnection();
                Log.e("get sibling",url.toString());
                int code = urlConnection.getResponseCode();

                Log.e("statusCode",String.valueOf(code));

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
            final StringBuilder sibling_pics=new StringBuilder();
            final StringBuilder sibling_ids=new StringBuilder();
            final StringBuilder sibling_pwd=new StringBuilder();
            // final Arra
            try {


                //ArrayList<student> sblings=new ArrayList<>();
                DecimalFormat value = new DecimalFormat("#.#");
                value.format(2.16851);


                JSONArray jArray=new JSONArray(res);
                SharedPreferences.Editor editor=cx.getSharedPreferences("gradelogics",MODE_PRIVATE).edit();

                for (int x=0;x<jArray.length();x++)
                {
                    // Log.e("array el",jArray.getJSONObject(x).toString());
                    JSONObject jObj=jArray.getJSONObject(x);
                    // subject nSubject=new subject(jObj.getString("subjectName"),jObj.getString("TeacherName"),jObj.getString("termAvg"),jObj.getString("subjectID"));
                    // dataSet.add(nSubject);
                    // String.format("%.2f", d);

                    student stu=new student();
                    stu.fullname=jObj.getString("cFullName");
                    stu.domain=jObj.getString("ConnString");
                    stu.department=jObj.getString("cDepartmentName");
                    stu.term_avg=String.format("%.1f",jObj.getDouble("termAvg"));
                    stu.excel=(jObj.getInt("excel"));
                    stu.struggle=(jObj.getInt("struggle"));
                    stu.id=(jObj.getInt("cStudentID"));
                    stu.img_url=jObj.getString("cPicFile");
                    stu.reg_year=jObj.getString("cRegYear");
                    stu.sch_term=jObj.getString("currentTerm");
                    stu.balance=Float.valueOf(String.format("%.2f",(jObj.getDouble("Balance"))));
                    double ovr=(jObj.getDouble("_OverallAvg"));
                    stu._OverallAvg=String.valueOf(value.format(ovr));
                    stu.grade_level=jObj.getString("gradeLevel");
                    stu.homework_count=Integer.valueOf(jObj.getInt("homework_count"));
                    stu.message_count=Integer.valueOf(jObj.getInt("message_count"));
                    Log.e("jon student",stu.fullname);

                    sblings.add(stu);
                }


                //   view.notify();
            }catch (Exception e)
            {
                Log.e("error",e.getMessage());
            }finally {
notify_me();
            }
        }
    }

    void notify_me()
    {
        //check if close of day
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        int dayOfweek=rightNow.get(Calendar.DAY_OF_WEEK);

        int currentHourIn12Format = rightNow.get(Calendar.HOUR);
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


       if ((dayOfweek>1&& dayOfweek<7))
        {
            Log.e("notify","yes");


            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

            final String NOTIFICATION_CHANNEL_ID = "channel_id";

            for (student schild:sblings) {

               // if (schild.homework_count>0 || schild.message_count >0)
                NotificationCompat.Builder builder = new NotificationCompat.Builder(cx, NOTIFICATION_CHANNEL_ID);
                builder.setContentTitle( "Daily Gradelogics Update: " );
                builder.setContentText("See what " +schild.fullname + " has been up to." );
                builder.setSmallIcon(R.drawable.gradelogicsappicon);
                builder.setLargeIcon(BitmapFactory.decodeResource(cx.getResources(), R.drawable.gradelogicsappicon));
                builder.setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Upcoming Homework: " + schild.homework_count)
                        .addLine("Unread Messages: " + schild.message_count)
                        .addLine("Current Term Avg: " + schild.term_avg));

                // Unique identifier for notification
                final int NOTIFICATION_ID = schild.id;
                //This is what will will issue the notification i.e.notification will be visible

                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

                Intent intent = new Intent(cx, main_dash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(cx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
// Get RemoteView and id's needed



//Following will set the tap action
                builder.setContentIntent(pendingIntent);

                Notification notification = builder.build();

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(cx);




                // Use Picasso with RemoteViews to load image into a notification

                // This is the Notification Channel ID. More about this in the next section
                //final String NOTIFICATION_CHANNEL_ID = "channel_id";
//User visible Channel Name
                final String CHANNEL_NAME = "Notification Channel";
// Importance applicable to all the notifications in this Channel
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
//Notification channel should only be created for devices running Android 26

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
                    //Boolean value to set if lights are enabled for Notifications from this Channel
                    notificationChannel.enableLights(true);
                    //Boolean value to set if vibration are enabled for Notifications from this Channel
                    notificationChannel.enableVibration(true);
                    //Sets the color of Notification Light
                    notificationChannel.setLightColor(Color.GREEN);
                    //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
                    notificationChannel.setVibrationPattern(new long[]{
                            500,
                            500,
                            500,
                            500,
                            500
                    });
                    //Sets whether notifications from these Channel should be visible on Lockscreen or not
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                    NotificationManager notificationManager = (NotificationManager) cx.getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationManager.createNotificationChannel(notificationChannel);
                    }


                }

             /*   final RemoteViews contentView = notification.contentView;
                Picasso.get()
                        .load(schild.img_url)
                        .fit()
                        .centerInside()
                        .into(contentView,android.R.id.icon,NOTIFICATION_ID,notification);

              */
                notificationManagerCompat.notify(NOTIFICATION_ID, notification);

                Log.e("notify", "me");
            }//next
        }

    }
}
