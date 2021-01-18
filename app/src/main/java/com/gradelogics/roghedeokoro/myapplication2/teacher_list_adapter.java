package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class teacher_list_adapter extends ArrayAdapter<teacher> {
ArrayList<teacher> dataSet;
Context mContext;
String current_view;


    public teacher_list_adapter(ArrayList<teacher> data, Context context, String currentView) {
        super(context, R.layout.student_item, data);
        this.dataSet = data;
        this.mContext=context;
        current_view=currentView;

    }

    private static class ViewHolder {
        TextView txtTeacherName;
        LinearLayout ly;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final teacher dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.teacher_item, parent, false);
            viewHolder.txtTeacherName = (TextView) convertView.findViewById(R.id.txt_teacherName);
            viewHolder.ly = (LinearLayout) convertView.findViewById(R.id.ly);
            result = convertView;

            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


       viewHolder.txtTeacherName.setText(dataModel.fullname);
        viewHolder.ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tecid",String.valueOf(dataModel.id));
                Intent intent = new Intent(mContext, view_msg_Activity.class);
                intent.putExtra("msg_date","Today");
                intent.putExtra("msgtype","new");
                intent.putExtra("msg_body","");
                intent.putExtra("msg_from",dataModel.fullname);
                intent.putExtra("msg_id",String.valueOf(dataModel.id));
                intent.putExtra("teacherID",String.valueOf(dataModel.id));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getContext().startActivity(intent);

            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}
