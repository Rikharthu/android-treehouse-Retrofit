package com.treehouse.android.retrofitworkshop.model;

/** Represents response*/
public class Basic<T> {

    // FIXME SEE assets/sample_response.txt for sample API response!!!

    public int status;
    /** Whether this call was successfull*/
    public boolean success;
    /** Data */
    public T data;

}
