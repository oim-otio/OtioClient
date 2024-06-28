package com.otio.client;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity {
    String id;
    String name;
    String subcategory;
    String imageName;
    double rating;
    String imagePath;
    String mapsLink;
    List<String> timeSlots;

    public Activity() {}

    public Activity(String id, String name, String subcategory, String imageName, double rating, String imagePath,
                       String mapsLink) {
        this.id = id;
        this.name = name;
        this.subcategory = subcategory;
        this.imageName = imageName;
        this.rating = rating;
        this.imagePath = imagePath;
        this.mapsLink = mapsLink;
        this.timeSlots = new ArrayList<String>();
    }

    public Activity(String id, String name, String subcategory, String imageName, double rating, String imagePath,
                       String mapsLink, List<String> timeSlots) {
        this.id = id;
        this.name = name;
        this.subcategory = subcategory;
        this.imageName = imageName;
        this.rating = rating;
        this.imagePath = imagePath;
        this.mapsLink = mapsLink;
        this.timeSlots = timeSlots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory){
        this.subcategory = subcategory;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName){
        this.imageName = imageName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public String getMapsLink() {
        return mapsLink;
    }

    public void setMapsLink(String mapsLink) {
        this.mapsLink = mapsLink;
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<String> timeSlots) {
        this.timeSlots = timeSlots;
    }

    @Override
    public String toString() {
        StringBuilder objectBuilder = new StringBuilder();
        objectBuilder.append("ArtsCulture [id=").append(id).append(", name=").append(name).append(", subcategory=").append(subcategory)
                .append(", imageName=").append(imageName).append(", rating=").append(rating).append(", imagePath=").append(imagePath)
                .append(", mapsLink=").append(mapsLink).append(", timeSlots=").append(timeSlots).append("]");
        return objectBuilder.toString();
    }
}
