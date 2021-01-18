package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class class_assignment_view_adapter extends ArrayAdapter<student> {
    private ArrayList<student> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtStudent;
        TextView txtComment;
        TextView txtScore;
       // TextView txtClassAVG;

    }

    public class_assignment_view_adapter(ArrayList<student> data, Context context) {
        super(context, R.layout.class_grade_item, data);
        this.dataSet = data;
        this.mContext=context;

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
            convertView = inflater.inflate(R.layout.grade_view_item, parent, false);
            viewHolder.txtStudent = (TextView) convertView.findViewById(R.id.txt_student);
            viewHolder.txtComment = (TextView) convertView.findViewById(R.id.txt_comment);
            viewHolder.txtScore = (TextView) convertView.findViewById(R.id.txt_score);
          //  viewHolder.txtClassAVG=(TextView)convertView.findViewById(R.id.txt_classAvg);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtStudent.setText(dataModel.fullname);
        viewHolder.txtScore.setText(dataModel.score);
      //  viewHolder.txtGradeDate.setText(dataModel.grade_date);
       // viewHolder.txtClassAVG.setText(dataModel.score);
        // Return the completed view to render on screen


        return convertView;
    }
}
