package com.example.g.myfirstapp.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g.myfirstapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    View mView;
    String namePlace;
    String nameDish;
    String idPlace;
    String addressPlace;
    Bitmap bmp;
    CheckBox cb_taste;
    CheckBox cb_presentation;
    CheckBox cb_service;
    CheckBox cb_ambient;
    CheckBox cb_temperature;
    EditText et_comment;
    Button btn_submit;
    private StorageReference mStorageRef;
    private OnFragmentInteractionListener mListener;
    FirebaseUser user;
    FirebaseFirestore db;

    public CommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_comment, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        final RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar_starts);
        final TextView estado = (TextView) mView.findViewById(R.id.tv_title_comment);
        final TextView mensaje = (TextView)mView.findViewById(R.id.tv_msg_comment);
        cb_taste = (CheckBox) mView.findViewById(R.id.cb_taste);
        cb_presentation = (CheckBox) mView.findViewById(R.id.cb_presentation);
        cb_service = (CheckBox) mView.findViewById(R.id.cb_service);
        cb_ambient = (CheckBox) mView.findViewById(R.id.cb_ambient);
        cb_temperature = (CheckBox) mView.findViewById(R.id.cb_temperature);
        et_comment = (EditText) mView.findViewById(R.id.et_commentUser);
        btn_submit = (Button) mView.findViewById(R.id.btn_submit);

        //manejo de textos cuando se mueve el rating
        ratingBar.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                        //if(v==0){estado.setText("");}
                        if(v==1){estado.setText("Awful"); mensaje.setText("They could improve");}
                        if(v==2){estado.setText("Bad"); mensaje.setText("They could improve");}
                        if(v==3){estado.setText("Average"); mensaje.setText("They could improve");}
                        if(v==4){estado.setText("Good"); mensaje.setText("They could improve");}
                        if(v==5){estado.setText("Great"); mensaje.setText("I really enjoyed");}
                    }
                }
        );

        //manejo de Submit
        final Bundle bundle = this.getArguments();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"We are saving your information.",Toast.LENGTH_LONG).show();
                //deshabilito el boton mientras se procesa la info del review
                btn_submit.setEnabled(false);
                //datos del fragment reviewFragment
                if (bundle != null) {
                    namePlace = bundle.getString("place");
                    nameDish = bundle.getString("dish");
                    addressPlace = bundle.getString("address");
                    idPlace = bundle.getString("placeId");
                    byte[] byteArrayPhoto = bundle.getByteArray("photo");
                    bmp = BitmapFactory.decodeByteArray(byteArrayPhoto, 0, byteArrayPhoto.length);

                    //hago storage de la foto
                    mStorageRef = FirebaseStorage.getInstance().getReference().child("images/dishes/"+user.getUid()+"/"+bmp.toString());
                    UploadTask uploadTask = mStorageRef.putBytes(byteArrayPhoto);
                    uploadTask
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //cargo los datos al db
                            String commentUser = et_comment.getText().toString();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Float ratingUser = ratingBar.getRating();
                            String idUser = user.getUid();
                            String nameUser = user.getDisplayName();
                            Map checkedList = isChecked();
                            Date dateReviewComment = new Date();

                            Map<String,Object> review = new HashMap<>();
                            review.put("comment",commentUser);
                            review.put("createdAt",dateReviewComment);
                            review.put("dishName",nameDish);
                            review.put("dishPhotoURL",downloadUrl.toString());
                            review.put("placeAddress",addressPlace);
                            review.put("placeId",idPlace);
                            review.put("placeName",namePlace);
                            review.put("rating",ratingUser);
                            review.put("tags",checkedList);
                            review.put("userId", idUser);
                            review.put("userPhotoURL",null);
                            review.put("username",nameUser);

                            db.collection("reviews")
                                    .add(review)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getActivity(),"Thank you for your review.",Toast.LENGTH_LONG).show();
                                            Log.i(">>>>>>>>> ID DOCUMENT: ",documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(),"there was an error in storing the DATA Review.",Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"there was an error in storing IMAGE",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                //habilito el boton despues de haber guardado el submit
                btn_submit.setEnabled(true);
            }
        });

        return mView;
    }

    //Categorias seleccionadas por el usaurio
    public Map isChecked(){
            Map<String,Object> tags = new HashMap<>();
            if(cb_taste.isChecked()){tags.put("Taste",true);}
            if(cb_presentation.isChecked()){tags.put("Presentation",true);}
            if(cb_service.isChecked()){tags.put("Service", true);}
            if(cb_ambient.isChecked()){tags.put("Ambient",true);}
            if(cb_temperature.isChecked()){tags.put("Temperature",true);}
            return tags;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

/*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onLoginFragmentInteractionListener) {
            mListener = (onLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onLoginFragmentInteractionListener");
        }
    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
