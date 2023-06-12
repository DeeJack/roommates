package it.unitn.disi.fumiprovv.roommates.fragments.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseCreatedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseCreatedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String houseId;

    public HouseCreatedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HouseCreatedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseCreatedFragment newInstance(String param1, String param2) {
        HouseCreatedFragment fragment = new HouseCreatedFragment();
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
        View view = inflater.inflate(R.layout.fragment_house_created, container, false);
        Button homeBtn = view.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener((a) -> onHomeButtonClick(view));
        Bundle bundle = getArguments();
        String houseName = bundle.getString("houseName");
        this.houseId = bundle.getString("houseId");

        TextView houseNameTextView = view.findViewById(R.id.houseNameField);
        houseNameTextView.setText(houseName);
        TextView houseIdTextView = view.findViewById(R.id.houseIdField);
        houseIdTextView.setText(houseId);
        ImageView shareBtn = view.findViewById(R.id.shareButton);
        shareBtn.setOnClickListener((v) -> onShareClick());
        return view;
    }

    public void onShareClick() {
        String text = getString(R.string.share_code_text)
                .replace("{code}", houseId)
                .replace("{link}", "http://roommates.asd/join?code=" + houseId)
                .replace("\\n", "\n");

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_code_title));
        startActivity(shareIntent);
    }

    public void onHomeButtonClick(View view) {
        NavigationUtils.navigateTo(R.id.action_houseCreatedFragment_to_homeFragment, view);
    }
}