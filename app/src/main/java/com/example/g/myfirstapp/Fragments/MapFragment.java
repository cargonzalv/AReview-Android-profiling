package com.example.g.myfirstapp.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.g.myfirstapp.Classes.PlaceOwn;
import com.example.g.myfirstapp.Classes.UserFireBase;
import com.example.g.myfirstapp.LocationLog;
import com.example.g.myfirstapp.R;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    //Constants
    public static final String PLACES = "places";
    public static final java.lang.String LNG = "lng";
    public static final java.lang.String LAT = "lat";
    public static final String CURRENT_USER = "currentUser";

    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserFireBase user;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;


    private OnFragmentInteractionListener mListener;
    private ArrayList<PlaceOwn> places;
    private double lng;
    private double lat;

    private Map<Marker, PlaceOwn> markers = new HashMap<>(); // Map to bind a Strings to Markers
    private ArrayList<LocationLog> friends;


    public MapFragment()
    {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance()
    {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            user = (UserFireBase) getArguments().getSerializable(CURRENT_USER);
            places = (ArrayList<PlaceOwn>) getArguments().getSerializable(PLACES);
            lat = getArguments().getDouble(LAT);
            lng = getArguments().getDouble(LNG);
        }
        friends=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstaceState)
    {
        super.onViewCreated(view, savedInstaceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement onLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        if (getContext() != null)
        {
            mGoogleMap = googleMap;
            mGoogleMap.setMyLocationEnabled(true);
            MapsInitializer.initialize(getContext());
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.setTrafficEnabled(true);
            mGoogleMap.setIndoorEnabled(true);
            getFriendsLocations();
            mGoogleMap.setOnMarkerClickListener(this);
        }
    }


    public void putMarkers(ArrayList<PlaceOwn> arr)
    {
        LatLng latLng = new LatLng(lat, lng);
        //move map camera
        CameraPosition userPosition = CameraPosition.builder().target(latLng).zoom(17).bearing(0).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(userPosition));

        for (int i = 0; arr != null && i < arr.size(); i++)
        {
            PlaceOwn actual = arr.get(i);
            MarkerOptions m = new MarkerOptions();
            BitmapDescriptor img = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_directions_walk_black_24dp);
            m.icon(img);
            m.position(new LatLng(actual.getLat(), actual.getLng()));
            m.title(actual.getName());
            Marker actualMarker = mGoogleMap.addMarker(m);
            markers.put(actualMarker, actual);


        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        PlaceOwn p = markers.get(marker);
        mListener.infoRestaurante(p.getName(), p.getAddress(), p.getId());
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        void infoRestaurante(String nombre, String direccion, String id);
    }

    public void getFriendsLocations()
    {
        db.collection("lastUserLocation")
                .whereGreaterThan("visibleTo." + user.getId(), 0)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot document : task.getResult())
                            {
                                Map location = document.getData();
                                LocationLog l = new LocationLog(location);
                                friends.add(l);
                                Log.d("friendsFound", "onComplete: "+location);
                            }
                            putMarkersFriends(friends);
                        }
                        else
                        {
                            Log.d("friendsFound", "onComplete: NotFound");

                            //mCallback.toast("Check your internet connection");
                        }
                    }
                });
    }

    private void putMarkersFriends(ArrayList<LocationLog> friends)
    {
        LatLng latLng = new LatLng(lat, lng);
        //move map camera
        CameraPosition userPosition = CameraPosition.builder().target(latLng).zoom(17).bearing(0).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(userPosition));

        for (int i = 0; friends != null && i < friends.size(); i++)
        {
            LocationLog actual = friends.get(i);
            MarkerOptions m = new MarkerOptions();
            BitmapDescriptor img = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_directions_walk_black_24dp);
            m.icon(img);
            m.position(new LatLng(actual.getLat(), actual.getLng()));
            m.title(actual.getUsername());
            Marker actualMarker = mGoogleMap.addMarker(m);
            //markers.put(actualMarker, actual);
        }
    }
}