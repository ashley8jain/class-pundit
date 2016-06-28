package com.ashleyjain.class_pundit;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    TextView title,address,classes,phone2,mail,outof;
    ImageButton left,right;
    LikeButton favourite_button;
    Context context;
    Circle shape;
    Marker marker;
    double radius;
    ListView lv;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int num_near = 0;
    HashMap hm = new HashMap();
    HashMap hm2;
    private float currentZoom = -1;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences pref;
    List<providerdetail> overallPList,plist,activep;
    final List<providerdetail>[] pd = new ArrayList[]{null};
    final int[] i = {0};
    DialogPlus dialogPlus;
    Marker homemarker;

    public static Drawer drawer = null;
    DrawerBuilder builder=null;

    //final String url = "http://192.168.8.100/cpnew/allp.json";
    final String url = "http://192.168.0.102/JSONallp.txt";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                final View dilogview = (LayoutInflater.from(context)).inflate(R.layout.search, null);;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                final EditText classes = (EditText) dilogview.findViewById(R.id.classes);
                Button chplace = (Button) dilogview.findViewById(R.id.chplace);
                alertBuilder.setView(dilogview).setCancelable(true);
                final Dialog dialog = alertBuilder.create();
                dialog.show();
                chplace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                        try {
                            Intent intent =
                                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                            .build((Activity) context);
                            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                        } catch (GooglePlayServicesRepairableException e) {
                            // TODO: Handle the error.
                        } catch (GooglePlayServicesNotAvailableException e) {
                            // TODO: Handle the error.
                        }
                    }
                });
                Button search = (Button) dilogview.findViewById(R.id.searchh);
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(classes.getText().equals(""))
                            classes.setError("Empty");
                        else{
                            dialog.hide();
                            final ProgressDialog dialog2 = ProgressDialog.show(context, "", "Loading...", true);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.classpundit.com/gen.php/ajaxactions",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                JSONObject dataobject = jsonResponse.getJSONObject("data");
                                                JSONArray activep = dataobject.getJSONArray("activep");
                                                t(activep.toString());
                                                dialog2.dismiss();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                dialog2.dismiss();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                            dialog2.dismiss();
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("action", "search");
                                    params.put("keyw", classes.getText().toString());
                                    System.out.println(params);
                                    return params;
                                }

                            };
                            Volley.newRequestQueue(context).add(stringRequest);

                        }
                    }
                });

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

        pref = getApplicationContext().getSharedPreferences("FavouriteList", 0); // 0 - for private mode
        editor = pref.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Class-Pundit");
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer();
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
                .addDrawerItems(new PrimaryDrawerItem().withName("How It Works"))
        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                String name = ((Nameable) drawerItem).getName().toString();
                if(name.equals("About us")||name.equals("Contact us")||name.equals("How It Works")){
                    View dilogview = null;
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

                    if(name.equals("About us")){
                        dilogview = (LayoutInflater.from(context)).inflate(R.layout.aboutus, null);
                    }
                    else if(name.equals("Contact us")){
                        dilogview = (LayoutInflater.from(context)).inflate(R.layout.contactus, null);
                    }
                    else if(name.equals("How It Works")){
                        dilogview = (LayoutInflater.from(context)).inflate(R.layout.tutorial, null);
                    }
                    alertBuilder.setView(dilogview).setCancelable(true);
                    Dialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else if(name.equals("Favourite")){
                    ArrayList<providerdetail> favouriteList;
                    favouriteList = new ArrayList<>();

                    for(int i=0;i<overallPList.size();i++){
                        if(pref.getBoolean(overallPList.get(i).getId(),false)){
                            favouriteList.add(overallPList.get(i));
                        }
                    }

                    i[0] = 0;
                    pd[0] = favouriteList;
                    if(pd[0].size()!=0){
                        if(pd[0].size()==1){
                            outof.setVisibility(View.INVISIBLE);
                            left.setVisibility(View.INVISIBLE);
                            right.setVisibility(View.INVISIBLE);
                        }
                        else{
                            outof.setVisibility(View.VISIBLE);
                            left.setVisibility(View.VISIBLE);
                            right.setVisibility(View.VISIBLE);
                        }
                        set_provider_details();
                        dialogPlus.show();
                    }
                    else{
                        t("Empty!! No favourite");
                    }
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

        final Button inc = (Button) findViewById(R.id.inc);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    shape.setRadius(radius=radius+500);
                    reDisplay();
            }
        });
        Button dec = (Button) findViewById(R.id.dec);
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    shape.setRadius(radius=radius-500);
                    reDisplay();
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
        init();
        //JSON();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                shape.setCenter(marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                reDisplay();
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition position) {
                if (position.zoom != currentZoom){
                    currentZoom = position.zoom;
                    reDisplay();
                }
            }
        });
        dialogPlus = DialogPlus.newDialog(context)
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .setPadding(20,20,20,20)
                .setContentHolder(new ViewHolder(R.layout.prov_detail)).create();
        View view = dialogPlus.getHolderView();
        title = (TextView) view.findViewById(R.id.title);
        address = (TextView) view.findViewById(R.id.address);
        classes = (TextView) view.findViewById(R.id.classes);
        phone2 = (TextView) view.findViewById(R.id.phone);
        mail = (TextView) view.findViewById(R.id.email);
        left = (ImageButton) view.findViewById(R.id.left_arrow);
        right = (ImageButton) view.findViewById(R.id.right_arrow);
        outof = (TextView) view.findViewById(R.id.outoftotal);
        favourite_button = (LikeButton) view.findViewById(R.id.favourite_button);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.isDraggable()){

                    i[0] = 0;
                    pd[0] = (List<providerdetail>) hm2.get(marker.getPosition());
                    if(pd[0].size()==1){
                        outof.setVisibility(View.INVISIBLE);
                        left.setVisibility(View.INVISIBLE);
                        right.setVisibility(View.INVISIBLE);
                    }
                    else{
                        outof.setVisibility(View.VISIBLE);
                        left.setVisibility(View.VISIBLE);
                        right.setVisibility(View.VISIBLE);
                    }
                    set_provider_details();

                    //favourite_button.setLiked(((providerdetail)hm.get(pd[0].get(i[0]).getId())).isLiked());
                        dialogPlus.show();
                }
                else{
                    marker.showInfoWindow();
                }
                return true;
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pd[0].get(i[0]).getWebsite()));
                startActivity(browserIntent);
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i[0]--;
                if(i[0]<0){
                    i[0]=pd[0].size()-1;
                }
                set_provider_details();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i[0]++;
                if(i[0]== pd[0].size()){
                    i[0]=0;
                }

                set_provider_details();

//                favourite_button.setOnLikeListener(new OnLikeListener() {
//                    @Override
//                    public void liked(LikeButton likeButton) {
//                        editor.remove(pd[0].get(i[0]).getId());
//                        editor.putBoolean(pd[0].get(i[0]).getId(),true);
//                        editor.commit();
//                    }
//                    @Override
//                    public void unLiked(LikeButton likeButton) {
//                        editor.remove(pd[0].get(i[0]).getId());
//                        editor.putBoolean(pd[0].get(i[0]).getId(),false);
//                        editor.commit();
//                    }
//                });

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

    void set_provider_details(){
        title.setText(pd[0].get(i[0]).getName_provider());
        address.setText(pd[0].get(i[0]).getAddress());
        classes.setText("Classes offered: "+ pd[0].get(i[0]).getMycat());
        if(pd[0].get(i[0]).getPhone().equals("")){
            phone2.setVisibility(View.GONE);
        }
        else{
            phone2.setVisibility(View.VISIBLE);
            phone2.setText(" "+ pd[0].get(i[0]).getPhone());
        }
        if(pd[0].get(i[0]).getEmail().equals("")){
            mail.setVisibility(View.GONE);
        }
        else{
            mail.setVisibility(View.VISIBLE);
            mail.setText(" "+ pd[0].get(i[0]).getEmail());
        }
        outof.setText((i[0]+1)+" of "+pd[0].size());
        favourite_button.setLiked(pref.getBoolean(pd[0].get(i[0]).getId(),false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                homemarker.setPosition(place.getLatLng());
                shape.setCenter(place.getLatLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                reDisplay();
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


    void reDisplay(){
        //        marker = mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.homem)).draggable(true));
//        Geocoder gc = new Geocoder(context);
//        LatLng ll = marker.getPosition();
//        List<Address> list = null;
//        try {
//            list = gc.getFromLocation(ll.latitude,ll.longitude,1);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//        Address add = list.get(0);
//        marker.setTitle(add.getLocality());
//        marker.setSnippet(add.getCountryName());
//        marker.showInfoWindow();
//        CircleOptions options = new CircleOptions().center(new LatLng(ll.latitude,ll.longitude)).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
//        shape = mMap.addCircle(options);
        p("reDisplay.....");
        num_near = 0;
        plist = new ArrayList<>();
        for(int i=0;i<activep.size();i++){
            providerdetail ele = activep.get(i);
            double distance = latlandist(new LatLng(ele.getLat(),ele.getLng()),homemarker.getPosition());
            if(distance<=shape.getRadius()){
                num_near++;
                plist.add(ele);
            }
        }
        p(shape.getRadius()+"");
        List<List<providerdetail>> groups = geolocgroup1(plist,currentZoom);
        drawgroups(groups);
        p(shape.getRadius()+"");
        msgformatching(num_near);

    }

    void init(){
        p("init....");
        activep = new ArrayList<>();
        radius = 8046.72;
        homemarker = mMap.addMarker(new MarkerOptions().position(new LatLng(37.610029, -122.079577)).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.homem)).draggable(true));
        CircleOptions options = new CircleOptions().center(homemarker.getPosition()).radius(radius).fillColor(0x330000FF).strokeColor(Color.RED).strokeWidth(3);
        shape = mMap.addCircle(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homemarker.getPosition(),12.0f));

        overallPList = new ArrayList<>();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        Iterator<?> keys = response.keys();

                        while( keys.hasNext() ) {
                            String key = (String)keys.next();
                            try {
                                if ( response.get(key) instanceof JSONObject ) {
                                    JSONObject js = (JSONObject) response.get(key);
                                    LatLng tmp = new LatLng(js.getDouble("lat"),js.getDouble("lng"));
                                        num_near++;
                                        p("key: "+key);
                                        Marker tmpmarker = mMap.addMarker(new MarkerOptions().position(tmp));
                                        providerdetail ptmp = new providerdetail(key,js.getDouble("lat"),js.getDouble("lng"),js.getString("mycat"),js.getString("name_provider"),js.getString("phone"),js.getString("email"),js.getString("address"),js.getString("website"),js.getString("countrycode"),js.getString("username"),tmpmarker);
                                        overallPList.add(ptmp);
                                        //hm.put(key,ptmp);
                                        if(!pref.contains(key)){
                                            editor.putBoolean(key, false);
                                        }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        editor.commit();
                        p("overallPloist: "+overallPList.toString());
                        activep = overallPList;
                        p("activep: "+activep.toString());
                        reDisplay();
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

    List<List<providerdetail>> geolocgroup1(List<providerdetail> plist,float zoom){

        for(int i=0;i<overallPList.size();i++){
            overallPList.get(i).getMarker().setVisible(false);
        }

        List<List<providerdetail>> groupslist = cluster_points(plist,zoom);
        //List<customArray> newgroupslist = new ArrayList<>();

        for(int i=0;i<groupslist.size();i++){
            List<providerdetail> group = groupslist.get(i);
            int length = group.size();
            double mid_lat = 0;
            double mid_lon = 0;
            for(int j=0;j<length;j++){
                mid_lat+=group.get(j).getLat();
                mid_lon+=group.get(j).getLng();
            }

            if(group.size()>0){
                group.get(0).marker.setPosition(new LatLng(mid_lat/length,mid_lon/length));
                //newgroupslist.add(new customArray(mid_lat/length,mid_lon/length,group.get(0),group));
            }
        }
        return groupslist;
    }

    void drawgroups(List<List<providerdetail>> groups){
        hm2 = new HashMap();
        for(int i=0;i<groups.size();i++){
            List<providerdetail> group = groups.get(i);
            Marker mrk = group.get(0).marker;
            //LatLng tmp = new LatLng(group.getLat(),group.getLog());
            //p("size: "+group.getListp().size());
            String markiconame = markericon_name(group.size());
            int resID = getResources().getIdentifier(markiconame , "drawable", getPackageName());
            mrk.setIcon(BitmapDescriptorFactory.fromResource(resID));
            mrk.setVisible(true);
            hm2.put(mrk.getPosition(),group);
            //hm2.put(tmp,group.getListp());
            //Marker tmpmarker = mMap.addMarker(new MarkerOptions().position(tmp).icon(BitmapDescriptorFactory.fromResource(resID)));
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
        p(shape.getRadius()+"");
        double intrad = Math.round(0.621371*shape.getRadius()/1000);
        if(num_near>0)
            t(num_near+" Locations matching in "+intrad+" Miles");
        else
            t("No Location matching in "+intrad+" Miles");
    }

    public class providerdetail{
        String mycat,name_provider,phone,email,address,website,countrycode,username,id;
        double lat,lng;

        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }

        Marker marker;

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        boolean liked;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public providerdetail(String id, double lat, double lng, String mycat, String name_provider, String phone, String email, String address, String website, String countrycode, String username,Marker marker){
            this.id = id;

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
            this.marker = marker;
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
