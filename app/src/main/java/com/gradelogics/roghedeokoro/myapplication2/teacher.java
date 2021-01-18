package com.gradelogics.roghedeokoro.myapplication2;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class teacher
{
    public int id;
    public String fullname;
    public String department;
    public String school_name;
    public String school_logo;
    public ArrayList<classroom> classrooms;
    public ArrayList<subject>subjects;
    public ArrayList<gradeBook>gradeBooks;
    public ArrayList<GradeScale>gradeScale;
    public String reg_year;
    public String sch_term;
    public String domain;
    public String img_url;
    public String _OverallAvg;
    public int homework_submit_count;
    public int message_count;
    public String subject_name;
public String object_type="teacher";
  public String api_key;

    public teacher()
    {
        classrooms=new ArrayList<>();
        subjects=new ArrayList<>();
        gradeBooks=new ArrayList<>();
        gradeScale=new ArrayList<>();
    }





}
