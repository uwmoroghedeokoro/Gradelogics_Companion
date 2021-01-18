package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class messageAdapter extends ArrayAdapter<msgObg> {
    private ArrayList<msgObg> dataSet;
    Context mContext;
    String object_type;

    // View lookup cache
    private static class ViewHolder {
        TextView txtBody;
        TextView txtDate;
        TextView txtFrom;
        TextView txtLetter;
        LinearLayout lc;

    }

    public messageAdapter(ArrayList<msgObg> data, Context context) {
        super(context, R.layout.message_item, data);
        this.dataSet = data;
        this.mContext=context;
        SharedPreferences prefs=context.getSharedPreferences("gradelogics",MODE_PRIVATE);
        object_type=prefs.getString("object_type","");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final msgObg dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.message_item, parent, false);
            viewHolder.txtBody = (TextView) convertView.findViewById(R.id.txt_msg);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            viewHolder.txtFrom = (TextView) convertView.findViewById(R.id.txt_from);
            viewHolder.txtLetter=(TextView)convertView.findViewById(R.id.txtLetter);
            viewHolder.lc=(LinearLayout)convertView.findViewById(R.id.llCircle);

            result=convertView;

            convertView.setTag(viewHolder);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;

                    if(dataModel.msgID.equals("-1"))
                    {
                        intent=new Intent(getContext(),view_discussions.class);
                    }else if(dataModel.msgID.equals("-2"))
                        intent=new Intent(getContext(),teacher_view_discussions.class);
                    else if (object_type.equals("student"))
                    intent=new Intent(getContext(),view_msg_Activity.class);
                    else
                        intent=new Intent(getContext(),view_teacher_message.class);

                    intent.putExtra("msg_date",dataModel.msgDate);
                    intent.putExtra("msgtype","reply");
                    intent.putExtra("msg_body",dataModel.msgBody);
                    intent.putExtra("msg_from",dataModel.msgFrom);
                    intent.putExtra("msg_id",dataModel.msgID);
                    intent.putExtra("msg_type",dataModel.msgType);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    getContext().startActivity(intent);
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        if(dataModel.msgID.equals("-1") || dataModel.msgID.equals("-2"))
        {
            viewHolder.lc.setBackground(mContext.getDrawable(R.drawable.group));
        }else
            viewHolder.lc.setBackground(mContext.getDrawable(R.drawable.msgprofile));

        if(dataModel.msgBody.length()>33)
            viewHolder.txtBody.setText(dataModel.msgBody.substring(0,32)+ "....");
        else
            viewHolder.txtBody.setText(dataModel.msgBody);

        if(dataModel.msgRead) {
            viewHolder.txtFrom.setTypeface(Typeface.DEFAULT);
            viewHolder.txtDate.setTypeface(Typeface.DEFAULT);
        }
        else {
            viewHolder.txtFrom.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.txtDate.setTypeface(Typeface.DEFAULT_BOLD);
        }

        viewHolder.txtDate.setText(dataModel.msgDate);
        viewHolder.txtFrom.setText(dataModel.msgFrom);
        viewHolder.txtLetter.setText(dataModel.msgFrom.substring(0,1));
        // Return the completed view to render on screen
        return convertView;
    }
}
