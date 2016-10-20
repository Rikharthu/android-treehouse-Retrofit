package com.treehouse.android.retrofitworkshop.model;

/** Represents response*/
public class Basic<T> {

    public int status;
    /** Whether this call was successfull*/
    public boolean success;
    /** Data */
    public T data;

}
