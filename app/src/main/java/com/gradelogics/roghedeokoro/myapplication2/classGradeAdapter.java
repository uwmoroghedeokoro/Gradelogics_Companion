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

public class classGradeAdapter extends ArrayAdapter<grade> {
    private ArrayList<grade> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtGradeTitle;
        TextView txtGradeCat;
        TextView txtGradeDate;
        TextView txtClassAVG;

    }

    public classGradeAdapter(ArrayList<grade> data, Context context) {
        super(context, R.layout.class_grade_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final grade dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
       ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.class_grade_item, parent, false);
            viewHolder.txtGradeTitle = (TextView) convertView.findViewById(R.id.txt_gradetitle);
            viewHolder.txtGradeCat = (TextView) convertView.findViewById(R.id.txt_gradeCategory);
            viewHolder.txtGradeDate = (TextView) convertView.findViewById(R.id.txt_gradeCDate);
            viewHolder.txtClassAVG=(TextView)convertView.findViewById(R.id.txt_classAvg);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtGradeTitle.setText(dataModel.grade_title);
        viewHolder.txtGradeCat.setText(dataModel.category);
        viewHolder.txtGradeDate.setText(dataModel.grade_date);
        viewHolder.txtClassAVG.setText(dataModel.score);
        // Return the completed view to render on screen

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,activity_view_assignment.class);
                Gson gson=new Gson();
                String json=gson.toJson(dataModel);
                intent.putExtra("assg_model",json);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
}
