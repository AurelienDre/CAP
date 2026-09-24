package com.example.cap;

import static androidx.core.os.BundleKt.bundleOf;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cap.databinding.FragmentMapBinding;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private MapView myOpenMap;
    private Marker positionMarker;

    private void checkLocationPermission()  {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            locationPermissionLauncher.launch(
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                }
            );
        }
    }
    private final ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        result -> {
            Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
            Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (Boolean.TRUE.equals(fineLocation) || Boolean.TRUE.equals(coarseLocation)) {
                startLocationUpdates();
            } else {
                Toast.makeText(
                        requireContext(),
                        "Permission de localisation refusée",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    );

    private void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                5,
                locationListener
        );
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            updatePosition(latitude, longitude);
        }
    };

    private void updatePosition(double latitude, double longitude) {
        GeoPoint position =
                new GeoPoint(latitude, longitude);
        if (positionMarker == null) {
            positionMarker = new Marker(myOpenMap);
            positionMarker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
            );
            positionMarker.setTitle("Ma position");
            myOpenMap.getOverlays().add(positionMarker);
        }
        positionMarker.setPosition(position);
        myOpenMap.invalidate();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = requireContext();

        // Configuration osmdroid
        Configuration.getInstance().load(
                context,
                context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        );

        // User-Agent de l'application
        Configuration.getInstance().setUserAgentValue(
                "CAP/1.0 (Android; com.example.cap)"
        );

        binding = FragmentMapBinding.inflate(
                inflater,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addPlaceButton.setOnClickListener(v ->
                NavHostFragment.findNavController(MapFragment.this)
                        .navigate(R.id.action_MapFragment_to_AddFragment)
        );

        myOpenMap = binding.map;

        XYTileSource osmFrance = new XYTileSource(
                "OSM France",
                2,
                20,
                256,
                ".png",
                new String[]{
                        "https://a.tile.openstreetmap.fr/osmfr/",
                        "https://b.tile.openstreetmap.fr/osmfr/",
                        "https://c.tile.openstreetmap.fr/osmfr/"
                },
                "© OpenStreetMap contributors / OSM France"
        );

        myOpenMap.setTileSource(osmFrance);

        // Zoom avec deux doigts
        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(myOpenMap);
        rotationGestureOverlay.setEnabled(true);
        myOpenMap.setMultiTouchControls(true);
        myOpenMap.getOverlays().add(rotationGestureOverlay);

        myOpenMap.getZoomController().setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER
        );

        myOpenMap.setMinZoomLevel(2.25);

        if (savedInstanceState == null) {
            // Position initiale uniquement au premier lancement
            GeoPoint point = new GeoPoint(48.1173, -1.6778);

            myOpenMap.getController().setCenter(point);
            myOpenMap.getController().setZoom(12.0);
        } else {
            // Restauration après une rotation
            double latitude = savedInstanceState.getDouble("map_lat");
            double longitude = savedInstanceState.getDouble("map_lon");
            double zoom = savedInstanceState.getDouble("map_zoom");
            float orientation = savedInstanceState.getFloat("map_ori");

            myOpenMap.post(() -> {
                myOpenMap.getController().setZoom(zoom);
                myOpenMap.getController().setCenter(
                        new GeoPoint(latitude, longitude)
                );
                myOpenMap.setMapOrientation(orientation);
            });
        }


        // Ajout de l'échelle
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(myOpenMap);
        myOpenMap.getOverlays().add(myScaleBarOverlay);

        // Ajout du GPS
        checkLocationPermission();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        GeoPoint center = (GeoPoint) myOpenMap.getMapCenter();

        outState.putDouble("map_lat", center.getLatitude());
        outState.putDouble("map_lon", center.getLongitude());
        outState.putDouble("map_zoom", myOpenMap.getZoomLevelDouble());
        outState.putFloat("map_ori", myOpenMap.getMapOrientation());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myOpenMap != null) {
            myOpenMap.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myOpenMap != null) {
            myOpenMap.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myOpenMap != null) {
            myOpenMap.onDetach();
            myOpenMap = null;
        }
        binding = null;
    }
}