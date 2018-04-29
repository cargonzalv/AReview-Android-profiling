package com.example.g.myfirstapp;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Fragment;

import com.example.g.myfirstapp.Classes.PlaceOwn;
import com.example.g.myfirstapp.Classes.UserFireBase;
import com.example.g.myfirstapp.Fragments.HomeFragment;
import com.example.g.myfirstapp.Fragments.LoginFragment;
import com.example.g.myfirstapp.Fragments.MapFragment;
import com.example.g.myfirstapp.Fragments.PlacesFragment;
import com.example.g.myfirstapp.Fragments.ReviewFragment;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        MapFragment.OnFragmentInteractionListener,
        HomeFragment.onHomeFragmentSelectedListener,
        LoginFragment.onLoginFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener, OnCompleteListener<Void>
{

    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String EXTRA_DISPLAY_NAME = "EXTRA_DISPLAY_NAME";
    private LocationHandler locationHandler;
    private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction fragmentTransaction;
    FirebaseFirestore db;

    //for toast
    private Context context;
    int duration = Toast.LENGTH_SHORT;
    private String id;
    private String name;
    private ArrayList<UserFireBase> friends;
    private FirebaseAuth auth;
    private HomeFragment homeFragment;
    private ArrayList<PlaceOwn> places;
    private double lat;
    private double lng;
    private Location mLastLocation;
    private UserFireBase currentUser;
    private ReviewFragment reviewFragment;
    private LoginFragment loginFragment;
    private PlacesFragment placesFragment;
    private MapFragment mapFragment;

    private void changeFragment(Fragment fragment,String tag,Bundle args) {
        if(!fragment.isAdded())
        {
            if(args!=null)
                fragment.setArguments(args);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment,tag);
            fragmentTransaction.commit();
        }
    }
    private void changeToAccount(UserFireBase userFireBase, boolean isRespondingRequest, boolean isConsulting) {
        if(loginFragment==null)
        {
            loginFragment = new LoginFragment();
        }
        if(userFireBase!=null)
        {
            areFriends(currentUser,userFireBase,isRespondingRequest,isConsulting);
        }
        else
        {
            Bundle args = new Bundle();
            args.putSerializable(LoginFragment.USERNAME, userFireBase);
            args.putSerializable(LoginFragment.CURRENT_USER, currentUser);
            args.putBoolean(LoginFragment.IS_RESPONDING_REQUEST, isRespondingRequest);
            args.putBoolean(LoginFragment.IS_CONSULTING, isConsulting);
            changeFragment(loginFragment,"loginFragment",args);
        }
    }




    private void openMap() {
        if(!isNetworkAvailable()){
            toast("Necesitas internet para ver el mapa");
        }
        else if(!isLocationServiceEnabled())
        {
            toast("Necesitas encender tu ubicación para ver el mapa");
        }
        else
        {
            Bundle args= new Bundle();
            args.putSerializable(MapFragment.CURRENT_USER,currentUser);
            args.putSerializable(MapFragment.PLACES,places);
            args.putDouble(MapFragment.LAT,lat);
            args.putDouble(MapFragment.LNG,lng);
            if(mapFragment==null)
            {
                mapFragment = new MapFragment();
                mapFragment.setArguments(args);
            }
            changeFragment(mapFragment,"mapFragment",null);
        }
    }

    public void toast(String text) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onSuccessMainActivity", "onSuccess:1 ");
        enableLocation();
        Log.d("onSuccessMainActivity", "onSuccess:2 ");
        auth = FirebaseAuth.getInstance();
        homeFragment= new HomeFragment();
        id=auth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();
        getCurrentUser();
        getFriends();
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        //Set default
        navigation.setSelectedItemId(R.id.navigation_review);
        context = getApplicationContext();
        id=getIntent().getStringExtra(EXTRA_SESSION_ID);
        name=getIntent().getStringExtra(EXTRA_DISPLAY_NAME);
        Log.d(EXTRA_SESSION_ID, "id: "+id);
        Log.d(EXTRA_SESSION_ID, "id: "+name);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(id!=null && name !=null)
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MainActivity.EXTRA_SESSION_ID, id);
            editor.putString(MainActivity.EXTRA_DISPLAY_NAME, name);
            editor.commit();
        }
        else
        {
            id  = sharedPref.getString(EXTRA_SESSION_ID, "none");
            name = sharedPref.getString(EXTRA_DISPLAY_NAME, "none");
        }
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean isLocationServiceEnabled(){
        LocationManager locationManager = null;
        boolean gps_enabled= false,network_enabled = false;
        if(locationManager ==null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }
        return gps_enabled || network_enabled;
    }

    @Override
    public void onUserSelected(UserFireBase userFireBase,boolean isRespondingRequest,boolean isConsulting) {
        changeToAccount(userFireBase,isRespondingRequest,isConsulting);
    }



    private void getFriends()
    {
        Log.d("idIsNUll!", "getFriends: "+id);
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("ErrorEN", auth.getCurrentUser().getDisplayName());
                        friends = UserFireBase.getFriends((HashMap<String, Object>) document.get("friends"));
                        if(friends==null)
                        {
                            friends=new ArrayList<>();
                        }
                    } else {
                        Log.d("TAGDocumentReference", "No such document");
                    }
                } else {
                    Log.d("TAGDocumentReference", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void updateFriends() {
        getCurrentUser();
        changeFragment(homeFragment,"homeFragment",null);
        //getFriends();
    }

    private void enableLocation()
    {
        LocationCallback lmLocationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                    lat=mLastLocation.getLatitude();
                    lng=mLastLocation.getLongitude();
                    saveLocation();
                    Log.d("onSuccessMainActivity", "onSuccess: "+lat+" "+lng);
                    getProducts();
                }
            };
        };
        locationHandler= new LocationHandler(this,lmLocationCallback);
        locationHandler.start();
    }

    private void saveLocation()
    {
        LocationLog locationLog = new LocationLog(auth.getCurrentUser().getUid(),lat,lng);
        db.collection("locations").document().set(locationLog.getMap()).addOnCompleteListener(this);
    }

    //MainActivity takes the responsability of getting the products from google api:
    private void getProducts()
    {
        String key = getResources()
                .getString(getResources()
                        .getIdentifier("google_places_key", "string", getPackageName()));
        String urlEndpoint = String.format(Locale.US, PlacesHandler.URL_PLACES, lat, lng, key);

        JsonTask jsonTask = new JsonTask(getApplicationContext(), new OnEventListener<String>() {
            @Override
            public void onSuccess(String result)
            {

                Log.d("onSuccessMainActivity", "onSuccess: places");
                PlacesHandler placesHandler = new PlacesHandler(result);
                try
                {
                    places = placesHandler.getPlaces(lat,lng);
                }
                catch (Exception e)
                {
                    toast("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                toast("ERROR: Check your intenet connection");
            }
        });
        jsonTask.execute(urlEndpoint);
    }
    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        locationHandler.stopUpdates();

    }
    /*Ir a una activity con la info del restaurante*/
    public void infoRestaurante(String nombre, String direccion, String id){
        Intent inten  = new Intent(this,RestaurantActivity.class);
        inten.putExtra("nombreRestaurante",nombre);
        inten.putExtra("direccionRestaurante",direccion);
        inten.putExtra("placeId",id);
        startActivity(inten);
    }

    private void getCurrentUser()
    {
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUser=new UserFireBase(document);
                        homeFragment.updateUser(currentUser);
                        //All fragments should be updated ?
                    }
                    else
                    {
                        Log.d("TAGDocumentReference", "No such document");
                    }
                }
                else
                {
                    Log.d("TAGDocumentReference", "get failed with ", task.getException());
                }
            }
        });
    }

    private void areFriends(final UserFireBase pCurrentUser, final UserFireBase pUserFireBase, final boolean isRespondingRequest, final boolean isConsulting)
    {
        db.collection("users").document(pCurrentUser.getId())
                .collection("friends").document(pUserFireBase.getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                boolean areFriends;
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        areFriends=true;
                        Log.d("TAGDocumentReference", "exists(");

                    }
                    else
                    {
                        Log.d("TAGDocumentReference", "No such document");
                        areFriends=false;
                    }
                    Bundle args = new Bundle();
                    args.putSerializable(LoginFragment.USERNAME, pUserFireBase);
                    args.putSerializable(LoginFragment.CURRENT_USER, pCurrentUser);
                    args.putBoolean(LoginFragment.ARE_FRIENDS, areFriends);
                    args.putBoolean(LoginFragment.IS_RESPONDING_REQUEST, isRespondingRequest);
                    args.putBoolean(LoginFragment.IS_CONSULTING, isConsulting);
                    changeFragment(loginFragment,"loginFragment",args);
                }
                else
                {

                    Log.d("TAGDocumentReference", "isNotSuccessful()");
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                Bundle args = new Bundle();
                args.putSerializable(LoginFragment.CURRENT_USER,currentUser);
                changeFragment(homeFragment,"homeFragment",args);
                return true;
            case R.id.navigation_places:
                if(placesFragment==null)
                {
                    placesFragment = new PlacesFragment();
                    args= new Bundle();
                    args.putSerializable(PlacesFragment.PLACES,places);
                    placesFragment.setArguments(args);
                }
                changeFragment(placesFragment,"placesFragment",null);
                return true;
            case R.id.navigation_review:
                if(reviewFragment==null)
                {
                    reviewFragment = new ReviewFragment();
                }
                changeFragment(reviewFragment,"reviewFragment",null);
                return true;
            case R.id.navigation_map:
                openMap();
                return true;
            case R.id.navigation_account:
                changeToAccount(null, false, false);
                return true;
        }
        return false;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task)
    {
        Log.d("locationsResult", "onComplete: "+task.isSuccessful());
    }
}
