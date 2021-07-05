package com.example.dogcollector;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.dogcollector.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private final static String LOG_TAG = "AAA";
    private FusedLocationProviderClient fusedLocationClient;
    private final static int REQ_CODE_LAST_KNOWN_LOCATION = 1;
    private final static int REQ_CODE_LOCATION_UPDATES = 2;
    LocationRequest locationRequest;
    boolean requestingLocationUpdates = false;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(LOG_TAG, "no location update");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(LOG_TAG, "location update");

                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(current).title("current"));
                    float zoomLevel = 13.0f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
                }
            }
        };

        //maps
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //markers
        LatLng plesmanhoek = new LatLng(52.33148796572073, 4.682458778283712);
        mMap.addMarker(new MarkerOptions().position(plesmanhoek).title("Plesmanhoek Hoofddorp Hondenuitlaat"));

        LatLng toolenburg = new LatLng(52.29793784031363, 4.654651310078626);
        mMap.addMarker(new MarkerOptions().position(toolenburg).title("Hondenpark Toolenburg"));

        LatLng haarlemmerhout = new LatLng(52.373462108807594, 4.629297246745453);
        mMap.addMarker(new MarkerOptions().position(haarlemmerhout).title("Hondenlosloopgebied Haarlemmerhout"));

        LatLng groenendaal = new LatLng(52.344523910363954, 4.613754545703693);
        mMap.addMarker(new MarkerOptions().position(groenendaal).title("Wandelbos Groenendaal"));

        LatLng lutkemeer = new LatLng(52.37902706205857, 4.757129771539566);
        mMap.addMarker(new MarkerOptions().position(lutkemeer).title("Hondenuitlaatgebied Lutkemeer"));

        LatLng wurmenveld = new LatLng(52.38618128013212, 4.561489394368898);
        mMap.addMarker(new MarkerOptions().position(wurmenveld).title("Hondenpark Wurmenveld"));

        //current marker
        showCurrentLocation();
    }

    //user location
    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE_LAST_KNOWN_LOCATION);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //got last known location
                        if (location != null) {
                            //logic to handle location object
                            Log.d(LOG_TAG, "location found");
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(current).title("current"));
                            float zoomLevel = 13.0f;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
                        } else {
                            Log.d(LOG_TAG, "no location available");
                        }
                        requestingLocationUpdates = true;
                        startLocationUpdates();
                    }
                });
    }

    //permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_LAST_KNOWN_LOCATION:
                //if request is cancelled, the result arrays are empty
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCurrentLocation();
                } else {
                    Log.e(LOG_TAG, "permission denied");
                }
                return;
            case REQ_CODE_LOCATION_UPDATES:
                //if request is cancelled, the result arrays are empty
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    requestingLocationUpdates = false;
                    Log.e(LOG_TAG, "permission denied");
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //location updates
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE_LOCATION_UPDATES);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}