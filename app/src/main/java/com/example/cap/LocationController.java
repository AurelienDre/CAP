package com.example.cap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

public class LocationController implements LocationListener {

    private final Context context;
    private final MapView mapView;
    private final DirectedLocationOverlay locationOverlay;

    private LocationManager locationManager;

    private double speed = 0.0;
    private float azimuthAngleSpeed = 0.0f;

    public LocationController(
            Context context,
            MapView mapView,
            DirectedLocationOverlay locationOverlay) {

        this.context = context;
        this.mapView = mapView;
        this.locationOverlay = locationOverlay;

        locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void start() {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                this
        );
    }

    public void stop() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location localisation) {

        GeoPoint nouvelleLocalisation =
                new GeoPoint(localisation);

        if (!locationOverlay.isEnabled()) {

            locationOverlay.setEnabled(true);

            mapView.getController()
                    .animateTo(nouvelleLocalisation);
        }

        GeoPoint localisationPrecedente =
                locationOverlay.getLocation();

        locationOverlay.setLocation(nouvelleLocalisation);

        locationOverlay.setAccuracy(
                (int) localisation.getAccuracy()
        );

        if (localisationPrecedente != null
                && LocationManager.GPS_PROVIDER.equals(
                localisation.getProvider())) {

            speed = localisation.getSpeed() * 3.6;

            if (speed >= 0.1) {

                azimuthAngleSpeed =
                        localisation.getBearing();

                locationOverlay.setBearing(
                        azimuthAngleSpeed
                );
            }
        }

        mapView.getController()
                .animateTo(nouvelleLocalisation);

        mapView.setMapOrientation(
                -azimuthAngleSpeed
        );
    }
}

