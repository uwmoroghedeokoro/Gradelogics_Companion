package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class enter_grades_adapter extends RecyclerView.Adapter<enter_grades_adapter.MyViewHolder> {
    private ArrayList<student> dataSet;
    Context mContext;
    teacher cTeacher;
    String maxScore;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudentName;
        EditText edtStudentScore;
        Spinner spnGrade;
        TextView txtStudentFinalScore;
        CircleImageView cvImg;

        public MyViewHolder(View view) {
            super(view);
            txtStudentName = (TextView) view.findViewById(R.id.txt_student_name);
            edtStudentScore = (EditText) view.findViewById(R.id.stu_score);
           // spnGrade = (Spinner) view.findViewById(R.id.stu_grade_spin);
            txtStudentFinalScore=(TextView)view.findViewById(R.id.stu_final_score);
            cvImg=(CircleImageView)view.findViewById(R.id.img_pic);
           // dataSet.get(getAdapterPosition()).maxS=maxScore;

            edtStudentScore.setHint(String.valueOf(maxScore));
            edtStudentScore.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!edtStudentScore.getText().toString().equals("") && Float.valueOf(edtStudentScore.getText().toString())<= Float.valueOf(maxScore) ) {
                        int maxS = Integer.valueOf(edtStudentScore.getHint().toString());
                        DecimalFormat df = new DecimalFormat("0.0");
                        float percS = (Float.valueOf(edtStudentScore.getText().toString()) / maxS) * 100;
                        txtStudentFinalScore.setText(String.valueOf(df.format(percS)));
                        dataSet.get(getAdapterPosition()).setScore(String.valueOf(df.format(percS)));
                        txtStudentFinalScore.setBackgroundColor(Color.parseColor("#c9ffea"));
                    }else
                    {
                        txtStudentFinalScore.setText("-");
                        dataSet.get(getAdapterPosition()).setScore( "-");
                    //    edtStudentScore.setText("");
                        txtStudentFinalScore.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    public enter_grades_adapter(ArrayList<student> studentlist,Context cx,int max_s) {
        this.dataSet = studentlist;
        mContext=cx;
        maxScore=String.valueOf(max_s);

        SharedPreferences prefs=cx.getSharedPreferences("gradelogics",MODE_PRIVATE);
        Gson gson=new Gson();
        String json=prefs.getString("login_object","");
        cTeacher=gson.fromJson(json,teacher.class);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_enter_grade_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
       student std = dataSet.get(position);
        final int current_index=position;
        List<StringWithValue> gradeScalelist = new ArrayList<StringWithValue>();

        StringWithValue blank=new StringWithValue("-",-1);
        gradeScalelist.add(blank);

        viewHolder.txtStudentName.setText(std.fullname);
        viewHolder.txtStudentFinalScore.setText(std.score);
        viewHolder.edtStudentScore.setHint(String.valueOf(maxScore));
       /*
        for (GradeScale gs : cTeacher.gradeScale)
        {
            StringWithValue grades=new StringWithValue(gs.gradeChar,gs.gradeMin);
            gradeScalelist.add(grades);
        }

        ArrayAdapter<StringWithValue> adap = new ArrayAdapter<StringWithValue> (mContext, R.layout.spinner_item_large_black, gradeScalelist);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        viewHolder.spnGrade.setAdapter(adap);


        viewHolder.spnGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if (position>0)
                {
                    StringWithValue selectedGrade = (StringWithValue) parent.getItemAtPosition(position);
                    if (selectedGrade.value>-1) {
                       // viewHolder.txtStudentFinalScore.setText(String.valueOf(selectedGrade.value) );
                       // dataSet.get(current_index).setScore( String.valueOf(selectedGrade.value));
                    }else
                    {
                      //  viewHolder.txtStudentFinalScore.setText("-");
                      //  dataSet.get(current_index).setScore( "-");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/





        Picasso.get()
                .load(std.img_url)
                .fit()
                .centerInside()
                .into(viewHolder.cvImg);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public student get_student(int pos)
    {
        return dataSet.get(pos);
    }
}
