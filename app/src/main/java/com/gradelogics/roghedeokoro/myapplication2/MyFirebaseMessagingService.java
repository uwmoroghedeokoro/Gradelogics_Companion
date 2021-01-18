package com.gradelogics.roghedeokoro.myapplication2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String object_type="";
    String login_object="";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("firebase message",remoteMessage.toString());

        SharedPreferences prefs=getApplicationContext().getSharedPreferences("gradelogics",MODE_PRIVATE);
        object_type=prefs.getString("object_type","");
        login_object=prefs.getString("login_object","");

        Map<String,String> _body = remoteMessage.getData();

        Intent intent=new Intent("in_message");
        intent.putExtra("msg_title",_body.get("title"));
        intent.putExtra("msg_text",_body.get("text").replace("\\n",System.getProperty("line.separator")));
        intent.putExtra("msg_sender_id",_body.get("sender_id"));
        intent.putExtra("class_id",_body.get("class_id"));
        intent.putExtra("msg_id",_body.get("msg_id"));
        intent.putExtra("sender_name",_body.get("sender_name"));

      Log.e("objecttype",object_type);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        try {

            boolean _to_notify=true;
            if(object_type.equals("teacher"))
            {
                Gson gson=new Gson();
                teacher meTeacher=gson.fromJson(login_object,teacher.class);
                   if (meTeacher.id==Integer.valueOf(_body.get("sender_id")))
                       _to_notify=false;
            }else
            {
                Gson gson=new Gson();
                student meStudent=gson.fromJson(login_object,student.class);
                    if (meStudent.id==Integer.valueOf(_body.get("sender_id")))
                        _to_notify=false;
            }
            if (_to_notify)
                notify_me(remoteMessage);
        } catch (IOException e) {
           Log.e("notify error",e.toString());
        }
    }

    void notify_me(RemoteMessage remoteMessage) throws IOException {
        //check if close of day
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        int dayOfweek=rightNow.get(Calendar.DAY_OF_WEEK);

        Map<String,String> _body = remoteMessage.getData();

        int currentHourIn12Format = rightNow.get(Calendar.HOUR);
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


        {
            Log.e("notify","yes");


            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

            final String NOTIFICATION_CHANNEL_ID = "channel_id";

            {

                // if (schild.homework_count>0 || schild.message_count >0)
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
                builder.setContentTitle( _body.get("title"));
                if (_body.get("sender_name")==null)
                    builder.setContentText(Html.fromHtml(_body.get("text").replace("\\n",System.getProperty("line.separator"))));
                else
                    builder.setContentText(_body.get("sender_name")+": " + Html.fromHtml(_body.get("text").replace("\\n",System.getProperty("line.separator"))));

                builder.setSmallIcon(R.drawable.gradelogicslogo_g);
               //

               Intent intent;

                Bitmap bmp=null;
                if(object_type.equals("teacher"))
                {
                    Gson gson=new Gson();
                    teacher meTeacher=gson.fromJson(login_object,teacher.class);
                     bmp = Picasso.get().load(meTeacher.school_logo).get();
                     intent = new Intent(getApplicationContext(), Teacher_dash.class);
                }else
                {
                    Gson gson=new Gson();
                    student meStudent=gson.fromJson(login_object,student.class);
                    bmp = Picasso.get().load(meStudent.school_logo).get();
                     intent = new Intent(getApplicationContext(), main_dash.class);
                }
                builder.setLargeIcon(bmp);
                // Unique identifier for notification
                final int NOTIFICATION_ID = 101;
                //This is what will will issue the notification i.e.notification will be visible

                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
// Get RemoteView and id's needed



//Following will set the tap action
                builder.setContentIntent(pendingIntent);

                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());




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

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
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

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}

