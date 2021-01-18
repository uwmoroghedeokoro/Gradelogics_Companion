package com.gradelogics.roghedeokoro.myapplication2;

import android.graphics.Bitmap;

public class student {
  public int id;
  public String fullname;
  public String department;
  public String term_avg;
  public float balance;
  public String grade_level;
  public String school_name;
  public String school_logo;
  public contact Contact;
  public Bitmap profile_pic;
  public int excel;
  public int struggle;
  public String reg_year;
  public String sch_term;
public String domain;
public String messages;
public String homework;
public String img_url;
public String _OverallAvg;
public int homework_count;
public int message_count;
public String object_type="student";
public String subject;
public classroom _classroom;
public String score="-";
public int maxS;
public String attend_status;


  public student()
  {
      Contact=new contact();
      _classroom=new classroom();
  }

  public void setScore(String s)
  {
      this.score=s;
  }

  public  String getScore()
  {
      return score;
  }

  public class contact{
      public String mother;
      public String father;
      public String email;
      public String workp;
      public String cellp;
      public String homep;
      public String address;
      public String city;


      public contact()
      {

      }

  }


}
