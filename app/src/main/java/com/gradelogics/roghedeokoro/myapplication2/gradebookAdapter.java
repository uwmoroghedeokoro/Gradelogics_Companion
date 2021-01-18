package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class gradebookAdapter extends ArrayAdapter<gradeBook> {
    Context mContext;
    private ArrayList<gradeBook> dataSet;

    // View lookup cache
    private static class ViewHolder {
        TextView txtGradebook;

    }
    public gradebookAdapter(ArrayList<gradeBook> data, Context context) {
        super(context, R.layout.spinner_item_medium_whitish, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public gradeBook getItem(int position) {
        return super.getItem(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        gradeBook dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        gradebookAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.spinner_item_medium_whitish, parent, false);
            viewHolder.txtGradebook = (TextView) convertView.findViewById(R.id.text1);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtGradebook.setText(dataModel.gradebookName.toString());

      //  convertView.setOn

        return convertView;
    }
}
