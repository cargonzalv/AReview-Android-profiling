package com.example.g.myfirstapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.g.myfirstapp.Classes.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class DishActivity extends AppCompatActivity {

    ListView listViewReviews;
    ArrayList<Review> listReviews;
    private FirebaseFirestore db;
    private RecyclerView rv_fotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_dish);

        //>datos del RestaurantActivity
        String nomPlato = getIntent().getStringExtra("dishName");
        String idRestaurant = getIntent().getStringExtra("idRestaurant");
        String urlPhoto = getIntent().getStringExtra("urlImage");
        Float ratingDish = getIntent().getFloatExtra("ratingDish",0);
        listReviews = new ArrayList<Review>();
        getReviews(idRestaurant,nomPlato);

        //asigno la foto del primer plato
        ImageView imageDishMain = (ImageView) findViewById(R.id.iv_dishPhotoMain);
        Picasso.with(this)
                .load(urlPhoto)
                .fit()
                .centerInside()
                .into(imageDishMain);
        //asigno nombre del plato
        TextView nombrePlato = (TextView) findViewById(R.id.tv_nameDish);
        nombrePlato.setText(nomPlato);

        //asigno el rating del plato
        RatingBar rat = (RatingBar) findViewById(R.id.ratingBarDish);
        rat.setRating(ratingDish);

        //>manejo la lista de los reviews del plato
        listViewReviews = (ListView) findViewById(R.id.lv_rvwsDish);
        ReviewAdapter reviewAdapter = new ReviewAdapter(getApplicationContext(),listReviews);
        listViewReviews.setAdapter(reviewAdapter);

        //>manejo de la lista de fotos
        rv_fotos = (RecyclerView) findViewById(R.id.rv_photos);
        PhotosAdapter photosAdapter = new PhotosAdapter(this);
        rv_fotos.setAdapter(photosAdapter);
        rv_fotos.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        rv_fotos.setLayoutManager(layoutManager);
        photosAdapter.addListPhotos(listReviews);

        //>>manejo del tabHost
        TabHost th = (TabHost)findViewById(R.id.tabHostDish);
         //tab PHOTOS
        th.setup();
        TabHost.TabSpec spec1 = th.newTabSpec("Photos");
        spec1.setIndicator("Photos");
        spec1.setContent(R.id.photos);
        th.addTab(spec1);
         //tab REVIEWS
        th.setup();
        TabHost.TabSpec spec2 = th.newTabSpec("Reviews");
        spec2.setIndicator("Reviews");
        spec2.setContent(R.id.reviews);
        th.addTab(spec2);
    }

    //obtengo los reviews del plato seleccionado del resturante que se esta observando
    public void getReviews(String idPlace, String nameDish){
        db.collection("reviews")
                .whereEqualTo("placeId",idPlace)
                .whereEqualTo("dishName",nameDish)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot document : task.getResult()){
                                Map review = document.getData();
                                Review reviewAdd = new Review(review);
                                listReviews.add(reviewAdd);

                                Log.d(">>>>>>>>>>>documento", document.getId() + " => " + document.getData());
                                Log.d(">>>>>>>>>>>documento", "DATA REVIEW OF DISH => " + reviewAdd.getUsername()+" "+ reviewAdd.getRating()+" "+reviewAdd.getComment());
                            }
                        }
                        else {
                            Log.d(">Reviews dish", "Error getting reviews of a dish: ", task.getException());
                        }
                    }
                });
    }
}
