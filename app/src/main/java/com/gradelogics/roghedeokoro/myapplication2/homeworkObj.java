package com.gradelogics.roghedeokoro.myapplication2;

import java.util.ArrayList;

public class homeworkObj {

    public String hmeDue,hmeID,hmeTitle,hmeSubject,hmeBody,hmeTeacher,hmeAttach,hmeClassName;
    public  int class_size,no_submitted;
    public boolean hmeSubmitted=false;
    public ArrayList<doc>attachments;

    public homeworkObj(String hDue,String hID, String hTitle,String hSubject,String hBody,String hTeacher,boolean submitted,String hAttach,ArrayList<doc>attachs){
       hmeDue=hDue;
       hmeID=hID;
       hmeTitle=hTitle;
       hmeSubject=hSubject;
       hmeBody=hBody;
       hmeTeacher=hTeacher;
       hmeSubmitted=submitted;
       hmeAttach=hAttach;
       attachments=attachs;
    }
    public homeworkObj()
    {

    }

    public class doc{
         public String docName,docPath;

         public doc (String dName,String dPath)
         {
             docName=dName;docPath=dPath;
         }
    }
}
