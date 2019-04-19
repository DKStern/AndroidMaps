package com.phantomfox.androidmap.Models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RealmMarker extends RealmObject {
    @PrimaryKey
    private int id;

    private double latitude;

    private double longitude;

    private RealmList<RealmPhoto> photos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public RealmList<RealmPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(RealmList<RealmPhoto> photos) {
        this.photos = photos;
    }
}
