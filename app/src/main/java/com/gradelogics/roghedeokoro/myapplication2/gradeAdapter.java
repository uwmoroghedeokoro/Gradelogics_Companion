package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class gradeAdapter  extends ArrayAdapter<grade> {

    Context mContext;
    private ArrayList<grade> dataSet;

    // View lookup cache
    private static class ViewHolder {
        TextView txtSubjectName;
        TextView txtTeacherName;
        TextView txtTermAvg;
        TextView txtLetter;
        TextView txtGeneralLbl;
        LinearLayout llCircle;
        TextView txtDate;
    }
    public gradeAdapter(ArrayList<grade> data, Context context) {
        super(context, R.layout.subject_item_layout, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        grade dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        gradeAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new gradeAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.view_grade_item, parent, false);
            viewHolder.txtSubjectName = (TextView) convertView.findViewById(R.id.txt_subjectName);
            viewHolder.txtTeacherName = (TextView) convertView.findViewById(R.id.txt_teacherName);
            viewHolder.txtTermAvg = (TextView) convertView.findViewById(R.id.txt_termAvg);
            viewHolder.txtLetter=(TextView)convertView.findViewById(R.id.txtLetter);
            viewHolder.llCircle=(LinearLayout)convertView.findViewById(R.id.llCircle);
            viewHolder.txtGeneralLbl=(TextView)convertView.findViewById(R.id.general_lbl);
            viewHolder.txtDate=(TextView)convertView.findViewById(R.id.txt_date);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (gradeAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtDate.setText(dataModel.grade_date);
        viewHolder.txtSubjectName.setText(dataModel.grade_title);
        viewHolder.txtTeacherName.setText(dataModel.category);
        viewHolder.txtTermAvg.setText(dataModel.score);
        viewHolder.txtGeneralLbl.setText(dataModel.impact==true?"Graded":"Ignored");
        //viewHolder.txtLetter.setText(dataModel.subjectName.substring(0,1));
        // Return the completed view to render on screen
        return convertView;
    }
}
