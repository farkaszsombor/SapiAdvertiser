package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ro.sapientia.ms.sapiadvertiser.Model.Advertisement;
import ro.sapientia.ms.sapiadvertiser.Model.User;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.AdImageAdapter;


public class DetailsFragment extends Fragment {

    private static final String TAG = DetailsFragment.class.getSimpleName();

    private ViewPager pager;
    private TextView longDescriptionView,titleView,phoneView,locationView;
    private Advertisement advertisement;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance() {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            advertisement = getArguments().getParcelable("Advertisement");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        setUpWidgets(v);
        populateWidgets();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setUpWidgets(@NonNull View v){

        pager = v.findViewById(R.id.viewpager);
        longDescriptionView = v.findViewById(R.id.long_description);
        titleView = v.findViewById(R.id.titleView);
        phoneView = v.findViewById(R.id.phoneView);
        locationView = v.findViewById(R.id.locationView);
    }

    private void populateWidgets(){

        FirebaseDatabase.getInstance().getReference().child("users").child(advertisement.getCreatorID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                advertisement.setPhoneNumber(Objects.requireNonNull(user).getPhoneNum());
                ArrayList<String> images = new ArrayList<>(advertisement.getImages().values());
                AdImageAdapter adImageAdapter = new AdImageAdapter(getActivity(), images);
                pager.setAdapter(adImageAdapter);
                longDescriptionView.setText(advertisement.getLongDescription());
                titleView.setText(advertisement.getTitle());
                phoneView.setText(advertisement.getPhoneNumber());
                locationView.setText(advertisement.getLocation());
                Log.e(TAG,"Phone num acquired!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Phone num not acquired!");
            }
        });
    }
}
