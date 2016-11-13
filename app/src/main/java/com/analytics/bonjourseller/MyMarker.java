package com.analytics.bonjourseller;

public class MyMarker {
    private String mtitle;
    private String mdescription;


    private String noOfPeople;
    private String mIcon;
    private Double mLatitude;
    private Double mLongitude;

    public MyMarker(String title, String description, String noOfPeople, String icon,
                    Double latitude, Double longitude) {
        this.mtitle = title;
        this.mdescription = description;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIcon = icon;
        this.noOfPeople = noOfPeople;
    }

    public String gettitle() {
        return mtitle;
    }

    public void settitle(String mLabel) {
        this.mtitle = mLabel;
    }

    public String getdescription() {
        return mdescription;
    }

    public void setdescription(String description) {
        this.mdescription = description;
    }

    public String getNoOfPeople() {
        return noOfPeople;
    }

    public void setNoOfPeople(String noOfPeople) {
        this.noOfPeople = noOfPeople;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String icon) {
        this.mIcon = icon;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
