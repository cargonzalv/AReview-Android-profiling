package com.example.g.myfirstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.g.myfirstapp.Classes.Review;

import java.util.List;

/**
 * Created by Sneider on 20/04/2018.
 */

public class MenuAdapter extends BaseAdapter {

    Context context;
    List<Review> listReviews;

    public MenuAdapter(Context context, List<Review> listReviews) {
        this.context = context;
        this.listReviews = listReviews;
    }

    @Override
    public int getCount() { return listReviews.size();}

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
        vista = inflate.inflate(R.layout.item_list_menu,null);

        //referencia a los campos del layout
        TextView nameMenu = (TextView) vista.findViewById(R.id.tv_menuName);
        TextView ratingMenu = (TextView) vista.findViewById(R.id.tv_menuRating);

        //obtener el elemento de la pos actual
        nameMenu.setText(listReviews.get(i).getDishName());
        ratingMenu.setText(listReviews.get(i).getRating()+"/5.0");

        return vista;
    }
}
