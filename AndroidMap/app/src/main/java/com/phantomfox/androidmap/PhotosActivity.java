package com.phantomfox.androidmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.phantomfox.androidmap.Models.RealmMarker;
import com.phantomfox.androidmap.Models.RealmPhoto;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

public class PhotosActivity extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String KEY_MARKER_ID = "KEY_MARKER_ID";
    private GridView gvPhotos;
    private ImageAdapterGridView adapter;
    private RealmMarker marker;
    private RealmList<RealmPhoto> photos;
    private Realm realm;

    private static int PERMISSION_REQUEST_CODE = 123;

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED  ) {
                dispatchTakePictureIntent();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        int markerId = Integer.parseInt(intent.getStringExtra(KEY_MARKER_ID));
        marker = realm.where(RealmMarker.class).equalTo("id", markerId).findFirst();
        photos = marker.getPhotos();

        gvPhotos = findViewById(R.id.gvPhotos);
        adapter = new ImageAdapterGridView(this, photos);
        gvPhotos.setAdapter(adapter);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(this);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "GoogleMap Photo" , "Simple description");

            Number maxId = realm.where(RealmPhoto.class).max("id");
            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
            realm.beginTransaction();
            RealmPhoto realmPhoto = realm.createObject(RealmPhoto.class, nextId);
            realmPhoto.setPath(path);
            photos.add(realmPhoto);
            realm.commitTransaction();

            adapter.notifyDataSetChanged();
            gvPhotos.invalidateViews();
        }
    }

    @Override
    public void onClick(View v) {
        requestMultiplePermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
