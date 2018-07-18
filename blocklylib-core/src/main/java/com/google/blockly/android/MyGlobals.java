package com.google.blockly.android;

/**
 * Created by CJH on 2016-12-29.
 */

public class MyGlobals {
    private String codes = "";
    public String getData()
    {
        return codes;
    }
    public void setData(String codes)
    {
        this.codes=codes;
    }
    private static MyGlobals instance = null;

    public static synchronized MyGlobals getInstance(){
        if(null == instance){
            instance = new MyGlobals();
        }
        return instance;
    }
}
