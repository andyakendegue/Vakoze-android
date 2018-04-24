package com.vakoze.video.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shuhart.stepview.StepView;
import com.vakoze.R;
import com.vakoze.video.adapter.FragmentEffectListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideoEffectsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoEffectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoEffectsFragment extends Fragment implements FragmentEffectListAdapter.OnItemClicked {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View v;
    private List<String> filterList;
    TypedArray imageList;
    private FragmentEffectListAdapter adapter;
    private RecyclerView recyclerView;

    public VideoEffectsFragment() {
        // Required empty public constructor
        this.filterList= new ArrayList<String>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoEffectsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoEffectsFragment newInstance(String param1, String param2) {
        VideoEffectsFragment fragment = new VideoEffectsFragment();
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
        /*String source = getArguments().getString("source");
        Long user_id = getArguments().getLong("user_id");
        String nom = getArguments().getString("nom");
        String tags = getArguments().getString("tags");
        String categorie = getArguments().getString("categorie");
        Long video_id = getArguments().getLong("id");
        String type = getArguments().getString("type");*/
        v = inflater.inflate(R.layout.fragment_video_effects, container, false);

        Context context = v.getContext();

        // Set the adapter
        recyclerView = v.findViewById(R.id.effectList);
        //recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 36));

        /*
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new MyvideoRecyclerViewAdapter(Video.ITEMS, mListener));

        }*/
        filterList = Arrays.asList(getResources().getStringArray(R.array.liste_effets));
        imageList= getResources().obtainTypedArray(R.array.effects_img);;
        adapter = new FragmentEffectListAdapter(getActivity(), filterList,imageList);
        adapter.setOnClick(this);
        recyclerView.setAdapter(adapter);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onItemClick(String effect) {


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
        void onFragmentInteraction(int uri);
    }
}
