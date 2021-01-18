package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.NumberFormat;
import java.util.ArrayList;

public class transacAdapter extends ArrayAdapter<transaction> {

    Context mContext;
    private ArrayList<transaction> dataSet;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTranDesc;
        TextView txtTranType;
        TextView txtTranAmt;
        TextView txtTranDate;

    }

    public transacAdapter(@NonNull Context context, ArrayList<transaction> data) {
        super(context, R.layout.accounts_item_layout, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        transaction dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;
        NumberFormat format=NumberFormat.getCurrencyInstance();

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());


            convertView = inflater.inflate(R.layout.accounts_item_layout, parent, false);
            viewHolder.txtTranAmt = (TextView) convertView.findViewById(R.id.txt_tran_amt);
            viewHolder.txtTranDesc = (TextView) convertView.findViewById(R.id.txt_tran_Desc);
            viewHolder.txtTranType = (TextView) convertView.findViewById(R.id.txt_tran_type);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.txtTranDesc.setText(dataModel.desc);
        viewHolder.txtTranType.setText(dataModel.type + " - " + dataModel.date);

        if (dataModel.type.equals("Payment"))
        {
            viewHolder.txtTranAmt.setTextColor(Color.parseColor("#c2233d"));
            viewHolder.txtTranAmt.setText("-"+format.format(dataModel.amt));
        }else
            viewHolder.txtTranAmt.setText(format.format(dataModel.amt));

        return convertView;
    }
}
