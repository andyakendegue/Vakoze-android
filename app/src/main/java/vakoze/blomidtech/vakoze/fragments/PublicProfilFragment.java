package vakoze.blomidtech.vakoze.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import vakoze.blomidtech.vakoze.LoginActivity;
import vakoze.blomidtech.vakoze.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PublicProfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PublicProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicProfilFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentTabHost mTabHost;
    private TextView textNom, textPrenom, textEmail, textPhone, nbrFan, nbrAbo, typeAffichage;
    private ImageView profile_pic;
    private Button btnModify;

    public PublicProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublicProfilFragment newInstance(String param1, String param2) {
        PublicProfilFragment fragment = new PublicProfilFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profilView = inflater.inflate(R.layout.fragment_profil, container, false);
        textNom = profilView.findViewById(R.id.textNom);
        textNom.setText("");
        textPrenom = profilView.findViewById(R.id.textPrenom);
        textPrenom.setText("");
        textEmail = profilView.findViewById(R.id.textEmail);
        textEmail.setText("");
        textPhone = profilView.findViewById(R.id.textPhone);
        textPhone.setText("");
        nbrAbo = profilView.findViewById(R.id.nbrAbo);
        nbrAbo.setText("");
        nbrFan = profilView.findViewById(R.id.nbrFan);
        nbrFan.setText("");
        typeAffichage = profilView.findViewById(R.id.typeAffichage);
        typeAffichage.setText("");
        profile_pic = profilView.findViewById(R.id.profile_pic);
        //profile_pic.setBackgroundResource(R.drawable.com_facebook_profile_picture_blank_square);
        profile_pic.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        btnModify = profilView.findViewById(R.id.btnModified);
        btnModify.setText("Se connecter");
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifyProfile = new Intent(getActivity(), LoginActivity.class);
                startActivity(modifyProfile);
            }
        });
        return profilView;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop(){
        super.onStop();
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
