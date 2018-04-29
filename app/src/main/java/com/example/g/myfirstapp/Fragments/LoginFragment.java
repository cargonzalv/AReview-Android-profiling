package com.example.g.myfirstapp.Fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.g.myfirstapp.AccountActivity;
import com.example.g.myfirstapp.Classes.UserFireBase;
import com.example.g.myfirstapp.R;
import com.example.g.myfirstapp.Classes.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Most rigth fragment
 *Used to:
 *-Check profile
 *-Add/Remove friends
 *-Accept/Reject friends requests
 */
public class LoginFragment extends Fragment implements View.OnClickListener, OnCompleteListener<Void>
{
    //Constants
    public static final String USERNAME = "username";
    public static final String IS_RESPONDING_REQUEST = "isRespondingRequest";
    public static final String IS_CONSULTING = "isConsulting";
    public static final String CURRENT_USER = "currentUser";
    public static final String ARE_FRIENDS = "areFriends";

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserFireBase currentUser;
    private UserFireBase userToRespond;

    //Variables to display info
    private String username;
    private String id;
    private String friendsCount;
    private String reviewCount;

    //Control variables
    private boolean isConsulting;
    private boolean isRespondingRequest;
    private boolean areFriends;


    private onLoginFragmentInteractionListener mCallback;

    //Collections
    private ArrayList<Review> reviews;
    private ArrayList<UserFireBase> friends;

    //Graphics components
    private View view;
    private Button btnLogOut;
    private Button btnEdit;
    private TextView text;
    private TextView reviewsText;
    private TextView friendsText;
    private ReviewsListFragment childListFragment;
    private Button btnReject;
    private String message;

    public LoginFragment()
    {/* Required empty public constructor*/}

    public void logOut()
    {
        Log.d("FirebaseAuth", "logOut: ");
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        reviews = new ArrayList<>();
        if (getArguments() != null)
        {
            userToRespond = (UserFireBase) getArguments().getSerializable(USERNAME);
            currentUser = (UserFireBase) getArguments().getSerializable(CURRENT_USER);
            if (userToRespond != null)
            {
                username = userToRespond.getUsername();
                id = userToRespond.getId();
                friendsCount = "" + userToRespond.getFriendCount();
                reviewCount = "" + userToRespond.getReviewCount();
                ;

            }
            else
            {
                username = currentUser.getUsername();
                id = currentUser.getUid();
                friendsCount = "" + currentUser.getFriendCount();
                reviewCount = "" + currentUser.getReviewCount();
            }
            isRespondingRequest = getArguments().getBoolean(IS_RESPONDING_REQUEST);
            isConsulting = getArguments().getBoolean(IS_CONSULTING);
            areFriends = getArguments().getBoolean(ARE_FRIENDS);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        //Init Components
        text = (TextView) view.findViewById(R.id.username);
        reviewsText = (TextView) view.findViewById(R.id.tv_numReviews);
        friendsText = view.findViewById(R.id.tv_nFriends);
        btnEdit = view.findViewById(R.id.buttonEdit);
        btnLogOut = (Button) view.findViewById(R.id.buttonSignOut);
        btnReject = (Button) view.findViewById(R.id.buttonReject);

        if (isRespondingRequest)
        {
            btnEdit.setText("Accept");
            //Missing other button
            ViewGroup.LayoutParams params = btnEdit.getLayoutParams();
            params.width = 200;
            btnEdit.setLayoutParams(params);
            btnReject.setVisibility(View.VISIBLE);
        }
        else if (isConsulting)
        {
            if (areFriends)
            {
                btnEdit.setText("Remove friend");
            }
            else
            {
                btnEdit.setText("Add friend");
            }
        }
        else
        {
            btnLogOut.setVisibility(View.VISIBLE);
        }

        btnEdit.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnReject.setOnClickListener(this);


        //manejo de la lista de reviews - llamo al fragmento que contiene la lista de reviews
        getReviews(id);
        childListFragment = new ReviewsListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmetListID, childListFragment).commit();

        //Set display items values
        reviewsText.setText(reviewCount);
        friendsText.setText(friendsCount);
        text.setText(username);

        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        //mCallback = null;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttonEdit:
                if (isRespondingRequest)
                {
                    Log.d("Acceptingh", "onClick: ");

                    //Accept
                    message="added";
                    db.collection("users").document(currentUser.getId())
                            .collection("incomingFriendRequests").document(userToRespond.getId()).update("accept", true)
                                .addOnCompleteListener(this);
                }
                else if (isConsulting)
                {
                    if (areFriends)
                    {
                        //Remove
                        message="removed";
                        db.collection("users").document(currentUser.getId())
                                .collection("friends").document(userToRespond.getId()).update("remove", true)
                                    .addOnCompleteListener(this);



                    }
                    else
                    {
                        //Add
                        message="added";
                        db.collection("users").document(currentUser.getId())
                                .collection("outgoingFriendRequests").document(userToRespond.getId()).set(userToRespond.getMap())
                                .addOnCompleteListener(this);

                       
                    }
                }

                break;

            case R.id.buttonSignOut:
                logOut();
                break;

            case R.id.buttonReject:
            //Reject
                message="rejected";
            db.collection("users").document(currentUser.getId())
                    .collection("incomingFriendRequests").document(userToRespond.getId()).update("accept", false)
                        .addOnCompleteListener(this);
            break;

            default:
                break;
        }
        mCallback.updateFriends();
    }

    @Override
    public void onComplete(@NonNull Task<Void> task)
    {

        message+=" successfully";
        if(!task.isSuccessful())
        {
            message+=" failed";
        }
        mCallback.toast(message+" "+userToRespond.getEmail());
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
    public interface onLoginFragmentInteractionListener
    {
        void updateFriends();

        void toast(String message);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try
        {
            mCallback = (onLoginFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement onHomeFragmentSelectedListener");
        }
    }

    public void getReviews(String id)
    {
        db.collection("reviews")
                .whereEqualTo("userId", id)
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
                                //getImage(reviewAdd);
                            }
                            childListFragment.updateReviews(reviews);
                        }
                        else
                        {
                            Log.d(">>>>>>>>>>>documento", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
