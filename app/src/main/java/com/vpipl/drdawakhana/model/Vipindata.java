package com.vpipl.drdawakhana.model;

public class Vipindata {

    private String HID;
    private String Heading;
    private String ImgPath;


    public Vipindata(String HID, String Heading, String ImgPath) {

        this.HID = HID;
        this.Heading = Heading;
        this.ImgPath = ImgPath;

    }

    public String getHID() {
        return HID;
    }

    public String getHeading() {
        return Heading;
    }

    public String getImgPath() {
        return ImgPath;
    }




}