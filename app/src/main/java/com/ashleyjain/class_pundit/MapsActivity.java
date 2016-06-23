package com.ashleyjain.class_pundit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    ExpandableRelativeLayout expandableLayout;
    TextView title,address,classes,phone2,mail;
    LikeButton favourite_button;
    Context context;
    Circle shape;
    Marker marker;
    double radius;
    ListView lv;
    ArrayList<Marker> markersList;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int num_near = 0;
    HashMap hm = new HashMap();
    HashMap hm2;
    List<providerdetail> plist;
    private float currentZoom = -1;

    //drawer
    public static Drawer drawer = null;
    DrawerBuilder builder=null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //KeyboardDown.keyboardDown();
        switch (item.getItemId()) {
            case R.id.search:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                return true;

            case R.id.filter:
                FragmentManager fm = getSupportFragmentManager();
                filterdialog overlay = new filterdialog();
                overlay.show(fm, "FragmentDialog");
                return  true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Class-Pundit");
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer();
                // KeyboardDown.keyboardDown();
            }
        });

        //drawer
        builder = new DrawerBuilder()
                .withActivity(this)
                .withDrawerWidthDp(200)
                .withTranslucentNavigationBar(false)
                .withTranslucentStatusBar(false)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggle(true);

        drawer = builder.build();
        builder.addDrawerItems(new PrimaryDrawerItem().withName("Favourite"))
                .addDrawerItems(new PrimaryDrawerItem().withName("Contact us"))
                .addDrawerItems(new PrimaryDrawerItem().withName("About us"))
        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                String name = ((Nameable) drawerItem).getName().toString();
                View dilogview = null;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                if(name.equals("About us")){
                    dilogview = (LayoutInflater.from(context)).inflate(R.layout.aboutus, null);
                    alertBuilder.setView(dilogview);
                    Dialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else if(name.equals("Contact us")){
                    dilogview = (LayoutInflater.from(context)).inflate(R.layout.contactus, null);
                    alertBuilder.setView(dilogview);
                    Dialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else if(name.equals("Favourite")){
                    //final Dialog dialog = new Dialog(context);

                    View view2 = getLayoutInflater().inflate(R.layout.favouritelist, null);
                    lv = (ListView) view2.findViewById(android.R.id.list);
                    ArrayList<providerdetail> favouriteList;
                    Set entrySet = hm.entrySet();
                    Iterator iterator = entrySet.iterator();
                    favouriteList = new ArrayList<>();

                    while(iterator.hasNext()){
                        Map.Entry mapping = (Map.Entry)iterator.next();
                        providerdetail pd = (providerdetail) mapping.getValue();
                        if(pd.isLiked()){
                            favouriteList.add(pd);
                        }
                    }

                    favouriteAdapter adapter = new favouriteAdapter(context, favouriteList);

                    p(lv.toString());
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(null);

//                    dialog.setContentView(view2);
//                    dialog.show();
                    alertBuilder.setView(view2);
                    Dialog dialog = alertBuilder.create();
                    dialog.show();
                }

                return false;
            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;

        expandableLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);
        title = (TextView) findViewById(R.id.title);
        address = (TextView) findViewById(R.id.address);
        classes = (TextView) findViewById(R.id.classes);
        phone2 = (TextView) findViewById(R.id.phone);
        mail = (TextView) findViewById(R.id.email);

        favourite_button = (LikeButton) findViewById(R.id.favourite_button);

//        expandableLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                expandableLayout.collapse();
//            }
//        });

        final Button inc = (Button) findViewById(R.id.inc);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shape!=null){
                    mMap.clear();
                    shape.setRadius(radius=radius+500);
                    reDisplay();
                }
            }
        });
        Button dec = (Button) findViewById(R.id.dec);
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shape!=null){
                    mMap.clear();
                    shape.setRadius(radius=radius-500);
                    reDisplay();
                }
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
        radius = 8046.72;
        LatLng sydney = new LatLng(37.610029, -122.079577);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.homem)).draggable(true));
        CircleOptions options = new CircleOptions().center(sydney).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
        shape = mMap.addCircle(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f));
        JSON();

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
                mMap.clear();
                reDisplay();
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition position) {
                if (position.zoom != currentZoom){
                    currentZoom = position.zoom;
                    removeShape();
                    mMap.clear();
                    reDisplay();
                }
            }
        });

        //gps
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
        mMap.setMyLocationEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).snippet(place.getAddress().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.homem)).draggable(true));
                        CircleOptions options = new CircleOptions().center(place.getLatLng()).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
                        shape = mMap.addCircle(options);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                        JSON();
                Log.i("search", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("search", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }












    void removeShape(){
        if(shape!=null){
            shape.remove();
            shape = null;
        }
    }

    void removeMarker(){
        if(marker!=null){
            marker.remove();
            marker = null;
        }
    }

    void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void JSON(){
        p("JSON");
        num_near = 0;
        //final String url = "http://192.168.8.100/cpnew/allp.json";
        final String url = "http://192.168.8.105/JSONallp.txt";
        plist = new ArrayList<>();
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
                                    LatLng tmp = new LatLng(js.getDouble("lat"),js.getDouble("lng"));
                                    double distance_home = latlandist(tmp,shape.getCenter());

                                    if(distance_home<=shape.getRadius()){
                                        num_near++;
                                        //Marker tmpmarker = mMap.addMarker(new MarkerOptions().position(tmp).title(js.getString("name_provider")).snippet(js.getString("address")).icon(BitmapDescriptorFactory.fromResource(resID)));
                                        providerdetail ptmp = new providerdetail(js.getDouble("lat"),js.getDouble("lng"),js.getString("mycat"),js.getString("name_provider"),js.getString("phone"),js.getString("email"),js.getString("address"),js.getString("website"),js.getString("countrycode"),js.getString("username"));
                                        plist.add(ptmp);
                                        if(!hm.containsKey(tmp)){
                                            p("does_not_contain");
                                            hm.put(tmp,ptmp);
                                        }
                                        else{
                                            p("contains");
                                        }
                                    }

//                                    var groups = geolocgroup1(togroup, gmap.zoom, markers_density);
//                                    drawgroups(groups);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        List<customArray> groups = geolocgroup1(plist,currentZoom);
                        drawgroups(groups);
                        msgformatching(num_near);
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
        Volley.newRequestQueue(this).add(getRequest);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.isDraggable()){
                    final List<providerdetail> pd = (List<providerdetail>) hm2.get(marker.getPosition());
                    title.setText(pd.get(0).getName_provider());
                    address.setText(pd.get(0).getAddress());
                    classes.setText("Classes offered: "+pd.get(0).getMycat());
                    phone2.setText("Phone: "+pd.get(0).getPhone());
                    mail.setText("Mail: "+pd.get(0).getEmail());
                    favourite_button.setLiked(pd.get(0).isLiked());
                    favourite_button.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            pd.get(0).setLiked(true);
                        }
                        @Override
                        public void unLiked(LikeButton likeButton) {
                            pd.get(0).setLiked(false);
                        }
                    });
//                    final providerdetail pd = (providerdetail) hm.get(marker.getPosition());
//                    title.setText(marker.getTitle());
//                    address.setText(marker.getSnippet());
//                    classes.setText("Classes offered: "+pd.getMycat());
//                    phone2.setText("Phone: "+pd.getPhone());
//                    mail.setText("Mail: "+pd.getEmail());
//                    favourite_button.setLiked(pd.isLiked());
//                    favourite_button.setOnLikeListener(new OnLikeListener() {
//                        @Override
//                        public void liked(LikeButton likeButton) {
//                            pd.setLiked(true);
//                        }
//                        @Override
//                        public void unLiked(LikeButton likeButton) {
//                            pd.setLiked(false);
//                        }
//                    });
                    expandableLayout.expand();
                }
                else{
                    marker.showInfoWindow();
                }
                return true;
            }
        });
    }

    double marker_density = 0.00600;
    long scale = 1183315100;

    List<List<providerdetail>> cluster_points(List prolist,float zoom){
        List<List<providerdetail>> clusters = new ArrayList<>();
        int size = 0;
        while(prolist.size()>0){
            List<providerdetail> cluster = new ArrayList<>();
            for(int i=0;i<prolist.size();i++){
                LatLng l1=new LatLng(((providerdetail)prolist.get(0)).getLat(),((providerdetail)prolist.get(0)).getLng());
                LatLng l2 = new LatLng(((providerdetail)prolist.get(i)).getLat(),((providerdetail)prolist.get(i)).getLng());
                double distance = latlandist(l1,l2);
                p(distance*Math.pow(2,zoom)+"<="+ marker_density * scale);
                if(distance*Math.pow(2,zoom) <= marker_density * scale){
                    cluster.add((providerdetail) prolist.get(i));
                    prolist.remove(i);
                }
            }
            p("cluster size: "+cluster.size());
            clusters.add(cluster);
            size+=cluster.size();
        }
        p("totalsize: "+size);
        return clusters;
    }

    List<customArray> geolocgroup1(List<providerdetail> plist,float zoom){
        List<List<providerdetail>> groupslist = cluster_points(plist,zoom);
        List<customArray> newgroupslist = new ArrayList<>();

        for(int i=0;i<groupslist.size();i++){
            List<providerdetail> group = groupslist.get(i);
            int length = group.size();
            double mid_lat = 0;
            double mid_lon = 0;
            for(int j=0;j<length;j++){
                mid_lat+=group.get(j).getLat();
                mid_lon+=group.get(j).getLng();
            }
            if(group.size()>0)
            newgroupslist.add(new customArray(mid_lat/length,mid_lon/length,group.get(0),group));
        }
        return newgroupslist;
    }

    void drawgroups(List<customArray> groups){
        //Marker tmpmarker = mMap.addMarker(new MarkerOptions().position(tmp).title(js.getString("name_provider")).snippet(js.getString("address")).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1)));
        hm2 = new HashMap();
        for(int i=0;i<groups.size();i++){
            customArray group = groups.get(i);
            LatLng tmp = new LatLng(group.getLat(),group.getLog());
            p("size: "+group.getListp().size());
            String markiconame = markericon_name(group.getListp().size());
            int resID = getResources().getIdentifier(markiconame , "drawable", getPackageName());
            hm2.put(tmp,group.getListp());
            Marker tmpmarker = mMap.addMarker(new MarkerOptions().position(tmp).icon(BitmapDescriptorFactory.fromResource(resID)));
        }
    }

    //    function cluster_points(plist, f) { //f (p0, p1) -> is p0 friend of p1
//        var clusters = [];
//        var limit = 100;
//        while(plist.length > 0) {
//            limit--;
//            cluster = filter(function(x) {
//                return f(x,plist[0]);
//            }, plist);
//            plist = listaminusb(plist, cluster);
//            clusters.push(cluster);
//        }
//        return clusters;
//
//    }
//    function geolocgroup(plist, zoom, screen_distance) {
//        var scale = 1183315100;//scale on google map when zoom = 0
//        return cluster_points(plist, function(x,y) {
//            var distance = latlandist(x[0], x[1], y[0], y[1]);//meter
//            return (distance*Math.pow(2, zoom) <= screen_distance*scale);
//        });
//    }

//    function geolocgroup1(plist, zoom, screen_distance) {
//        var groupslist = geolocgroup(plist, zoom, screen_distance);// Assert( Every cluster has alteast 1 point in it)
//        return map(function(x) {
//            var cluster_llsum = fold(function(y,z) {
//                return [y[0]+z[0], y[1]+z[1]];
//            } ,x, [0,0]);
//            var lenc = x.length;//number of point in that cluster
//            return [cluster_llsum[0]/lenc, cluster_llsum[1]/lenc, x[0][2], map(function(y){
//                return y[2];
//            }, x)];
//        } ,groupslist);
//
//    }

//    var markers_density = 0.00600;

//    function drawgroups(groups) {
//        var myl = [];
//        mapp(function(x) {
//            if(haskey(x, "locmark")) {
//                x.locmark.setVisible(false);
//            }
//        }, providers);
//        map(function(x) {// lat:x[0], lng:x[1], provider_id:x[2], list_of_providers:x[3]
//            if(!haskey(providers[x[2]], "locmark")) {
//                var prov_loc= new google.maps.Marker({
//                        position: {lat: x[0], lng: x[1]},
//                });
//                google.maps.event.addListener(prov_loc, 'click', function() {
//                    //$("#providerinfo").openModal();
//                    bcard.openbcard(providers[x[2]].providers);
//                });
//                prov_loc.setMap(gmap);
//                providers[x[2]].locmark = prov_loc;
//            }
//            var thismarker = providers[x[2]].locmark;
//            providers[x[2]].providers = x[3];
//
//            thismarker.setIcon(markericon_name(x[3].length));
//            //thismarker.setLabel('45');
//            thismarker.setPosition({lat: x[0], lng: x[1]});
//            thismarker.setVisible(true);
//        }, groups);
//    }






    class customArray{
        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLog() {
            return log;
        }

        public void setLog(double log) {
            this.log = log;
        }

        public providerdetail getA() {
            return a;
        }

        public void setA(providerdetail a) {
            this.a = a;
        }

        public List<providerdetail> getListp() {
            return listp;
        }

        public void setListp(List<providerdetail> listp) {
            this.listp = listp;
        }

        double lat,log;
        providerdetail a;
        List<providerdetail> listp;
        public customArray(double lat,double log,providerdetail a,List<providerdetail> listp){
            this.lat = lat;
            this.log = log;
            this.a = a;
            this.listp = listp;
        }
    }

    void reDisplay(){
        marker = mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.homem)).draggable(true));
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
        JSON();
    }

    void p(String print){
        System.out.println(print);
    }
    void t(String toast){
        Toast.makeText(MapsActivity.this, toast, Toast.LENGTH_SHORT).show();
    }

    //return particular markericon
    String markericon_name(int num) {
        String ind = "1";
        int[] bp;
        if(num==0)
            num=1;
        if(num<11) {
            ind = ""+num;
        } else {
            bp = new int[]{10, 20, 30, 50, 100, 200, 300, 500, 700, 1000};
            for(int i=0; i<bp.length; i++) {
                if(num>bp[i]) {
                    ind = (bp[i] == 1000 ? "1kp":(bp[i]+"p"));
                }
            }
        }
        return "marker"+ind;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        t("No Connection");
    }

    double latlandist(LatLng l1,LatLng l2){
        double R = 7270000;
        double ph1 = degtorad(l1.latitude);
        double ph2 = degtorad(l2.latitude);
        double dph = degtorad(l2.latitude-l1.latitude);
        double dlem = degtorad(l2.longitude-l1.longitude);
        double a = Math.sin(dph/2) * Math.sin(dph/2) + Math.sin(ph1)*Math.sin(ph2)*Math.sin(dlem/2)*Math.sin(dlem/2);
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return R*c;
    }

    double degtorad(double deg){
        return deg*(Math.PI/180);
    }

    void msgformatching(int num_near){
        double intrad = Math.round(0.621371*shape.getRadius()/1000);
        if(num_near>0)
            t(num_near+" Locations matching in "+intrad+" Miles");
        else
            t("No Location matching in "+intrad+" Miles");
    }

    public class providerdetail{
        String mycat,name_provider,phone,email,address,website,countrycode,username;
        double lat,lng;

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        boolean liked;

        public providerdetail(double lat, double lng, String mycat, String name_provider, String phone, String email, String address, String website,String countrycode,String username){
            this.lat = lat;
            this.lng = lng;
            this.mycat = mycat;
            this.name_provider = name_provider;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.website = website;
            this.countrycode = countrycode;
            this.username = username;
            liked = false;
        }

        public String getMycat() {
            return mycat;
        }

        public void setMycat(String mycat) {
            this.mycat = mycat;
        }

        public String getName_provider() {
            return name_provider;
        }

        public void setName_provider(String name_provider) {
            this.name_provider = name_provider;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getCountrycode() {
            return countrycode;
        }

        public void setCountrycode(String countrycode) {
            this.countrycode = countrycode;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

    }

}
