package com.example.mylocation;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    double[] latArr =new double[]{12.947033,12.947075,12.947127,12.947179,12.947263,12.947336,12.947482,
            12.947608,12.947702,12.947796,12.947921,12.948036,12.948151,12.948298,12.948455,
            12.948632,12.948499,12.948894,12.949457,12.949665};
    double[] longArr = new double[]{77.599366,77.599054,77.598893,77.598733,77.598582,77.598432,77.598271,77.598110,
            77.597928,77.597767,77.597552,77.597381,77.597188,77.597037,77.596930,77.596769,77.596957,
            77.596614,77.596271,77.596108};
    int po=0;
    ImageView moveToMap,myLocation;
    private GoogleMap mMap;
    LocationManager locationManager;
    double latitude;
    double longitude;
    LatLng lt;
    LatLng latLngMoved;
    float cameraP=15.2f;
    boolean ready=false;
    boolean moved=false;
    Marker marker;
    MarkerOptions markerOptions;
    HashMap<String, Marker> hashMapMarker = new HashMap<String, Marker>();

    LatLng latLng;

    Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        moveToMap=(ImageView)findViewById(R.id.move_to_map);
        myLocation=findViewById(R.id.my_location);



        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
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
                    latLng=new LatLng(latitude,longitude);
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        List<Address> list=geocoder.getFromLocation(latitude,longitude,1);
                        String city=list.get(0).getSubLocality();
                        city +=", "+list.get(0).getLocality();
                        city +=", "+list.get(0).getCountryName();
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location);

//                        if (hashMapMarker.size()>0) {
//                            marker = hashMapMarker.get("my");
//                            marker.remove();
//                            hashMapMarker.get("my").remove();
//                        }
                        Marker marker = hashMapMarker.get("my");
                        if (marker!=null){
                            marker.remove();
                            hashMapMarker.remove("my");
                        }
                        markerOptions=new MarkerOptions().position(latLng).title(city).icon(icon).anchor(0.5f,1);
                        marker=mMap.addMarker(markerOptions);
                        hashMapMarker.put("my",marker);
                        if (moved){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMoved, cameraP));
                        }else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraP));
                        }                        ready=true;
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
                    latLng=new LatLng(latitude,longitude);
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        List<Address> list=geocoder.getFromLocation(latitude,longitude,1);
                        String city=list.get(0).getSubLocality();
                        city +=", "+list.get(0).getLocality();
                        city +=", "+list.get(0).getCountryName();
                 //       mMap.addMarker(new MarkerOptions().position(latLng).title(city));
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location);

//                        if (hashMapMarker.size()>0) {
//                            marker = hashMapMarker.get("my");
//                            marker.remove();
//                            hashMapMarker.get("my").remove();
//                        }
                        Marker marker = hashMapMarker.get("my");

                        if (marker!=null){
                            marker.remove();
                            hashMapMarker.remove("my");
                        }
                        markerOptions=new MarkerOptions().position(latLng).title(city).icon(icon).anchor(0.5f,1);
                        marker=mMap.addMarker(markerOptions);
                        hashMapMarker.put("my",marker);

                        if (moved){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMoved, cameraP));
                        }else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraP));
                        }
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



    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraP));
            }
        });
        moveToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   String url = "https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude + "&travelmode=driving";
                String url = "https://www.google.com/maps/dir/?api=1&destination=" + lt.latitude + "," + lt.longitude;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
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


                    Marker marker1 = hashMapMarker.get("clicked");
                if (marker1!=null) {
                    marker1.remove();
                    hashMapMarker.remove("clicked");
                }
                Marker marker=mMap.addMarker(new MarkerOptions().position(lt).title(add));
                hashMapMarker.put("clicked",marker);
              //  new GetDirectionsAsync().execute();

                Location startPoint=new Location("locationA");
                startPoint.setLatitude(latitude);
                startPoint.setLongitude(longitude);

                Location endPoint=new Location("locationA");
                endPoint.setLatitude(position.latitude);
                endPoint.setLongitude(position.longitude);

                double distance=startPoint.distanceTo(endPoint)/1000;

                String dis=distance+"";
                dis=dis.substring(0,4);

                final AlertDialog.Builder builder= new AlertDialog.Builder(MapsActivity.this);
                        builder.setTitle("Distance between you and selected point ("+add+") is :- \n"+dis+" Km").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

//                Polyline line = mMap.addPolyline(new PolylineOptions()
//                        .add(new LatLng(latitude,longitude), new LatLng(position.latitude,position.longitude))
//                        .width(5)
//                        .color(Color.RED));
            //    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lt, cameraP));

            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (ready) {
                    cameraP = mMap.getCameraPosition().zoom;
                    latLngMoved=new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude);
                    moved=true;
                }
                Log.i("centerLat", String.valueOf(cameraPosition.target.latitude));

                Log.i("centerLong", String.valueOf(cameraPosition.target.longitude));

            }
        });
    }
}
