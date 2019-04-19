package com.phantomfox.androidmap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.Intent;

import com.phantomfox.androidmap.Models.RealmMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap map;

    private Realm realm;

    private String KEY_MARKER_ID = "KEY_MARKER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void restoreMarkers() {
        RealmResults<RealmMarker> markers = realm.where(RealmMarker.class).findAll();
        for (RealmMarker marker: markers){
            Marker m = map.addMarker(new MarkerOptions().position(new LatLng(marker.getLatitude(), marker.getLongitude())));
            m.setTag(marker.getId());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

        restoreMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(MapsActivity.this, PhotosActivity.class);
        intent.putExtra(KEY_MARKER_ID, marker.getTag().toString());
        startActivityForResult(intent, 1);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Number maxId = realm.where(RealmMarker.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        realm.beginTransaction();
        RealmMarker realmMarker = realm.createObject(RealmMarker.class, nextId);
        realmMarker.setLatitude(latLng.latitude);
        realmMarker.setLongitude(latLng.longitude);
        realm.commitTransaction();

        Marker marker = map.addMarker(new MarkerOptions().position(latLng));
        marker.setTag(realmMarker.getId());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
