package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class siblingAdapter extends RecyclerView.Adapter<siblingAdapter.MyViewHolder>  {
    private ArrayList<student> studentList;
    Context cx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_stu_name, txt_class, txt_dept,txt_avg,txt_msg_count,txt_homework_count,txt_excelcount,txt_strugglecount;
        public CircleImageView stuPic;
        public LinearLayout lyparent;

        public MyViewHolder(View view) {
            super(view);
            txt_stu_name = (TextView) view.findViewById(R.id.txt_child_name);
            txt_class = (TextView) view.findViewById(R.id.txt_child_class);
            txt_dept = (TextView) view.findViewById(R.id.txt_child_dept);

            txt_avg = (TextView) view.findViewById(R.id.txt_avg);
            txt_msg_count = (TextView) view.findViewById(R.id.txt_msg_count);
            txt_homework_count = (TextView) view.findViewById(R.id.txt_homework_count);
            txt_excelcount = (TextView) view.findViewById(R.id.txt_excel_count);
            txt_strugglecount = (TextView) view.findViewById(R.id.txt_struggle_count);

            lyparent=(LinearLayout)view.findViewById(R.id.ly_parent);

            stuPic=(CircleImageView) view.findViewById(R.id.img_pic);

            //gain_perc = (TextView) view.findViewById(R.id.sym_change);
            //high = (TextView) view.findViewById(R.id.txt_high);
            //low = (TextView) view.findViewById(R.id.txt_low);


        }
    }

    public siblingAdapter(ArrayList<student> stock_list,Context context) {
        this.studentList = stock_list;cx=context;
    }
    @Override
    public siblingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dash_item, parent, false);

        return new siblingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(siblingAdapter.MyViewHolder holder, final int position) {
        final student stk = studentList.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        // holder.bo_shares.setText(stk.buy_qty);
        holder.txt_stu_name.setText(stk.fullname);
        holder.txt_class.setText(stk.grade_level);
        holder.txt_dept.setText(stk.department);
        Log.e("stuname",stk.fullname);

       holder.txt_avg.setText(stk.term_avg);
       holder.txt_excelcount.setText(String.valueOf(stk.excel));
       holder.txt_strugglecount.setText(String.valueOf(stk.struggle));

        holder.txt_homework_count.setText(String.valueOf(stk.homework_count));
        holder.txt_msg_count.setText(String.valueOf(stk.message_count));

        if (stk.message_count>0){
            holder.txt_msg_count.setBackgroundResource(R.drawable.round_shape);
            holder.txt_msg_count.setTextColor(Color.parseColor("#ffffff"));
        }else
        {
            holder.txt_msg_count.setBackgroundResource(0);
        }

        if (stk.homework_count>0){
            holder.txt_homework_count.setBackgroundResource(R.drawable.round_shape);
            holder.txt_homework_count.setTextColor(Color.parseColor("#ffffff"));
        }else
        {
            holder.txt_homework_count.setBackgroundResource(0);
        }

        Picasso.get()
                .load(stk.img_url)
                .fit()
                .centerInside()
                .into(holder.stuPic);

        holder.lyparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(cx,MainActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(stk);

                intent.putExtra("student_object",json);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
