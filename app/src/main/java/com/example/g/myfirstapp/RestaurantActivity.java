package com.example.g.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.g.myfirstapp.Classes.Review;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RestaurantActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private TextView mTextMessage;
    private TextView tvInfo;
    private TextView tvDireccion;
    String idPlace;
    Place currentPlace;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseFirestore db;
    private CollectionReference reviewsRef;
    ListView listReviews;
    ListView listMenus;
    ArrayList<Review> lista;
    private double average;
    private TextView mTextPhone;
    private RatingBar ratingBar_review;
    private TextView mTextWebPage;
    private TextView mTextPriceLevel;
    private GeoDataClient mGeoDataClient;
    private ImageView mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mGoogleApiClient = new GoogleApiClient.Builder
                (this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this,this).build();
        //instan para hacer consulta a FireBase
        db = FirebaseFirestore.getInstance();
        mGoogleApiClient.connect();

        mGeoDataClient= Places.getGeoDataClient(this, null);
        idPlace = getIntent().getStringExtra("placeId");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Log.i("TagAdecuado", "Entra al onCreate "+idPlace);
        Log.i("TagAdecuado", "Entra al mGoogleApiClient "+mGoogleApiClient);

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, idPlace).setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        Log.i("TagAdecuado", "result: " + places);

                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            currentPlace = myPlace;
                            Log.i("TagAdecuado", "Place found: " + myPlace.getName());
                            updateTabInfo(myPlace);
                        } else {
                            Log.e("TagAdecuado", "Place not found");
                        }
                        places.release();
                    }
                });
        getPlace(mGoogleApiClient,idPlace);
        //obtengo los reviews del place realizados en la aplicacion
        getReviews(idPlace);

        //>>>manejo de la lista de reviews
        listReviews = (ListView) findViewById(R.id.lv_reviews);
        lista = new ArrayList<Review>();
        ReviewAdapter reviewAdapter = new ReviewAdapter(getApplicationContext(),lista);
           //asigno al listvView el adapter para mostrar
        listReviews.setAdapter(reviewAdapter);

        //>>>manejo de la lista de menus
        listMenus = (ListView) findViewById(R.id.lv_menus);
        MenuAdapter menuAdapter = new MenuAdapter(getApplicationContext(),lista);
            //asigno al listvView el adapter para mostrar
        listMenus.setAdapter(menuAdapter);
        eventslistMenus(listMenus);

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextPhone = (TextView) findViewById(R.id.tv_valuePhone);
        mTextWebPage = (TextView) findViewById(R.id.tv_valueQueue);
        mTextPriceLevel = (TextView) findViewById(R.id.tv_valueSchedule);
        mImage = (ImageView) findViewById(R.id.imageView);

        //asigno el nombre del restaurante que me pasa el FragmentPlaces
        tvInfo = (TextView)findViewById(R.id.tv_infoRestaurante);

        ratingBar_review = (RatingBar)findViewById(R.id.ratingBar_review);
        String nombreRestaurante = getIntent().getStringExtra("nombreRestaurante");
        //String nombreRestaurante = currentPlace.getName().toString();
        tvInfo.setText(nombreRestaurante);

        //asigno la direccion del restaurante que me pasa el FragmentPlaces
        tvDireccion = (TextView)findViewById(R.id.tv_direccion);
        String direccionRestaurante = getIntent().getStringExtra("direccionRestaurante");

        tvDireccion.setText(direccionRestaurante);

        //TabHost
        TabHost th = (TabHost)findViewById(R.id.tabHost);

        //>>tab INFO
        th.setup();
        TabHost.TabSpec spec1=th.newTabSpec("Info");
        spec1.setIndicator("Information");
        spec1.setContent(R.id.info);
        th.addTab(spec1);

        //>>tab MENU
        th.setup();
        TabHost.TabSpec spec2=th.newTabSpec("Menu");
        spec2.setIndicator("Menu");
        spec2.setContent(R.id.menu);
        th.addTab(spec2);

        //>>tab REVIEW
        th.setup();
        TabHost.TabSpec spec3=th.newTabSpec("Reviews");
        spec3.setIndicator("reviews");
        spec3.setContent(R.id.reviews);
        th.addTab(spec3);

    }

    private void eventslistMenus(ListView listMenus) {
        listMenus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Review re = (Review) adapterView.getItemAtPosition(i);
                String nomPlato = re.getDishName();

                //ir a la actividad que contiene mas info del plato seleccionado
                Intent inten = new Intent(getApplicationContext(),DishActivity.class);
                inten.putExtra("idRestaurant",idPlace);
                inten.putExtra("dishName",nomPlato);
                inten.putExtra("urlImage",re.getDishPhotoURL());
                inten.putExtra("ratingDish",re.getRating());
                startActivity(inten);

                Log.e("PIC",">nombre plato: "+nomPlato+" id p:"+idPlace);
            }
        });
    }

    private void disconnect()
    {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        disconnect();
        super.onStop();
    }

    private void updateTabInfo(Place myPlace) {
        String webSite="not found";
        if(myPlace.getWebsiteUri()!=null)
        {
            webSite = myPlace.getWebsiteUri().toString();
        }
        mTextWebPage.setText(webSite );
        mTextPhone.setText(myPlace.getPhoneNumber());
        mTextPriceLevel.setText(""+myPlace.getPriceLevel());
        getPhotos(idPlace);
    }

    // Request photos and metadata for the specified place.
    private void getPhotos(String placeId) {

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                if(photoMetadataBuffer.getCount()>0)
                {

                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    //CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    //Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getScaledPhoto(photoMetadata,5,10);
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            mImage.setImageBitmap(bitmap);
                            photoMetadataBuffer.release();
                            mGoogleApiClient.disconnect();

                        }
                    });
                }

            }
        });

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.e("TagAdecuado", "onConnectionFailed: "+result.getErrorMessage());
    }

    //Obtengo el PlACE SEGUN EL ID
    public void getPlace(GoogleApiClient client, String id){

        Places.GeoDataApi.getPlaceById(client, id)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            currentPlace = myPlace;
                            Log.i(">>>NOMBRE LUGAR", "Place found: " + myPlace.getName());
                        } else {
                            Log.e(">>>NO SE ENCONTRO LUGAR", "Place not found");
                        }
                        places.release();
                    }
                });
    }

    //Obtengo los reviews del lugar con ID
    public void getReviews(String id){
        db.collection("reviews")
                .whereEqualTo("placeId",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double sum =0;
                            for (DocumentSnapshot document : task.getResult()) {
                                Map review = document.getData();
                                Review reviewAdd = new Review(review);
                                lista.add(reviewAdd);

                                Log.d(">>>>>>>>>>>documento", document.getId() + " => " + document.getData());
                                Log.d(">>>>>>>>>>>documento", "DATA REVIEW => " + reviewAdd.getUsername()+" "+ reviewAdd.getRating()+" "+reviewAdd.getComment());

                                }
                                double average =(double) sum/lista.size();
                            ratingBar_review.setRating((float) average);
                        }
                        else {
                            Log.d(">>>>>>>>>>>documento", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
