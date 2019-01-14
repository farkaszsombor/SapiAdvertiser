package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import ro.sapientia.ms.sapiadvertiser.Model.Advertisement;
import ro.sapientia.ms.sapiadvertiser.Model.User;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.AdImageAdapter;


public class DetailsFragment extends Fragment {

    private static final String TAG = DetailsFragment.class.getSimpleName();

    private ViewPager pager;
    private TextView longDescriptionView,titleView,phoneView,locationView,dateView;
    private ImageView reportButton,shareButton,leftArrow,rightArrow;
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
        reportAdvertisement();
        shareAdvertisement();
        handleViewpagerNavigation();
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
        reportButton = v.findViewById(R.id.reportAdd);
        shareButton = v.findViewById(R.id.shareAdd);
        dateView = v.findViewById(R.id.date_of_ad);
        leftArrow = v.findViewById(R.id.left_arrow);
        rightArrow = v.findViewById(R.id.right_arrow);
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

                Date date = new Date(advertisement.getTimeStamp());
                DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateFormatted = formatter.format(date);
                dateView.setText(dateFormatted);
                Log.d(TAG,"Phone num acquired!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Phone num not acquired!");
            }
        });
    }

    private void reportAdvertisement(){

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure to report this advertisement?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("advertismenets").child(advertisement.getAdID()).child("isReported").setValue(true);
                                Toast.makeText(getContext(),"Advertisement reported!",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void shareAdvertisement(){

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, advertisement.getTitle() + "\n\n" + advertisement.getShortDescription() + "\n\n" + advertisement.getFirstImage());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Send to: "));
            }
        });
    }

    private void handleViewpagerNavigation(){

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tab = pager.getCurrentItem();
                if(tab > 0){
                    tab--;
                    pager.setCurrentItem(tab);
                }
                else{
                    pager.setCurrentItem(tab);
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tab = pager.getCurrentItem();
                tab++;
                pager.setCurrentItem(tab);
            }
        });
    }
}
