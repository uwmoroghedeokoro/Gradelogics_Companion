package com.gradelogics.roghedeokoro.myapplication2;

public class gradeBook {
    public int ID;
    public String gradebookName;
    public String gradebookYear;
    public int gradebookTerm;

    public gradeBook()
    {

    }
    @Override
    public String toString() {
        return gradebookName;
    }
}
