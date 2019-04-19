package com.phantomfox.androidmap;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.phantomfox.androidmap.Models.RealmPhoto;

import io.realm.RealmList;

public class ImageAdapterGridView extends BaseAdapter {
    private Context context;
    private RealmList<RealmPhoto> images;

    public ImageAdapterGridView(Context c, RealmList<RealmPhoto> images) {
        context = c;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(Uri.parse(images.get(position).getPath()));

        return imageView;
    }
}

