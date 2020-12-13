package com.zx.tinytiktok;

import android.util.Log;

public class DisplayItem {
    private String left_author;
    private String left_date;
    private String left_ImageUrl;
    private String left_VedioUrl;

    private String right_author;
    private String right_date;
    private String right_ImageUrl;
    private String right_VedioUrl;

    public void setLeft_author(String author){ this.left_author = author; }
    public void setLeft_date(String date){
        this.left_date = date;
    }
    public void setLeft_ImageUrl(String ImageUrl){
        this.left_ImageUrl = ImageUrl;
    }
    public void setLeft_VedioUrl(String VedioUrl){
        this.left_VedioUrl = VedioUrl;
    }

    public void setRight_author(String author){this.right_author = author;}
    public void setRight_date(String date){this.right_date = date;}
    public void setRight_ImageUrl(String imageUrl){this.right_ImageUrl = imageUrl;}
    public void setRight_VedioUrl(String vedioUrl){this.right_VedioUrl = vedioUrl;}

    public String getLeft_author() { return this.left_author; }
    public String getLeft_date() {
        return ChangeTime(this.left_date);
    }
    public String getLeft_ImageUrl() { return this.left_ImageUrl; }
    public String getLeft_VedioUrl() { return this.left_VedioUrl; }

    public String getRight_author() { return this.right_author; }
    public String getRight_date() {
        return ChangeTime(this.right_date);
    }
    public String getRight_ImageUrl() { return this.right_ImageUrl; }
    public String getRight_VedioUrl() { return this.right_VedioUrl; }

    public String ChangeTime(String UKtime){
        String hour = UKtime.substring(11,13);

        String BJtime = new String();
        BJtime = UKtime;
        String hour0 = hour.substring(0,1);
        String hour1 = hour.substring(1);

        if (hour0.substring(0) == "0"){
            int newHour = Integer.parseInt(hour1)+8;

            if (newHour<10){
                BJtime = UKtime.substring(0,11)+"0"+String.valueOf(newHour)+UKtime.substring(13);
            }
            else {
                BJtime = UKtime.substring(0, 11) + String.valueOf(newHour) + UKtime.substring(13);
            }
        }
        else {
            int newHour = Integer.parseInt(hour)+8;
            if (newHour > 24){
                newHour = newHour % 24;
                BJtime = UKtime.substring(0,11)+"0"+String.valueOf(newHour)+UKtime.substring(13);
            }
            else {
                BJtime = UKtime.substring(0,11)+String.valueOf(newHour)+UKtime.substring(13);

            }
        }

        return BJtime;
    }

}