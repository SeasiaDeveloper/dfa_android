package com.dfa.maps;

/*
 * Created by admin on 09-01-2018.
 */

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

public interface MapInterface {

    void onMapReady(GoogleMap googleMap);

    void onAutoCompleteListener(Place place);

    void onCameraIdle(CameraPosition cameraPosition);

    void onCameraMoveStarted();
}
