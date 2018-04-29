package com.example.g.myfirstapp.Fragments;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.g.myfirstapp.Classes.PlaceOwn;
import com.example.g.myfirstapp.CustomListAdapter;
import com.example.g.myfirstapp.JsonTask;
import com.example.g.myfirstapp.LocationHandler;
import com.example.g.myfirstapp.OnEventListener;
import com.example.g.myfirstapp.PlacesHandler;
import com.example.g.myfirstapp.R;
import com.example.g.myfirstapp.RestaurantActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlacesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlacesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlacesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String PLACES = "places";

    SharedPreferences shared;
    ArrayList<PlaceOwn> placeOwns = new ArrayList<PlaceOwn>();
    String nombres[]={"Cargando..."};
    String direcciones[];

    View mView;
    //datos que paso al activityRestaurante
    //private String nombreRestaurante = "Ningun restaurante";

    //private String direccionRestaurante = "Ninguna direccion";
    private OnFragmentInteractionListener mListener;
    private Location mLastLocation;

    private LocationHandler locationHandler;
    private CustomListAdapter adapter;
    private ListView listView;


    public PlacesFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlacesFragment.
     */
    public static PlacesFragment newInstance(String param1, String param2) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences("App_settings", MODE_PRIVATE);
        if (getArguments() != null) {
            ArrayList<PlaceOwn> placeOwnsParameter = (ArrayList<PlaceOwn>) getArguments().getSerializable(PLACES);
            if(placeOwnsParameter!=null)
            {
                placeOwns=placeOwnsParameter;
            }
        }

        if(placeOwns.isEmpty())
        {
            PlaceOwn test = new PlaceOwn(1,1,"...","Cargando...","...");
            placeOwns.add(test);
        }
        //Toast.makeText(getActivity().getApplicationContext(),   test.getName(), Toast.LENGTH_LONG).show();

    }

    /* lista con los nombres de los restaurantes */
    public String[] listRestaurants(ArrayList<PlaceOwn> placeOwns){
        String nombres[] = new String[placeOwns.size()];
        String direcciones[] = new String[placeOwns.size()];
        for (int i = 0; i< placeOwns.size(); i++){
            PlaceOwn rest = placeOwns.get(i);
            nombres[i]= rest.getName()+".";
        }
        return nombres;
    }
    /* lista con las direcciones de los restaurantes */
    public String[] listAddresses(ArrayList<PlaceOwn> placeOwns){
        String direcciones[] = new String[placeOwns.size()];
        for (int i = 0; i< placeOwns.size(); i++){
            PlaceOwn rest = placeOwns.get(i);
            direcciones[i] = rest.getAddress();
        }
        return direcciones;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_places, container, false);
        listView = (ListView) mView.findViewById(R.id.lvPlaces);
        Button btn = (Button) mView.findViewById(R.id.btn_camera);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera(v);
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("CO")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("DPlace", "Place: " + place.getId());
                infoRestaurante(place.getName().toString(),place.getAddress().toString(),place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("D", "An error occurred: " + status);
            }
        });



        drawList();

        if(!isLocationServiceEnabled()||!isNetworkAvailable())
        {
            String text = "No pudimos conectarnos, cargando datos de tu última ubicación";
            Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            retriveSharedValue();
        }


        return mView;
    }

    private void packagesharedPreferences() {
        SharedPreferences.Editor editor = shared.edit();

        Gson gson = new GsonBuilder().create();
        String s = gson.toJson(placeOwns);
        editor.putString("placeOwns",s);
        editor.apply();
        Log.d("storesharedPreferences","");
    }

    private void retriveSharedValue() {
        String s = shared.getString("placeOwns",null);
        if(s!=null)
        {
            Gson gson = new GsonBuilder().create();
            placeOwns = (ArrayList<PlaceOwn>) gson.fromJson(s,new TypeToken<List<PlaceOwn>>(){}.getType());
            Log.d("retrivesharedPreferences",""+placeOwns.get(0).getName()+placeOwns.size());
            nombres = listRestaurants(placeOwns);
            direcciones = listAddresses(placeOwns);
            drawList();
        }
        else
        {
            String text = "Aún no tienes datos guardados";
            Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void drawList() {


        //final ListView lv1 = (ListView) findViewById(R.id.custom_list);
        adapter=new CustomListAdapter(getActivity(), placeOwns);
        listView.setAdapter(adapter);


        //manejo los eventos al dar un click en el listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //>> obtengo el nombre del restaurante seleccionado
                PlaceOwn p = (PlaceOwn) adapter.getItem(i);
                String nombreRestaurante = p.getName();
                String direccionRestaurante = p.getAddress();
                String id = p.getId();
                infoRestaurante(nombreRestaurante,direccionRestaurante,id);

                Log.d("onItemClickqwdmoppcas", p.toString());
            }
        });
    }

    /*Ir a una activity con la info del restaurante*/
    public void infoRestaurante(String nombre, String direccion, String id){
        Intent inten  = new Intent(getActivity(),RestaurantActivity.class);
        inten.putExtra("nombreRestaurante",nombre);
        inten.putExtra("direccionRestaurante",direccion);
        inten.putExtra("placeId",id);
        startActivity(inten);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstaceState){
        super.onViewCreated(view,savedInstaceState);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void startCamera(View view){
        //Creamos el Intent para llamar a la Camara
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //Lanzamos la aplicacion de la camara con retorno (forResult)
        //startActivityForResult(cameraIntent, 1);
        startActivity(cameraIntent);
    }
/*


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("CONTEXT",context.toString());
        if (context instanceof onLoginFragmentInteractionListener) {
            mListener = (onLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onLoginFragmentInteractionListener");
        }
    }
*/

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isLocationServiceEnabled(){
        LocationManager locationManager = null;
        boolean gps_enabled= false,network_enabled = false;

        if(locationManager ==null)
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
