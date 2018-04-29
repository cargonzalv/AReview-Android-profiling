package com.example.g.myfirstapp.Fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g.myfirstapp.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    View mView;

    private OnFragmentInteractionListener mListener;
    int PLACE_PICKER_REQUEST = 1;
    private TextView namePlaceLocation;
    private EditText dishName;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private ImageView photoDish;
    Bitmap bmp;
    Uri uriPhoto;
    private String idPlace;
    private String addressPlace;

    public ReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_review, container, false);

        namePlaceLocation = (TextView) mView.findViewById(R.id.tv_location);
        dishName = (EditText) mView.findViewById(R.id.et_dishName);
        photoDish = (ImageView) mView.findViewById(R.id.iv_photoDish);
        Button btn_next = (Button) mView.findViewById(R.id.btn_continue);
        Button btn_photo = (Button) mView.findViewById(R.id.btn_photo);
        Button btn_location = (Button) mView.findViewById(R.id.btn_location);

        //>>>>Escucho la accion de los botones

        //accion para el boton NEXT
        btn_next.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if(dishName!=null && photoDish!=null && namePlaceLocation!=null && bmp!=null){
              //llamo el fragmento comment y paso los datos que selecciono el usuario.
              CommentFragment commentFragment = new CommentFragment();
              FragmentManager manager = getFragmentManager();
              Bundle bundle = new Bundle();
              bundle.putString("place",namePlaceLocation.getText().toString());
              bundle.putString("dish",dishName.getText().toString());
              bundle.putString("address",addressPlace);
              bundle.putString("placeId",idPlace);

              ByteArrayOutputStream stream = new ByteArrayOutputStream();
              bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
              byte[] byteArray = stream.toByteArray();

              bundle.putByteArray("photo",byteArray);

              commentFragment.setArguments(bundle);

              //hago la transicion del fragmento
              manager.beginTransaction()
                      .replace(R.id.fragment_container,commentFragment,commentFragment.getTag())
                      .commit();
              }
              else{
                  Toast.makeText(getActivity(),"Complete all requirements for do a review.",Toast.LENGTH_LONG).show();
              }
          }
         });

        //accion para el boton PHOTO
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        //accion para el boton LOCATION
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        return mView;
    }

    /*Sneider*/
    /*change text in tv_location*/
    /*this gets the place that the user chooses*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                idPlace = place.getId();
                addressPlace = place.getAddress().toString();
                namePlaceLocation.setText(place.getName());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bmp = imageBitmap;
            photoDish.setImageBitmap(imageBitmap);
            uriPhoto = data.getData();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /*Metodo para abrir la camara*/
    public void startCamera(View view){
        //Creamos el Intent para llamar a la Camara
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //Lanzamos la aplicacion de la camara con retorno (forResult)
        //startActivityForResult(cameraIntent, 1);
        startActivity(cameraIntent);
    }
}
