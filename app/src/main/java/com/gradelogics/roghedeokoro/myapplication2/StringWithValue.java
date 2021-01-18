package com.gradelogics.roghedeokoro.myapplication2;

public class StringWithValue
{
    public String string;
    public float value;

    public StringWithValue(String stringPart, float tagPart) {
        string = stringPart;
        value = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }
}
