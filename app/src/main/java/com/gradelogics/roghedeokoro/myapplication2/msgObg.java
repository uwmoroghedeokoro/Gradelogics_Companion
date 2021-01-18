package com.gradelogics.roghedeokoro.myapplication2;

public class msgObg {

    public String msgDate,msgID,msgBody,msgFrom,file_path="";
    public int msgType;
    public boolean msgRead;

    public msgObg(String mDate,String mID, String mBody,String mFrom,boolean mRead,int mType){
        msgBody=mBody;
        msgDate=mDate;
        msgID=mID;
        msgFrom=mFrom;
        msgType=mType;
        msgRead=mRead;
    }

    public String get_file_path()
    {
        return file_path;
    }
}
