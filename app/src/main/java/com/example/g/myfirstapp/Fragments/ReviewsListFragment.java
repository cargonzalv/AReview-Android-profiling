package com.example.g.myfirstapp.Fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.g.myfirstapp.DetailReviewActivity;
import com.example.g.myfirstapp.R;
import com.example.g.myfirstapp.Classes.Review;
import com.example.g.myfirstapp.ReviewListAdapter;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;



public class ReviewsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View mView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Review> lista;
    private ListView listReviews;

    public ReviewsListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lista = new ArrayList<Review>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_reviews_list, container, false);

        listReviews = (ListView) mView.findViewById(R.id.lv_listReviews);
        final ReviewListAdapter reviewListAdapter = new ReviewListAdapter(getContext(),lista);
        //asigno al listvView el adapter para mostrar
        listReviews.setAdapter(reviewListAdapter);

        //manejo los eventos al dar un click en un review del listview
        listReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //>> obtengo el review seleccionado
                Review res = (Review) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(),DetailReviewActivity.class);
                intent.putExtra("review",res);
                startActivity(intent);
            }
        });
        return mView;
    }

    public void updateReviews(ArrayList<Review> reviews)
    {
        lista=reviews;
        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(getContext(),lista);
        //asigno al listvView el adapter para mostrar
        listReviews.setAdapter(reviewListAdapter);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
