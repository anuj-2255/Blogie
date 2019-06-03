package com.worldtechq.blog.Helper;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mname;
    private String murl;
    //create variable to access the database unique key for image.
    private String mkey;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String iname, String imurl) {
        //to set the name and image url
        if (iname.trim().equals("")) {
            iname = "no name";
        }
        mname = iname;
        murl = imurl;
    }

    //gtter setter methods
    //to get the private variable in another activity
    public String getMname() {
        return mname;
    }

    //to set its attribute
    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMurl() {
        return murl;
    }

    public void setMurl(String murl) {
        this.murl = murl;
    }

    @Exclude
    public String getkey() {
        return mkey;
    }

    public void setkey(String key) {
        this.mkey = key;
    }
}
