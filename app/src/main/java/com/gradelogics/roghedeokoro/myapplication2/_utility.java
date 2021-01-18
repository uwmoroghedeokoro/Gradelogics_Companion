package com.gradelogics.roghedeokoro.myapplication2;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.ContentValues.TAG;

public class _utility {
    public static void subscribe_topic(String topic_name)
    {

        FirebaseMessaging.getInstance().subscribeToTopic(topic_name)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      //  String msg = getString(R.string.msg_subscribed);
                        String msg = "Subscibed";
                        if (!task.isSuccessful()) {
                           msg="Subscribe failed";
                        }
                        Log.e("TAG", "subscribe result : " + (msg));
                      //  Toast.makeText(, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
