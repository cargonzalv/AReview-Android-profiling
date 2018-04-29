package com.example.g.myfirstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.g.myfirstapp.Classes.Review;

import java.util.List;

/**
 * Created by Sneider on 20/04/2018.
 */

public class ReviewAdapter extends BaseAdapter{

    Context context;
    List<Review> listReviews;

    public ReviewAdapter(Context context, List<Review> listReviews) {
        this.context = context;
        this.listReviews = listReviews;
    }

    @Override
    public int getCount() {
        return listReviews.size();
    }

    @Override
    public Object getItem(int i) {
        return listReviews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //obtengo la referencia vinculo el layout con un item del listview
        View vista = view;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.item_list_review,null);

        //referencia a los campos del layout
        TextView userName = (TextView) vista.findViewById(R.id.tv_nameUser);
        RatingBar ratingBar = (RatingBar) vista.findViewById(R.id.ratingBar_review);
        TextView comment = (TextView) vista.findViewById(R.id.tv_comment);

        //obtener el elemento de la pos actual
        userName.setText(listReviews.get(i).getUsername());
        ratingBar.setRating(listReviews.get(i).getRating());
        comment.setText(listReviews.get(i).getComment());

        return vista;
    }
}
