package com.abhay.myapplication;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
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
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(33.7490,
                        -84.3880));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        UiSettings mMapSettings = mMap.getUiSettings();
        mMapSettings.setZoomControlsEnabled(true);
        mMapSettings.setZoomGesturesEnabled(true);
        mMapSettings.setCompassEnabled(true);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }
        //Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        createPolygon();
    }



    public void createHeatMap() {
        InputStream s = getResources().openRawResource(R.raw.finalheatmap2);
        List<WeightedLatLng> list = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                count = count + 1;
                String[] arrTemp = line.split(",");
                list.add(new WeightedLatLng(new LatLng(Double.parseDouble(arrTemp[0]),Double.parseDouble(arrTemp[1])),Double.parseDouble(arrTemp[2])));
                line = br.readLine();
            }
            br.close();
            final HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().weightedData(list).radius(50).opacity(0.4).build();
            final TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            double meter = 156543.03392 * Math.cos(33.7490 * Math.PI / 180) / Math.pow(2, 10);
            meter = 1/meter;
            meter = Math.ceil(meter * 80.4672 * 35);
            System.out.println(meter);
            mProvider.setRadius((int) meter);
            mOverlay.clearTileCache();
            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                private float currentZoom = -1;
                @Override
                public void onCameraIdle() {
                    if (mMap.getCameraPosition().zoom != currentZoom){
                        currentZoom = mMap.getCameraPosition().zoom;
                        double meter = 156543.03392 * Math.cos(mMap.getCameraPosition().target.latitude * Math.PI / 180) / Math.pow(2, currentZoom);
                        meter = 1/meter;
                        meter = Math.ceil(meter * 80.4672 * 35);

                        System.out.println(meter);
                        mProvider.setRadius((int) meter);
                        mOverlay.clearTileCache();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void createPolygon() {
        InputStream s = getResources().openRawResource(R.raw.final25no);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s));
            String line = br.readLine();
            int count = 0;
            final double[] arr = new double[5];
            while (line != null) {
                count = count + 1;
                String[] arrTemp = line.split(",");
                for (int i = 0; i < arrTemp.length; i++) {
                    arr[i] = Double.parseDouble(arrTemp[i]);
                }
                int color;
                if (count < 800) {
                    color = 0x4000ff00;
                } else if (count < 1448) {
                    color = 0x40ffff00;
                } else if (count < 2005) {
                    color = 0x40ff9933;
                } else {
                    color = 0x40ff0000;
                }
                Polygon polygon = mMap.addPolygon(new PolygonOptions().clickable(true).zIndex((float) arr[4]).geodesic(true)
                        .add(new LatLng(arr[0], arr[2]), new LatLng(arr[0], arr[3]), new LatLng(arr[1], arr[3]), new LatLng(arr[1], arr[2]))
                        .strokeColor(color)
                        .fillColor(color)
                        .strokeWidth(0));

                mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(Polygon polygon) {
                        List<LatLng> points = polygon.getPoints();
                        double Lat = (points.get(0).latitude + points.get(1).latitude + points.get(2).latitude + points.get(3).latitude)/4.0;
                        double Long = (points.get(0).longitude + points.get(1).longitude + points.get(2).longitude + points.get(3).longitude)/4.0;
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Lat, Long))
                                .title(Float.toString(polygon.getZIndex()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallmarker)));
                        marker.showInfoWindow();
                        System.out.println(polygon.getZIndex());
                    }
                });

                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}