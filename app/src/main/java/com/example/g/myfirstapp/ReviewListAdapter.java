package com.example.g.myfirstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.g.myfirstapp.Classes.Review;

import java.util.List;

/**
 * Created by Sneider on 20/04/2018.
 */

public class ReviewListAdapter extends BaseAdapter {

    Context context;
    List<Review> listReviews;

    public ReviewListAdapter(Context context, List<Review> listReviews) {
        this.context = context;
        this.listReviews = listReviews;
    }

    @Override
    public int getCount() {
        return listReviews.size();
    }

    @Override
    public Object getItem(int i) { return listReviews.get(i); }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        //obtengo la referencia vinculo el layout con un item del listview
        View vista = view;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.item_list_reviewhome,null);

        //referencia a los campos del layout
        TextView userName = (TextView) vista.findViewById(R.id.tv_userNameH);
        TextView name_place_dish = (TextView) vista.findViewById(R.id.tv_plcae_dish);
        final ImageView imageView = (ImageView) vista.findViewById(R.id.iv_photoReview);
        RatingBar ratingBar = (RatingBar) vista.findViewById(R.id.ratingBarHome);





        //obtener el elemento de la pos actual
        userName.setText(listReviews.get(i).getUsername());
        name_place_dish.setText(listReviews.get(i).getPlaceName()+" * "+listReviews.get(i).getDishName());
        ratingBar.setRating(listReviews.get(i).getRating());

        Glide.with(context)
                .load(listReviews.get(i).getDishPhotoURL())
                .override(1000, 500) // resizes the image to these dimensions (in pixel)
                .centerCrop() // t
                .into(imageView);
        return vista;
    }
}
