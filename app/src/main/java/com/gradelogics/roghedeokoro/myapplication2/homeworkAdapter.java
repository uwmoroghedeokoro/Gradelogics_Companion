package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class homeworkAdapter extends ArrayAdapter<homeworkObj> {
    private ArrayList<homeworkObj> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtBody;
        TextView txtDue;
        TextView txtSubject;
        TextView txtTeacher;
        TextView txtTitle;
        LinearLayout lyBg;

    }

    public homeworkAdapter(ArrayList<homeworkObj> data, Context context) {
        super(context, R.layout.homework_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final homeworkObj dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.homework_item, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
            viewHolder.txtSubject = (TextView) convertView.findViewById(R.id.txt_subject);
            viewHolder.txtDue = (TextView) convertView.findViewById(R.id.txt_duedate);
            viewHolder.txtBody=(TextView)convertView.findViewById(R.id.txt_homeBody);
            viewHolder.txtTeacher=(TextView)convertView.findViewById(R.id.hme_teacherName);
            viewHolder.lyBg=(LinearLayout)convertView.findViewById(R.id.hmeBg);

            result=convertView;

            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.txtSubject.setText(dataModel.hmeSubject);
        viewHolder.txtTitle.setText(dataModel.hmeTitle);
        viewHolder.txtTeacher.setText(dataModel.hmeTeacher);

        if(dataModel.hmeSubmitted)
        {
            viewHolder.txtDue.setText("SUBMITTED");
            viewHolder.lyBg.setBackgroundColor(Color.parseColor("#dddddd"));


        }else {
            viewHolder.txtDue.setText(dataModel.hmeDue);
            viewHolder.lyBg.setBackgroundColor(Color.parseColor("#75EB8B"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(),view_homeworkActivity.class);
                    intent.putExtra("home_date",dataModel.hmeDue);
                    intent.putExtra("home_id",dataModel.hmeID);
                    intent.putExtra("home_title",dataModel.hmeTitle);
                    intent.putExtra("home_subject",dataModel.hmeSubject);
                    intent.putExtra("home_body",dataModel.hmeBody);
                    intent.putExtra("home_teacher",dataModel.hmeTeacher);
                    intent.putExtra("home_attach",dataModel.hmeAttach);
                    Gson gson=new Gson();
                    String json=gson.toJson(dataModel);
                    intent.putExtra("home_object",json);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
        }

        if (dataModel.hmeBody.length()>55)
          viewHolder.txtBody.setText("\"" + Html.fromHtml(dataModel.hmeBody.substring(0,54))+ " ...\"");
        else
            viewHolder.txtBody.setText("\""+ Html.fromHtml(dataModel.hmeBody) + "\"");
        // Return the completed view to render on screen
        return convertView;
    }
}
