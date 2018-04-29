package com.example.g.myfirstapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.g.myfirstapp.Classes.Review;

import java.util.ArrayList;

/**
 * Created by Sneider on 26/04/2018.
 */

class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder>{

    private ArrayList<Review> dataset;
    private Context context;

    public PhotosAdapter(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_photos,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review revi = dataset.get(position);
        Glide.with(context)
                .load(revi.getDishPhotoURL())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.photoIv);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void addListPhotos(ArrayList<Review> listReviews) {
        dataset.addAll(listReviews);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView photoIv;
        public  ViewHolder(View itemView){
            super (itemView);
            photoIv = itemView.findViewById(R.id.iv_photo);
        }
    }
}
