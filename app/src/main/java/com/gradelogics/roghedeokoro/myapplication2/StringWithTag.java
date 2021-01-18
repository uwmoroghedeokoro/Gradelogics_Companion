package com.gradelogics.roghedeokoro.myapplication2;

public class StringWithTag {
    public String string;
    public int tag;

    public StringWithTag(String stringPart, int tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }
}
