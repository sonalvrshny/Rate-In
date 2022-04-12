package edu.neu.madproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button catBtn, feedBtn, youBtn;

    public NavFragment() {
        super(R.layout.fragment_nav);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavFragment newInstance(String param1, String param2) {
        NavFragment fragment = new NavFragment();
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
        View view = inflater.inflate(R.layout.fragment_nav, container, false);
        catBtn = view.findViewById(R.id.cat_btn);
        feedBtn = view.findViewById(R.id.feeds_btn);
        youBtn = view.findViewById(R.id.you_btn);

        catBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CategoriesActivity.class));
            if(getActivity().getClass().equals(CategoriesActivity.class)) getActivity().finish();
        });
        feedBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FeedActivity.class));
            if(getActivity().getClass().equals(FeedActivity.class)) getActivity().finish();
        });
//        youBtn.setOnClickListener(v -> {
//            startActivity(new Intent(getActivity(), CategoriesActivity.class));
//        });
        return view;
    }
}