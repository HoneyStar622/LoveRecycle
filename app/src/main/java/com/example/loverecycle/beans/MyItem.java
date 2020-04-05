package com.example.loverecycle.beans;

public class MyItem {

    private String myInfo;
    private String myClub;
    private String moreInfo;
    private String setting;
    private String about;

    public MyItem(String myInfo, String myClub, String moreInfo, String setting, String about) {
        this.myInfo = myInfo;
        this.myClub = myClub;
        this.moreInfo = moreInfo;
        this.setting = setting;
        this.about = about;
    }


    public String getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(String myInfo) {
        this.myInfo = myInfo;
    }

    public String getMyClub() {
        return myClub;
    }

    public void setMyClub(String myClub) {
        this.myClub = myClub;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
