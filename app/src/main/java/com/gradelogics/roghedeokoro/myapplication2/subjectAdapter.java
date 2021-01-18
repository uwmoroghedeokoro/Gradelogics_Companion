package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Roghe DeOkoro on 9/29/2018.
 */

public class subjectAdapter extends ArrayAdapter<subject> {
    private ArrayList<subject> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtSubjectName;
        TextView txtTeacherName;
        TextView txtTermAvg;
        TextView txtLetter;
        LinearLayout llCircle;
    }

    public subjectAdapter(ArrayList<subject> data, Context context) {
        super(context, R.layout.subject_item_layout, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        subject dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.subject_item_layout, parent, false);
            viewHolder.txtSubjectName = (TextView) convertView.findViewById(R.id.txt_subjectName);
            viewHolder.txtTeacherName = (TextView) convertView.findViewById(R.id.txt_teacherName);
            viewHolder.txtTermAvg = (TextView) convertView.findViewById(R.id.txt_termAvg);
            viewHolder.txtLetter=(TextView)convertView.findViewById(R.id.txtLetter);
            viewHolder.llCircle=(LinearLayout)convertView.findViewById(R.id.llCircle);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtSubjectName.setText(dataModel.subjectName);
        viewHolder.txtTeacherName.setText(dataModel.teacherName);
        viewHolder.txtTermAvg.setText(dataModel.termAvg);
        viewHolder.txtLetter.setText(dataModel.subjectName.substring(0,1));
        // Return the completed view to render on screen
        return convertView;
    }
}

