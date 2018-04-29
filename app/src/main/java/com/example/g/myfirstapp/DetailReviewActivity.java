package com.example.g.myfirstapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.g.myfirstapp.Classes.Review;
import com.squareup.picasso.Picasso;

public class DetailReviewActivity extends AppCompatActivity {

    private Review reviewToShow;
    private TextView tv_nameUser;
    private TextView tv_namePlace;
    private ImageView iv_photoReview;
    private TextView tv_nameDish;
    private RatingBar ratingBar;
    private TextView tv_comment;
    private TextView tv_tTags;
    private TextView tv_Ambient;
    private TextView tv_Presentation;
    private TextView tv_Service;
    private TextView tv_Taste;
    private TextView tv_Temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_review);

        //intancia de los elementos graficos
        tv_nameUser = findViewById(R.id.tv_detail_nameUser);
        tv_namePlace = findViewById(R.id.tv_detail_namePlace);
        iv_photoReview = findViewById(R.id.iv_detailA_photoReview);
        tv_nameDish = findViewById(R.id.tv_detail_nameDish);
        ratingBar = findViewById(R.id.rb_detail);
        tv_comment = findViewById(R.id.tv_detail_comment);
        tv_tTags = findViewById(R.id.tv_detail_tTags);
        tv_Ambient = findViewById(R.id.tv_detail_Ambient);
        tv_Presentation = findViewById(R.id.tv_detail_Presentation);
        tv_Service = findViewById(R.id.tv_detail_Service);
        tv_Taste = findViewById(R.id.tv_detail_Taste);
        tv_Temperature = findViewById(R.id.tv_detail_Temperature);

        //obtengo el review seleccionado
        reviewToShow = getIntent().getExtras().getParcelable("review");
        renderView(reviewToShow);
    }

    //muestra la informacion del review en el .xml
    private void renderView(Review reviewToShow) {

        tv_nameUser.setText(reviewToShow.getUsername());
        tv_namePlace.setText(reviewToShow.getPlaceName());
        Picasso.with(this)
                .load(reviewToShow.getDishPhotoURL())
                .fit()
                .centerInside()
                .into(iv_photoReview);
        tv_nameDish.setText(reviewToShow.getDishName());
        tv_comment.setText(reviewToShow.getComment());

        Float rat = reviewToShow.getRating();
        ratingBar.setRating(rat);
        setTitleTags(rat);

        String tags = reviewToShow.getTags();        //>>manejo de los tags
        tags = tags.substring(1, tags.length()-1);   //remove curly brackets
        Log.e("TAGS",tags);
        if(tags.contains(",")){                      //there are some tags
            String[] cadena = tags.split(",");
            for(String pair : cadena){ highlightText(pair); }
        }
        else{ highlightText(tags); }                 //there is one tag
    }

    private void setTitleTags(Float rat) {
        if(rat<5){
            tv_tTags.setText("They could improve:");
        }
    }

    //modifica el texto de los tags seleccionados en el review
    public void highlightText(String cad){
        String[] cadena = cad.split("=");
        if(cadena[0].contains("Ambient")){tv_Ambient.setTypeface(null, Typeface.BOLD); tv_Ambient.setTextColor(Color.parseColor("#0277bd")); tv_Ambient.setTextSize(17);}
        if(cadena[0].contains("Presentation")){tv_Presentation.setTypeface(null, Typeface.BOLD); tv_Presentation.setTextColor(Color.parseColor("#0277bd")); tv_Presentation.setTextSize(17);}
        if(cadena[0].contains("Service")){tv_Service.setTypeface(null, Typeface.BOLD); tv_Service.setTextColor(Color.parseColor("#0277bd")); tv_Service.setTextSize(17);}
        if(cadena[0].contains("Taste")){tv_Taste.setTypeface(null, Typeface.BOLD); tv_Taste.setTextColor(Color.parseColor("#0277bd")); tv_Taste.setTextSize(17);}
        if(cadena[0].contains("Temperature")){tv_Temperature.setTypeface(null, Typeface.BOLD); tv_Temperature.setTextColor(Color.parseColor("#0277bd")); tv_Temperature.setTextSize(17);}
    }

}
