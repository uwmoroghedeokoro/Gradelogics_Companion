package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class studentAssignAdapter extends ArrayAdapter<student> {
    private ArrayList<student> dataSet;
    Context mContext;
    teacher cTeacher;

    // View lookup cache
    public static class ViewHolder {
        TextView txtStudentName;
        EditText edtStudentScore;
        Spinner spnGrade;
        TextView txtStudentFinalScore;
        CircleImageView cvImg;
    }

    public studentAssignAdapter(ArrayList<student> data, Context context) {
        super(context, R.layout.student_enter_grade_item, data);
        this.dataSet = data;
        this.mContext=context;

        SharedPreferences prefs=context.getSharedPreferences("gradelogics",0);
        String json=prefs.getString("login_object","");
        Gson gson=new Gson();
        cTeacher=gson.fromJson(json,teacher.class);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        student dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final int current_index=position;
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.student_enter_grade_item, parent, false);
            viewHolder.txtStudentName = (TextView) convertView.findViewById(R.id.txt_student_name);
            viewHolder.edtStudentScore = (EditText) convertView.findViewById(R.id.stu_score);
            viewHolder.txtStudentFinalScore = (TextView) convertView.findViewById(R.id.stu_final_score);
           // viewHolder.spnGrade=(Spinner) convertView.findViewById(R.id.stu_grade_spin);
            viewHolder.cvImg=(CircleImageView) convertView.findViewById(R.id.img_pic);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtStudentName.setText(dataModel.fullname);

        List<StringWithValue> gradeScalelist = new ArrayList<StringWithValue>();

        StringWithValue blank=new StringWithValue("-",-1);
        gradeScalelist.add(blank);

        for (GradeScale gs : cTeacher.gradeScale)
        {
            StringWithValue grades=new StringWithValue(gs.gradeChar,gs.gradeMin);
            gradeScalelist.add(grades);
        }

        ArrayAdapter<StringWithValue> adap = new ArrayAdapter<StringWithValue> (mContext, R.layout.spinner_item_large_black, gradeScalelist);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        viewHolder.spnGrade.setAdapter(adap);
      //  viewHolder.spnGrade.setPrompt("Select");

        viewHolder.spnGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //if (position>0)
              {
                  StringWithValue selectedGrade = (StringWithValue) parent.getItemAtPosition(position);
                  if (selectedGrade.value>-1) {
                      viewHolder.txtStudentFinalScore.setText(String.valueOf(selectedGrade.value) );
                      dataSet.get(current_index).score = String.valueOf(selectedGrade.value);
                  }else
                  {
                      viewHolder.txtStudentFinalScore.setText("-");
                      dataSet.get(current_index).score = "-";
                  }
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        viewHolder.edtStudentScore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!viewHolder.edtStudentScore.getText().toString().equals("")) {
                    int maxS = Integer.valueOf(viewHolder.edtStudentScore.getHint().toString());
                    float percS = (Float.valueOf(viewHolder.edtStudentScore.getText().toString()) / maxS) * 100;
                    viewHolder.txtStudentFinalScore.setText(String.valueOf(percS));
                    dataSet.get(current_index).score=String.valueOf(percS);
                }else
                {
                    viewHolder.txtStudentFinalScore.setText("-");
                    dataSet.get(current_index).score = "-";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        Picasso.get()
                .load(dataModel.img_url)
                .fit()
                .centerInside()
                .into(viewHolder.cvImg);
        // Return the completed view to render on screen



        return convertView;
    }
}
