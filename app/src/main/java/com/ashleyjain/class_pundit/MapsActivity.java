package com.ashleyjain.class_pundit;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context context;
    Circle shape;
    Marker marker;
    double radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        JSON();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        View view = getSupportActionBar().getCustomView();

        final EditText editText = (EditText) view.findViewById(R.id.search_bar);
        ImageButton search = (ImageButton) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = editText.getText().toString();

                Geocoder gc = new Geocoder(context);
                try {
                    hideKeyboard();

                    List<Address> list = gc.getFromLocationName(location, 1);
                    if(!list.isEmpty()){

                        removeShape();
                        removeMarker();

                        Address add = list.get(0);
                        p("listt: "+list.toString());
                        double lat = add.getLatitude();
                        double lng = add.getLongitude();
                        LatLng latlng = new LatLng(lat, lng);

                        marker = mMap.addMarker(new MarkerOptions().position(latlng).title(add.getLocality()).snippet(add.getCountryName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.homem)).draggable(true));
                        CircleOptions options = new CircleOptions().center(new LatLng(lat,lng)).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
                        shape = mMap.addCircle(options);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12.0f));

                    }
                    else{
                        t("Location not found!!");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        final Button inc = (Button) findViewById(R.id.inc);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shape!=null)
                    shape.setRadius(radius=radius+500);
            }
        });
        Button dec = (Button) findViewById(R.id.dec);
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shape!=null)
                    shape.setRadius(radius=radius-500);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
        radius = 5000;
        LatLng sydney = new LatLng(37.610029, -122.079577);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.homem)).draggable(true));
        CircleOptions options = new CircleOptions().center(sydney).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
        shape = mMap.addCircle(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                removeShape();
                LatLng ll = marker.getPosition();
                CircleOptions options = new CircleOptions().center(new LatLng(ll.latitude,ll.longitude)).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
                shape = mMap.addCircle(options);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                removeShape();
                Geocoder gc = new Geocoder(context);
                LatLng ll = marker.getPosition();
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(ll.latitude,ll.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Address add = list.get(0);
                marker.setTitle(add.getLocality());
                marker.setSnippet(add.getCountryName());
                marker.showInfoWindow();
                CircleOptions options = new CircleOptions().center(new LatLng(ll.latitude,ll.longitude)).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
                shape = mMap.addCircle(options);

            }
        });

    }














    void removeShape(){
        if(shape!=null){
            shape.remove();
            shape = null;
        }
    }

    void removeMarker(){

    }

    void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void JSON(){

        RequestQueue queue = Volley.newRequestQueue(this); // this = context
        final String url = "https://www.dropbox.com/s/7fg87yyp33cib4c/JSONallp.txt?dl=0";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Iterator<?> keys = response.keys();

                        while( keys.hasNext() ) {
                            String key = (String)keys.next();
                            try {
                                if ( response.get(key) instanceof JSONObject ) {
                                    JSONObject js = (JSONObject) response.get(key);
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(js.getDouble("lat"),js.getDouble("lng"))).title(js.getString("name_provider")).snippet(js.getString("address")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error",error.toString());
                    }
                }
        );
        queue.add(getRequest);
    }

    void p(String print){
        System.out.println(print);
    }
    void t(String toast){
        Toast.makeText(MapsActivity.this, toast, Toast.LENGTH_SHORT).show();
    }
}
