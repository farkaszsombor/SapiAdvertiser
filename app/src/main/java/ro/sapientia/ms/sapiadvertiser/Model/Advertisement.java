package ro.sapientia.ms.sapiadvertiser.Model;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.HashMap;

public class Advertisement implements Parcelable {

    private String creatorID;
    private String adID;
    private String title;
    private String longDescription;
    private String shortDescription;
    private int numOfViews;
    private String advertURL;
    private HashMap<String,String> images = new HashMap<>();
    private String location;
    private Long timeStamp;
    private String phoneNumber;
    private boolean isReported;

    public Advertisement(){

    }

    private Advertisement(Parcel in) {
        title = in.readString();
        longDescription = in.readString();
        numOfViews = in.readInt();
        advertURL = in.readString();
        isReported = in.readByte() != 0;
        timeStamp = in.readLong();
    }

    public static final Creator<Advertisement> CREATOR = new Creator<Advertisement>() {
        @Override
        public Advertisement createFromParcel(Parcel in) {
            return new Advertisement(in);
        }

        @Override
        public Advertisement[] newArray(int size) {
            return new Advertisement[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public int getNumOfViews() {
        return numOfViews;
    }

    public void setNumOfViews(int numOfViews) {
        this.numOfViews = numOfViews;
    }

    public String getAdvertURL() {
        return advertURL;
    }

    public void setAdvertURL(String advertURL) {
        this.advertURL = advertURL;
    }

    public HashMap<String,String> getImages() {
        return images;
    }

    public void setImages(HashMap<String,String> images) {
        this.images = images;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isIsReported() {
        return isReported;
    }

    public void setIsReported(boolean reported) {
        isReported = reported;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.longDescription);
        dest.writeInt(this.numOfViews);
        dest.writeString(this.advertURL);
        dest.writeMap(this.images);
        dest.writeString(this.shortDescription);
        dest.writeString(this.location);
        dest.writeByte((byte) (this.isReported ? 1 : 0));
        dest.writeLong(this.timeStamp);
    }

    public String getFirstImage(){

        if(!images.isEmpty()){
            return images.entrySet().iterator().next().getValue();
        }
        return "https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-5bc78.appspot.com/o/Screenshot_1.png?alt=media&token=a9d468a1-f4a0-4b22-bada-dd41c6e6e1fe";
    }
}
