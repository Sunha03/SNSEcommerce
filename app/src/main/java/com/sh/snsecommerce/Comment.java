package com.sh.snsecommerce;

import android.graphics.Bitmap;

public class Comment {
    private String imgUrl;
    //private Bitmap bitmap;
    private String name;
    private String comment;
    private String date;

    public Comment(/*String imgUrl, */String name, String comment, String date) {
        //this.imgUrl = imgUrl;
        this.name = name;
        this.comment = comment;
        this.date = date;
    }
    /*public Comment(Bitmap bitmap, String name, String comment, String date) {
        this.bitmap = bitmap;
        this.name = name;
        this.comment = comment;
        this.date = date;
    }*/

    //public String getImageUrl() { return imgUrl; }
    //public Bitmap getBitmap() {return bitmap;}
    public String getName() { return name; }
    public String getComment() {
        return comment;
    }
    public String getDate() {
        return date;
    }
}
