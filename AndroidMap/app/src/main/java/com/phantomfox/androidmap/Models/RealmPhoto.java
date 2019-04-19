package com.phantomfox.androidmap.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RealmPhoto extends RealmObject {
    @PrimaryKey
    private int id;

    @Required
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
