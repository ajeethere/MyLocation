package com.example.mylocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ImageView moveToMap;
    private GoogleMap mMap;
    LocationManager locationManager;
    double latitude;
    double longitude;
    LatLng lt;
    float cameraP=15.2f;
    boolean ready=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        moveToMap=(ImageView)findViewById(R.id.move_to_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    LatLng latLng=new LatLng(latitude,longitude);
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        List<Address> list=geocoder.getFromLocation(latitude,longitude,1);
                        String city=list.get(0).getLocality();
                        city +=", "+list.get(0).getCountryName();
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location);

                        mMap.addMarker(new MarkerOptions().position(latLng).title(city).icon(icon).anchor(0.5f,1));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraP));
                        ready=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    LatLng latLng=new LatLng(latitude,longitude);
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        List<Address> list=geocoder.getFromLocation(latitude,longitude,1);
                        String city=list.get(0).getLocality();
                        city +=", "+list.get(0).getCountryName();
                 //       mMap.addMarker(new MarkerOptions().position(latLng).title(city));
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location);

                        mMap.addMarker(new MarkerOptions().position(latLng).title(city).icon(icon).anchor(0.5f,1));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraP));
                        ready=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        moveToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   String url = "https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude + "&travelmode=driving";
                String url = "https://www.google.com/maps/dir/?api=1&destination=" + lt.latitude + "," + lt.longitude;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {

                lt=new LatLng(position.latitude,position.longitude);

                Geocoder geocoder=new Geocoder(getApplicationContext());
                String add = "";
                try {
                    List<Address> list=geocoder.getFromLocation(position.latitude,position.longitude,1);
                    add=list.get(0).getSubLocality()+", "+list.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MapsActivity.this, add,Toast.LENGTH_SHORT).show();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(lt).title(add));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lt, cameraP));
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (ready) {
                    cameraP = mMap.getCameraPosition().zoom;
                }
                Log.i("centerLat", String.valueOf(cameraPosition.target.latitude));

                Log.i("centerLong", String.valueOf(cameraPosition.target.longitude));

            }
        });
    }
}
