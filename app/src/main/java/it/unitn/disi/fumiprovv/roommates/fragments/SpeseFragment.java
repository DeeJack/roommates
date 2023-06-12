package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Note;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SpeseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeseFragment newInstance(String param1, String param2) {
        SpeseFragment fragment = new SpeseFragment();
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
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle("Spese");

        FragmentContainerView fragmentContainerView = view.findViewById(R.id.fragmentContainerView);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        SpeseComuniFragment speseComuniFragment = new SpeseComuniFragment();
        SpeseStorico speseStoricoFragment = new SpeseStorico();
        SpeseSituazioneFragment speseSituazioneFragment = new SpeseSituazioneFragment();
        SpeseComuniFragment prova = new SpeseComuniFragment();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getChildFragmentManager().beginTransaction().replace(fragmentContainerView.getId(), speseSituazioneFragment).commit();
                        break;
                    case 1:
                        getChildFragmentManager().beginTransaction().replace(fragmentContainerView.getId(), prova).commit();
                        break;
                    case 2:
                        getChildFragmentManager().beginTransaction().replace(fragmentContainerView.getId(),speseStoricoFragment).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getChildFragmentManager().beginTransaction().replace(fragmentContainerView.getId(), speseSituazioneFragment).commit();

        return view;
    }
}