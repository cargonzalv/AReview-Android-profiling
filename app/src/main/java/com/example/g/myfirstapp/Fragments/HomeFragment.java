package com.example.g.myfirstapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.g.myfirstapp.Classes.UserFireBase;
import com.example.g.myfirstapp.R;
import com.example.g.myfirstapp.Classes.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


/**
 * Fragment most at left
 * Responsible for:
 * -Show friends requests
 * -Show friends reviews
 * -Find users (given a character)
 */
public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher, OnSuccessListener<QuerySnapshot>
{
    //Constants
    private static final String CURRENT_USER = "currentUser";

    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserFireBase user;
    private CollectionReference usersRef;

    private onHomeFragmentSelectedListener mCallback;
    //Graphics components
    private AutoCompleteTextView suggestion_box;
    private ArrayAdapter<UserFireBase> adapter;
    private TextView counter;
    private Button friends_button;
    private ReviewsListFragment childListFragment;
    private View mView;

    //Data
    private ArrayList<UserFireBase> users;
    private ArrayList<UserFireBase> requests;
    private ArrayList<Review> reviews;


    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        usersRef = db.collection("users");

        if (getArguments() != null)
        {
            user = (UserFireBase) getArguments().getSerializable(CURRENT_USER);
        }
        updateRequests();
        reviews = new ArrayList<>();
        users = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        //init graphic components
        suggestion_box = (AutoCompleteTextView) mView.findViewById(R.id.suggestion_box);
        friends_button = mView.findViewById(R.id.friends_button);
        counter = mView.findViewById(R.id.badge_notification_1);

        childListFragment = new ReviewsListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentList, childListFragment).commit();

        suggestion_box.addTextChangedListener(this);
        friends_button.setOnClickListener(this);
        suggestion_box.setOnItemClickListener(this);

        adapter = new ArrayAdapter<UserFireBase>(getActivity(), android.R.layout.simple_dropdown_item_1line, users);
        suggestion_box.setAdapter(adapter);
        suggestion_box.setThreshold(1);

        getReviews();
        return mView;
    }

    private void updateAutoCompletion(CharSequence name)
    {
        if (name.length() > 0)
        {
            String filter = name.toString().toLowerCase();
            char last = filter.charAt(filter.length() - 1);
            char newLast = (char) (last + 1);
            String lessThan = filter.substring(0, filter.length() - 1);
            String less = lessThan + "" + newLast;
            usersRef.whereGreaterThanOrEqualTo("email", filter)
                    .whereLessThan("email", less).
                    get()
                    .addOnSuccessListener(this);
        }

    }


    private void updateRequests()
    {
        if(usersRef!=null)
        {
            usersRef.document(user.getId())
                    .collection("incomingFriendRequests")
                    .get()
                    .addOnSuccessListener(
                            new OnSuccessListener<QuerySnapshot>()
                            {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                {
                                    requests = UserFireBase.getUsers(queryDocumentSnapshots);
                                    updateFriendsCount();
                                }
                            }
                    );
        }
    }

    private void updateFriendsCount()
    {
        counter.bringToFront();
        counter.setVisibility(View.VISIBLE);
        counter.setText("" + requests.size());
        counter.invalidate();
    }

    private void Update(QuerySnapshot queryDocumentSnapshots)
    {
        ArrayList<DocumentSnapshot> f = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

        UserFireBase tempUser;
        DocumentSnapshot doc;
        Log.d("UpdateQuery", "Update: " + f.size());
        adapter.clear();
        for (int i = 0; i < f.size(); i++)
        {
            doc = f.get(i);
            tempUser = new UserFireBase(doc);
            if (user != null && tempUser != null  && !tempUser.getId().equals(user.getId()))
            {
                adapter.add(tempUser);
            }
        }
        adapter.getFilter().filter(suggestion_box.getText(), null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.friends_button:
                if (requests != null)
                {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                    builderSingle.setIcon(R.drawable.ic_person_black_24dp);
                    builderSingle.setTitle("Theese are your friends requests:");

                    final ArrayAdapter<UserFireBase> arrayAdapter = new ArrayAdapter<UserFireBase>(getActivity(), android.R.layout.simple_dropdown_item_1line, requests);

                    builderSingle.setNegativeButton("Close", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            UserFireBase u = arrayAdapter.getItem(which);
                            mCallback.onUserSelected(u, true, false);
                        }
                    });
                    builderSingle.show();
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        UserFireBase u = (UserFireBase) parent.getItemAtPosition(position);
        mCallback.onUserSelected(u, false, true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {   }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){updateAutoCompletion(s);}

    @Override
    public void afterTextChanged(Editable s) {    }

    public void updateUser(UserFireBase currentUser)
    {
        user = currentUser;
        updateRequests();
    }

    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {Update(queryDocumentSnapshots);    }

    public interface onHomeFragmentSelectedListener
    {
        void onUserSelected(UserFireBase userFireBase, boolean isRespondingRequest, boolean isConsulting);
        void toast(String s);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (onHomeFragmentSelectedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()+ " must implement onHomeFragmentSelectedListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
    public void getReviews()
    {
        db.collection("reviews")
                .whereGreaterThan("visibleTo." + user.getId(), 0)
                .limit(5)
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
                                Map review = document.getData();
                                final Review reviewAdd = new Review(review);
                                reviews.add(reviewAdd);
                            }
                            childListFragment.updateReviews(reviews);
                        }
                        else
                        {
                            mCallback.toast("Check your internet connection");
                        }
                    }
                });
    }
}
